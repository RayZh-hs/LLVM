package space.norb.llvm.analysis.presets

import org.junit.jupiter.api.Test
import space.norb.llvm.analysis.AnalysisManager
import space.norb.llvm.builder.IRBuilder
import space.norb.llvm.enums.IcmpPredicate
import space.norb.llvm.structure.Module
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.IntegerType
import kotlin.test.assertEquals

class InstructionLivenessAnalysisTest {

    @Test
    fun `tracks live in and out per instruction in a straight line block`() {
        val module = Module("test")
        val builder = IRBuilder(module)
        val function = module.registerFunction(
            "calc",
            FunctionType(IntegerType.I32, listOf(IntegerType.I32, IntegerType.I32))
        )

        val entry = function.insertBasicBlock("entry")
        val lhs = function.parameters[0]
        val rhs = function.parameters[1]

        builder.positionAtEnd(entry)
        val add = builder.insertAdd(lhs, rhs, "sum")
        val sub = builder.insertSub(add, lhs, "delta")
        val ret = builder.insertRet(sub)

        val lookup = AnalysisManager(module).get(InstructionLivenessAnalysis::class)

        assertEquals(setOf(lhs, rhs), lookup.liveInTable[add])
        assertEquals(setOf(add, lhs), lookup.liveOutTable[add])
        assertEquals(setOf(add, lhs), lookup.liveInTable[sub])
        assertEquals(setOf(sub), lookup.liveOutTable[sub])
        assertEquals(setOf(sub), lookup.liveInTable[ret])
        assertEquals(emptySet(), lookup.liveOutTable[ret])
    }

    @Test
    fun `keeps phi incoming values on predecessor edges instead of merge block live in`() {
        val module = Module("test")
        val builder = IRBuilder(module)
        val function = module.registerFunction(
            "branch",
            FunctionType(IntegerType.I32, listOf(IntegerType.I32, IntegerType.I32))
        )

        val entry = function.insertBasicBlock("entry")
        val left = function.insertBasicBlock("left")
        val right = function.insertBasicBlock("right")
        val merge = function.insertBasicBlock("merge")
        val lhs = function.parameters[0]
        val rhs = function.parameters[1]

        builder.positionAtEnd(entry)
        val cmp = builder.insertICmp(IcmpPredicate.EQ, lhs, rhs, "cmp")
        val entryBranch = builder.insertCondBr(cmp, left, right)

        builder.positionAtEnd(left)
        val leftValue = builder.insertAdd(lhs, rhs, "leftValue")
        val leftBranch = builder.insertBr(merge)

        builder.positionAtEnd(right)
        val rightValue = builder.insertSub(lhs, rhs, "rightValue")
        val rightBranch = builder.insertBr(merge)

        builder.positionAtEnd(merge)
        val phi = builder.insertPhi(IntegerType.I32, listOf(leftValue to left, rightValue to right), "result")
        builder.insertRet(phi)

        val lookup = AnalysisManager(module).get(InstructionLivenessAnalysis::class)

        assertEquals(setOf(lhs, rhs), lookup.liveOutTable[entryBranch])
        assertEquals(setOf(cmp, lhs, rhs), lookup.liveInTable[entryBranch])
        assertEquals(setOf(leftValue), lookup.liveInTable[leftBranch])
        assertEquals(setOf(leftValue), lookup.liveOutTable[leftBranch])
        assertEquals(setOf(rightValue), lookup.liveInTable[rightBranch])
        assertEquals(setOf(rightValue), lookup.liveOutTable[rightBranch])
        assertEquals(emptySet(), lookup.liveInTable[phi])
        assertEquals(setOf(phi), lookup.liveOutTable[phi])
    }
}
