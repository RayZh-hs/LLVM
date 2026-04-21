package space.norb.llvm.transformation.presets

import org.junit.jupiter.api.Test
import space.norb.llvm.analysis.AnalysisManager
import space.norb.llvm.builder.IRBuilder
import space.norb.llvm.instructions.other.CallInst
import space.norb.llvm.instructions.other.PhiNode
import space.norb.llvm.structure.Function
import space.norb.llvm.structure.Module
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.IntegerType
import space.norb.llvm.types.VoidType
import space.norb.llvm.values.constants.IntConstant
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class FunctionInliningPassTest {
    @Test
    fun `inlines simple direct call and rewrites post-call uses`() {
        val module = Module("test")
        val builder = IRBuilder(module)
        val addOne = module.registerFunction("add_one", FunctionType(IntegerType.I32, listOf(IntegerType.I32)))
        val caller = module.registerFunction("caller", FunctionType(IntegerType.I32, listOf(IntegerType.I32)))

        builder.positionAtEnd(addOne.insertBasicBlock("entry"))
        val incremented = builder.insertAdd(addOne.parameters[0], IntConstant(1L, IntegerType.I32), "incremented")
        builder.insertRet(incremented)

        builder.positionAtEnd(caller.insertBasicBlock("entry"))
        val call = builder.insertCall(addOne, listOf(caller.parameters[0]), "called")
        val sum = builder.insertAdd(call, IntConstant(2L, IntegerType.I32), "sum")
        builder.insertRet(sum)

        FunctionInliningPass().run(module, AnalysisManager(module))

        assertFalse(caller.instructions().any { it is CallInst && it.callee == addOne })
        assertTrue(caller.basicBlocks.size > 1)
        assertFalse(sum.lhs == call)
        assertTrue(module.toIRString().contains("ret i32 %sum"))
        ValidationPass.run(module, AnalysisManager(module))
    }

    @Test
    fun `creates phi for inlined callee with multiple return blocks`() {
        val module = Module("test")
        val builder = IRBuilder(module)
        val choose = module.registerFunction("choose", FunctionType(IntegerType.I32, listOf(IntegerType.I1)))
        val caller = module.registerFunction("caller", FunctionType(IntegerType.I32, listOf(IntegerType.I1)))

        val chooseEntry = choose.insertBasicBlock("entry")
        val chooseThen = choose.insertBasicBlock("then")
        val chooseElse = choose.insertBasicBlock("else")

        builder.positionAtEnd(chooseEntry)
        builder.insertCondBr(choose.parameters[0], chooseThen, chooseElse)

        builder.positionAtEnd(chooseThen)
        builder.insertRet(IntConstant(1L, IntegerType.I32))

        builder.positionAtEnd(chooseElse)
        builder.insertRet(IntConstant(2L, IntegerType.I32))

        builder.positionAtEnd(caller.insertBasicBlock("entry"))
        val call = builder.insertCall(choose, listOf(caller.parameters[0]), "picked")
        builder.insertRet(call)

        FunctionInliningPass().run(module, AnalysisManager(module))

        val phi = assertNotNull(caller.instructions().filterIsInstance<PhiNode>().singleOrNull())
        assertEquals("picked", phi.name)
        assertEquals(2, phi.getNumIncomingValues())
        assertFalse(caller.instructions().any { it is CallInst && it.callee == choose })
        ValidationPass.run(module, AnalysisManager(module))
    }

    @Test
    fun `does not inline functions with no-inline attribute`() {
        val module = Module("test")
        val builder = IRBuilder(module)
        val callee = module.registerFunction("callee", FunctionType(VoidType, emptyList()))
            .addAttribute(FunctionInliningPass.NO_INLINE_ATTRIBUTE)
        val caller = module.registerFunction("caller", FunctionType(VoidType, emptyList()))

        builder.positionAtEnd(callee.insertBasicBlock("entry"))
        builder.insertRetVoid()

        builder.positionAtEnd(caller.insertBasicBlock("entry"))
        builder.insertVoidCall(callee, emptyList())
        builder.insertRetVoid()

        FunctionInliningPass { true }.run(module, AnalysisManager(module))

        assertTrue(caller.instructions().any { it is CallInst && it.callee == callee })
        assertTrue(module.toIRString().contains("__no_inline"))
        ValidationPass.run(module, AnalysisManager(module))
    }

    @Test
    fun `uses custom simplicity predicate`() {
        val module = Module("test")
        val builder = IRBuilder(module)
        val callee = module.registerFunction("callee", FunctionType(VoidType, emptyList()))
        val caller = module.registerFunction("caller", FunctionType(VoidType, emptyList()))

        builder.positionAtEnd(callee.insertBasicBlock("entry"))
        builder.insertRetVoid()

        builder.positionAtEnd(caller.insertBasicBlock("entry"))
        builder.insertVoidCall(callee, emptyList())
        builder.insertRetVoid()

        FunctionInliningPass { false }.run(module, AnalysisManager(module))

        assertTrue(caller.instructions().any { it is CallInst && it.callee == callee })
    }

    @Test
    fun `traverses callees before callers when inlining`() {
        val module = Module("test")
        val builder = IRBuilder(module)
        val caller = module.registerFunction("caller", FunctionType(VoidType, emptyList()))
        val middle = module.registerFunction("middle", FunctionType(VoidType, emptyList()))
        val leaf = module.registerFunction("leaf", FunctionType(VoidType, emptyList()))

        builder.positionAtEnd(caller.insertBasicBlock("entry"))
        builder.insertVoidCall(middle, emptyList())
        builder.insertRetVoid()

        builder.positionAtEnd(middle.insertBasicBlock("entry"))
        builder.insertVoidCall(leaf, emptyList())
        repeat(7) { index -> builder.insertComment("padding $index") }
        builder.insertRetVoid()

        builder.positionAtEnd(leaf.insertBasicBlock("entry"))
        builder.insertRetVoid()

        FunctionInliningPass().run(module, AnalysisManager(module))

        assertFalse(middle.instructions().any { it is CallInst && it.callee == leaf })
        assertTrue(caller.instructions().any { it is CallInst && it.callee == middle })
        ValidationPass.run(module, AnalysisManager(module))
    }

    @Test
    fun `does not inline callee in recursive dependency component`() {
        val module = Module("test")
        val builder = IRBuilder(module)
        val left = module.registerFunction("left", FunctionType(VoidType, emptyList()))
        val right = module.registerFunction("right", FunctionType(VoidType, emptyList()))
        val caller = module.registerFunction("caller", FunctionType(VoidType, emptyList()))

        builder.positionAtEnd(left.insertBasicBlock("entry"))
        builder.insertVoidCall(right, emptyList())
        builder.insertRetVoid()

        builder.positionAtEnd(right.insertBasicBlock("entry"))
        builder.insertVoidCall(left, emptyList())
        builder.insertRetVoid()

        builder.positionAtEnd(caller.insertBasicBlock("entry"))
        builder.insertVoidCall(left, emptyList())
        builder.insertRetVoid()

        FunctionInliningPass { true }.run(module, AnalysisManager(module))

        assertTrue(caller.instructions().any { it is CallInst && it.callee == left })
        assertTrue(left.instructions().any { it is CallInst && it.callee == right })
        assertTrue(right.instructions().any { it is CallInst && it.callee == left })
        ValidationPass.run(module, AnalysisManager(module))
    }

    private fun Function.instructions() = basicBlocks.flatMap { it.instructions }
}
