package space.norb.llvm.analysis.presets

import org.junit.jupiter.api.Test
import space.norb.llvm.analysis.AnalysisManager
import space.norb.llvm.builder.IRBuilder
import space.norb.llvm.structure.Module
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.IntegerType
import kotlin.test.assertContentEquals
import kotlin.test.assertTrue

class UseDefAnalysisTest {

    @Test
    fun `tracks defs and uses across instructions`() {
        val module = Module("test")
        val builder = IRBuilder(module)
        val function = module.registerFunction("sum", FunctionType(IntegerType.I32, listOf(IntegerType.I32, IntegerType.I32)))

        val entry = function.insertBasicBlock("entry")
        val exit = function.insertBasicBlock("exit")
        val lhs = function.parameters[0]
        val rhs = function.parameters[1]

        builder.positionAtEnd(entry)
        val add = builder.insertAdd(lhs, rhs, "sum")
        val branch = builder.insertBr(exit)

        builder.positionAtEnd(exit)
        val ret = builder.insertRet(add)

        val chain = AnalysisManager(module).get(UseDefAnalysis::class)

        assertContentEquals(listOf(lhs, rhs), chain.getDefs(add))
        assertContentEquals(listOf(add), chain.getUses(lhs))
        assertContentEquals(listOf(add), chain.getUses(rhs))
        assertContentEquals(listOf(exit), chain.getDefs(branch))
        assertContentEquals(listOf(branch), chain.getUses(exit))
        assertContentEquals(listOf(add), chain.getDefs(ret))
        assertContentEquals(listOf(ret), chain.getUses(add))
        assertTrue(function in chain)
        assertTrue(entry in chain)
    }

    @Test
    fun `preserves duplicate operand uses`() {
        val module = Module("test")
        val builder = IRBuilder(module)
        val function = module.registerFunction("double", FunctionType(IntegerType.I32, listOf(IntegerType.I32)))

        val entry = function.insertBasicBlock("entry")
        val value = function.parameters[0]

        builder.positionAtEnd(entry)
        val add = builder.insertAdd(value, value, "twice")
        builder.insertRet(add)

        val chain = AnalysisManager(module).get(UseDefAnalysis::class)

        assertContentEquals(listOf(value, value), chain.getDefs(add))
        assertContentEquals(listOf(add, add), chain.getUses(value))
    }
}
