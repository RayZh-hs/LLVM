package space.norb.llvm.transformation.presets

import org.junit.jupiter.api.Test
import space.norb.llvm.analysis.AnalysisManager
import space.norb.llvm.builder.IRBuilder
import space.norb.llvm.instructions.memory.AllocaInst
import space.norb.llvm.instructions.memory.LoadInst
import space.norb.llvm.instructions.memory.StoreInst
import space.norb.llvm.structure.Module
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.IntegerType
import space.norb.llvm.types.PointerType
import space.norb.llvm.types.VoidType
import space.norb.llvm.values.constants.IntConstant
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DeadStoreEliminationPassTest {

    @Test
    fun `eliminates simple dead store`() {
        val module = Module("test")
        val builder = IRBuilder(module)
        val function = module.registerFunction("dead_store", FunctionType(VoidType, emptyList()))
        val entry = function.insertBasicBlock("entry")

        builder.positionAtEnd(entry)
        val slot = builder.insertAlloca(IntegerType.I32, "slot")
        builder.insertStore(IntConstant(1L, IntegerType.I32), slot)
        builder.insertStore(IntConstant(2L, IntegerType.I32), slot)
        builder.insertRetVoid()

        DeadStoreEliminationPass.run(module, AnalysisManager(module))

        val stores = entry.instructions.filterIsInstance<StoreInst>()
        assertEquals(1, stores.size)
        assertEquals(2L, (stores.first().value as IntConstant).value)
        ValidationPass.run(module, AnalysisManager(module))
    }

    @Test
    fun `keeps store when loaded before overwrite`() {
        val module = Module("test")
        val builder = IRBuilder(module)
        val function = module.registerFunction("loaded_store", FunctionType(IntegerType.I32, emptyList()))
        val entry = function.insertBasicBlock("entry")

        builder.positionAtEnd(entry)
        val slot = builder.insertAlloca(IntegerType.I32, "slot")
        builder.insertStore(IntConstant(1L, IntegerType.I32), slot)
        val loaded = builder.insertLoad(IntegerType.I32, slot, "loaded")
        builder.insertStore(IntConstant(2L, IntegerType.I32), slot)
        builder.insertRet(loaded)

        DeadStoreEliminationPass.run(module, AnalysisManager(module))

        val stores = entry.instructions.filterIsInstance<StoreInst>()
        assertEquals(2, stores.size)
        ValidationPass.run(module, AnalysisManager(module))
    }

    @Test
    fun `keeps store across function call`() {
        val module = Module("test")
        val builder = IRBuilder(module)
        val callee = module.declareExternalFunction("callee", FunctionType(VoidType, emptyList()))
        val function = module.registerFunction("with_call", FunctionType(VoidType, emptyList()))
        val entry = function.insertBasicBlock("entry")

        builder.positionAtEnd(entry)
        val slot = builder.insertAlloca(IntegerType.I32, "slot")
        builder.insertStore(IntConstant(1L, IntegerType.I32), slot)
        builder.insertVoidCall(callee, emptyList())
        builder.insertStore(IntConstant(2L, IntegerType.I32), slot)
        builder.insertRetVoid()

        DeadStoreEliminationPass.run(module, AnalysisManager(module))

        val stores = entry.instructions.filterIsInstance<StoreInst>()
        assertEquals(2, stores.size)
        ValidationPass.run(module, AnalysisManager(module))
    }

    @Test
    fun `does not eliminate stores to different pointers`() {
        val module = Module("test")
        val builder = IRBuilder(module)
        val function = module.registerFunction("two_slots", FunctionType(VoidType, emptyList()))
        val entry = function.insertBasicBlock("entry")

        builder.positionAtEnd(entry)
        val slotA = builder.insertAlloca(IntegerType.I32, "a")
        val slotB = builder.insertAlloca(IntegerType.I32, "b")
        builder.insertStore(IntConstant(1L, IntegerType.I32), slotA)
        builder.insertStore(IntConstant(2L, IntegerType.I32), slotB)
        builder.insertRetVoid()

        DeadStoreEliminationPass.run(module, AnalysisManager(module))

        val stores = entry.instructions.filterIsInstance<StoreInst>()
        assertEquals(2, stores.size)
        ValidationPass.run(module, AnalysisManager(module))
    }

    @Test
    fun `eliminates multiple consecutive dead stores`() {
        val module = Module("test")
        val builder = IRBuilder(module)
        val function = module.registerFunction("multi_dead", FunctionType(VoidType, emptyList()))
        val entry = function.insertBasicBlock("entry")

        builder.positionAtEnd(entry)
        val slot = builder.insertAlloca(IntegerType.I32, "slot")
        builder.insertStore(IntConstant(1L, IntegerType.I32), slot)
        builder.insertStore(IntConstant(2L, IntegerType.I32), slot)
        builder.insertStore(IntConstant(3L, IntegerType.I32), slot)
        builder.insertRetVoid()

        DeadStoreEliminationPass.run(module, AnalysisManager(module))

        val stores = entry.instructions.filterIsInstance<StoreInst>()
        assertEquals(1, stores.size)
        assertEquals(3L, (stores.first().value as IntConstant).value)
        ValidationPass.run(module, AnalysisManager(module))
    }

    @Test
    fun `eliminates dead store across pure computation`() {
        val module = Module("test")
        val builder = IRBuilder(module)
        val function = module.registerFunction("pure_between", FunctionType(IntegerType.I32, emptyList()))
        val entry = function.insertBasicBlock("entry")

        builder.positionAtEnd(entry)
        val slot = builder.insertAlloca(IntegerType.I32, "slot")
        builder.insertStore(IntConstant(1L, IntegerType.I32), slot)
        val unused = builder.insertAdd(IntConstant(10L, IntegerType.I32), IntConstant(20L, IntegerType.I32), "unused")
        builder.insertStore(IntConstant(2L, IntegerType.I32), slot)
        builder.insertRet(unused)

        DeadStoreEliminationPass.run(module, AnalysisManager(module))

        val stores = entry.instructions.filterIsInstance<StoreInst>()
        assertEquals(1, stores.size)
        assertEquals(2L, (stores.first().value as IntConstant).value)
        ValidationPass.run(module, AnalysisManager(module))
    }

    @Test
    fun `keeps dead store when passed to external function`() {
        val module = Module("test")
        val builder = IRBuilder(module)
        val sink = module.declareExternalFunction("sink", FunctionType(VoidType, listOf(PointerType)))
        val function = module.registerFunction("escaped", FunctionType(VoidType, emptyList()))
        val entry = function.insertBasicBlock("entry")

        builder.positionAtEnd(entry)
        val slot = builder.insertAlloca(IntegerType.I32, "slot")
        builder.insertStore(IntConstant(1L, IntegerType.I32), slot)
        builder.insertVoidCall(sink, listOf(slot))
        builder.insertStore(IntConstant(2L, IntegerType.I32), slot)
        builder.insertRetVoid()

        DeadStoreEliminationPass.run(module, AnalysisManager(module))

        val stores = entry.instructions.filterIsInstance<StoreInst>()
        assertEquals(2, stores.size)
        ValidationPass.run(module, AnalysisManager(module))
    }

    @Test
    fun `does not eliminate stores across blocks`() {
        val module = Module("test")
        val builder = IRBuilder(module)
        val function = module.registerFunction("across_blocks", FunctionType(VoidType, listOf(IntegerType.I1)))
        val condition = function.parameters[0]
        val entry = function.insertBasicBlock("entry")
        val thenBlock = function.insertBasicBlock("then")
        val elseBlock = function.insertBasicBlock("else")
        val merge = function.insertBasicBlock("merge")

        builder.positionAtEnd(entry)
        val slot = builder.insertAlloca(IntegerType.I32, "slot")
        builder.insertStore(IntConstant(0L, IntegerType.I32), slot)
        builder.insertCondBr(condition, thenBlock, elseBlock)

        builder.positionAtEnd(thenBlock)
        builder.insertStore(IntConstant(1L, IntegerType.I32), slot)
        builder.insertBr(merge)

        builder.positionAtEnd(elseBlock)
        builder.insertStore(IntConstant(2L, IntegerType.I32), slot)
        builder.insertBr(merge)

        builder.positionAtEnd(merge)
        builder.insertRetVoid()

        DeadStoreEliminationPass.run(module, AnalysisManager(module))

        assertEquals(1, entry.instructions.filterIsInstance<StoreInst>().size)
        assertEquals(1, thenBlock.instructions.filterIsInstance<StoreInst>().size)
        assertEquals(1, elseBlock.instructions.filterIsInstance<StoreInst>().size)
        ValidationPass.run(module, AnalysisManager(module))
    }
}
