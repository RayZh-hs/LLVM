package space.norb.llvm.instructions.other

import space.norb.llvm.structure.BasicBlock
import space.norb.llvm.structure.Function
import space.norb.llvm.structure.Module
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.IntegerType
import space.norb.llvm.types.VoidType
import space.norb.llvm.values.constants.IntConstant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class PhiNodeTest {

    private fun createTestBasicBlock(name: String): BasicBlock {
        val module = Module("test")
        val function = Function("test", FunctionType(VoidType, emptyList()), module)
        return BasicBlock(name, function)
    }

    @Test
    fun testIncomingValueIndexForMissingBlockReturnsNull() {
        val entry = createTestBasicBlock("entry")
        val thenBlock = createTestBasicBlock("then")
        val missing = createTestBasicBlock("missing")
        val phi = PhiNode.create(
            "phi",
            IntegerType.I32,
            listOf(
                IntConstant(1L, IntegerType.I32) to entry,
                IntConstant(2L, IntegerType.I32) to thenBlock
            )
        )

        assertNull(phi.getIncomingValueIndexForBlock(missing))
    }

    @Test
    fun testReplaceIncomingValueForBlockUsesNullableLookup() {
        val entry = createTestBasicBlock("entry")
        val thenBlock = createTestBasicBlock("then")
        val phi = PhiNode.create(
            "phi",
            IntegerType.I32,
            listOf(
                IntConstant(1L, IntegerType.I32) to entry,
                IntConstant(2L, IntegerType.I32) to thenBlock
            )
        )

        val updated = phi.replaceIncomingValueForBlock(thenBlock, IntConstant(3L, IntegerType.I32))

        assertEquals(3L, (updated.getIncomingValueForBlock(thenBlock) as IntConstant).value)
        assertEquals(1L, (updated.getIncomingValueForBlock(entry) as IntConstant).value)
    }

    @Test
    fun testReplaceIncomingValueForMissingBlockFailsClearly() {
        val entry = createTestBasicBlock("entry")
        val thenBlock = createTestBasicBlock("then")
        val missing = createTestBasicBlock("missing")
        val phi = PhiNode.create(
            "phi",
            IntegerType.I32,
            listOf(
                IntConstant(1L, IntegerType.I32) to entry,
                IntConstant(2L, IntegerType.I32) to thenBlock
            )
        )

        val error = assertFailsWith<IllegalArgumentException> {
            phi.replaceIncomingValueForBlock(missing, IntConstant(3L, IntegerType.I32))
        }

        assertEquals("No incoming value found for block: missing", error.message)
    }
}
