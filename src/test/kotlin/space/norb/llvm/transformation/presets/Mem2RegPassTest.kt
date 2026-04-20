package space.norb.llvm.transformation.presets

import org.junit.jupiter.api.Test
import space.norb.llvm.analysis.AnalysisManager
import space.norb.llvm.analysis.presets.DominatorTreeAnalysis
import space.norb.llvm.analysis.presets.PredecessorAnalysis
import space.norb.llvm.analysis.presets.UseDefAnalysis
import space.norb.llvm.builder.IRBuilder
import space.norb.llvm.enums.IcmpPredicate
import space.norb.llvm.instructions.memory.AllocaInst
import space.norb.llvm.instructions.memory.LoadInst
import space.norb.llvm.instructions.memory.StoreInst
import space.norb.llvm.instructions.other.PhiNode
import space.norb.llvm.structure.Module
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.IntegerType
import space.norb.llvm.types.PointerType
import space.norb.llvm.types.VoidType
import space.norb.llvm.values.constants.IntConstant
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class Mem2RegPassTest {
    private fun createAnalysisManager(module: Module): AnalysisManager =
        AnalysisManager(module).apply {
            register(PredecessorAnalysis)
            register(UseDefAnalysis)
            register(DominatorTreeAnalysis)
        }

    @Test
    fun `promotes straight-line stack slot`() {
        val module = Module("test")
        val builder = IRBuilder(module)
        val function = module.registerFunction("promote", FunctionType(IntegerType.I32, emptyList()))
        val entry = function.insertBasicBlock("entry")

        builder.positionAtEnd(entry)
        val slot = builder.insertAlloca(IntegerType.I32, "slot")
        builder.insertStore(IntConstant(40L, IntegerType.I32), slot)
        val loaded = builder.insertLoad(IntegerType.I32, slot, "loaded")
        val sum = builder.insertAdd(loaded, IntConstant(2L, IntegerType.I32), "sum")
        builder.insertRet(sum)

        Mem2RegPass.run(module, createAnalysisManager(module))

        assertFalse(module.toIRString().contains("alloca"))
        assertFalse(module.toIRString().contains("load"))
        assertFalse(module.toIRString().contains("store"))
        assertEquals(2, entry.instructions.size)
        ValidationPass.run(module, AnalysisManager(module))
    }

    @Test
    fun `inserts phi node at branch join`() {
        val module = Module("test")
        val builder = IRBuilder(module)
        val function = module.registerFunction("join", FunctionType(IntegerType.I32, listOf(IntegerType.I1)))
        val condition = function.parameters[0]
        val entry = function.insertBasicBlock("entry")
        val thenBlock = function.insertBasicBlock("then")
        val elseBlock = function.insertBasicBlock("else")
        val merge = function.insertBasicBlock("merge")

        builder.positionAtEnd(entry)
        val slot = builder.insertAlloca(IntegerType.I32, "result")
        builder.insertCondBr(condition, thenBlock, elseBlock)

        builder.positionAtEnd(thenBlock)
        builder.insertStore(IntConstant(1L, IntegerType.I32), slot)
        builder.insertBr(merge)

        builder.positionAtEnd(elseBlock)
        builder.insertStore(IntConstant(2L, IntegerType.I32), slot)
        builder.insertBr(merge)

        builder.positionAtEnd(merge)
        val loaded = builder.insertLoad(IntegerType.I32, slot, "loaded")
        builder.insertRet(loaded)

        Mem2RegPass.run(module, createAnalysisManager(module))

        val phi = assertIs<PhiNode>(merge.instructions.first())
        assertEquals(2, phi.getNumIncomingValues())
        assertEquals(setOf("then", "else"), phi.blocks.map { it.name }.toSet())
        assertNoMemoryTraffic(function)
        assertTrue(module.toIRString().contains("phi i32"))
        ValidationPass.run(module, AnalysisManager(module))
    }

    @Test
    fun `inserts loop phi for carried value`() {
        val module = Module("test")
        val builder = IRBuilder(module)
        val function = module.registerFunction("loop", FunctionType(IntegerType.I32, listOf(IntegerType.I32)))
        val limit = function.parameters[0]
        val entry = function.insertBasicBlock("entry")
        val header = function.insertBasicBlock("header")
        val body = function.insertBasicBlock("body")
        val exit = function.insertBasicBlock("exit")

        builder.positionAtEnd(entry)
        val slot = builder.insertAlloca(IntegerType.I32, "counter")
        builder.insertStore(IntConstant(0L, IntegerType.I32), slot)
        builder.insertBr(header)

        builder.positionAtEnd(header)
        val current = builder.insertLoad(IntegerType.I32, slot, "current")
        val cond = builder.insertICmp(IcmpPredicate.SLT, current, limit, "cond")
        builder.insertCondBr(cond, body, exit)

        builder.positionAtEnd(body)
        val bodyCurrent = builder.insertLoad(IntegerType.I32, slot, "bodyCurrent")
        val next = builder.insertAdd(bodyCurrent, IntConstant(1L, IntegerType.I32), "next")
        builder.insertStore(next, slot)
        builder.insertBr(header)

        builder.positionAtEnd(exit)
        val result = builder.insertLoad(IntegerType.I32, slot, "result")
        builder.insertRet(result)

        Mem2RegPass.run(module, createAnalysisManager(module))

        val phi = assertIs<PhiNode>(header.instructions.first())
        assertEquals(2, phi.getNumIncomingValues())
        assertTrue(phi.blocks.any { it.name == "entry" })
        assertTrue(phi.blocks.any { it.name == "body" })
        assertNoMemoryTraffic(function)
        assertTrue(module.toIRString().contains("[ 0, %entry ]"))
        assertTrue(module.toIRString().contains("[ %next, %body ]"))
        ValidationPass.run(module, AnalysisManager(module))
    }

    @Test
    fun `uses unique names for multiple phis from one slot`() {
        val module = Module("test")
        val builder = IRBuilder(module)
        val function = module.registerFunction("two_joins", FunctionType(IntegerType.I32, listOf(IntegerType.I1)))
        val condition = function.parameters[0]
        val entry = function.insertBasicBlock("entry")
        val firstThen = function.insertBasicBlock("first_then")
        val firstJoin = function.insertBasicBlock("first_join")
        val secondThen = function.insertBasicBlock("second_then")
        val secondJoin = function.insertBasicBlock("second_join")

        builder.positionAtEnd(entry)
        val slot = builder.insertAlloca(IntegerType.I32, "slot")
        builder.insertStore(IntConstant(0L, IntegerType.I32), slot)
        builder.insertCondBr(condition, firstThen, firstJoin)

        builder.positionAtEnd(firstThen)
        builder.insertStore(IntConstant(1L, IntegerType.I32), slot)
        builder.insertBr(firstJoin)

        builder.positionAtEnd(firstJoin)
        builder.insertCondBr(condition, secondThen, secondJoin)

        builder.positionAtEnd(secondThen)
        builder.insertStore(IntConstant(2L, IntegerType.I32), slot)
        builder.insertBr(secondJoin)

        builder.positionAtEnd(secondJoin)
        val loaded = builder.insertLoad(IntegerType.I32, slot, "loaded")
        builder.insertRet(loaded)

        Mem2RegPass.run(module, createAnalysisManager(module))

        val phiNames = function.basicBlocks
            .flatMap { it.instructions }
            .filterIsInstance<PhiNode>()
            .mapNotNull { it.name }

        assertEquals(2, phiNames.size)
        assertEquals(phiNames.toSet().size, phiNames.size)
        assertTrue(phiNames.all { it.startsWith("slot.phi") })
        assertNoMemoryTraffic(function)
        ValidationPass.run(module, AnalysisManager(module))
    }

    @Test
    fun `does not promote slot passed to call argument`() {
        val module = Module("test")
        val builder = IRBuilder(module)
        val sink = module.declareExternalFunction("sink", FunctionType(VoidType, listOf(PointerType)))
        val function = module.registerFunction("caller", FunctionType(IntegerType.I32, emptyList()))
        val entry = function.insertBasicBlock("entry")

        builder.positionAtEnd(entry)
        val slot = builder.insertAlloca(IntegerType.I32, "slot")
        builder.insertStore(IntConstant(7L, IntegerType.I32), slot)
        builder.insertVoidCall(sink, listOf(slot))
        val loaded = builder.insertLoad(IntegerType.I32, slot, "loaded")
        builder.insertRet(loaded)

        Mem2RegPass.run(module, createAnalysisManager(module))

        assertTrue(entry.instructions.any { it is AllocaInst && it == slot })
        assertTrue(entry.instructions.any { it is StoreInst && it.pointer == slot })
        assertTrue(entry.instructions.any { it is LoadInst && it.pointer == slot })
        assertTrue(module.toIRString().contains("call void @sink(ptr %slot)"))
    }

    @Test
    fun `does not promote slot used as both store destination and value`() {
        val module = Module("test")
        val builder = IRBuilder(module)
        val function = module.registerFunction("self_store", FunctionType(VoidType, emptyList()))
        val entry = function.insertBasicBlock("entry")

        builder.positionAtEnd(entry)
        val slot = builder.insertAlloca(PointerType, "slot")
        builder.insertStore(slot, slot)
        builder.insertRetVoid()

        Mem2RegPass.run(module, createAnalysisManager(module))

        assertTrue(entry.instructions.any { it is AllocaInst && it == slot })
        assertTrue(entry.instructions.any { it is StoreInst && it.pointer == slot && it.value == slot })
        assertTrue(module.toIRString().contains("store ptr %slot, ptr %slot"))
    }

    private fun assertNoMemoryTraffic(function: space.norb.llvm.structure.Function) {
        for (block in function.basicBlocks) {
            assertTrue(block.instructions.none { it is AllocaInst || it is LoadInst || it is StoreInst })
        }
    }
}
