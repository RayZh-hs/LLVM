package space.norb.llvm.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import space.norb.llvm.types.ArrayType
import space.norb.llvm.types.IntegerType
import space.norb.llvm.types.StructType

@DisplayName("MemoryLayoutUtils size calculations")
class MemoryLayoutUtilsTest {

    @Test
    fun `array of integers multiplies element size`() {
        val arrayType = ArrayType(5, IntegerType(32))
        assertEquals(20, arrayType.getSizeInBytes())
    }

    @Test
    fun `struct pads members to natural alignment`() {
        val structType = StructType.AnonymousStructType(listOf(IntegerType(8), IntegerType(32)))

        // i8 (1 byte) + padding (3 bytes) + i32 (4 bytes) = 8 bytes total
        assertEquals(8, structType.getSizeInBytes())

        // Verify arrays of the struct respect the padded stride
        val structArray = ArrayType(2, structType)
        assertEquals(16, structArray.getSizeInBytes())
    }

    @Test
    fun `packed struct removes padding`() {
        val packedStruct = StructType.AnonymousStructType(listOf(IntegerType(8), IntegerType(32)), isPacked = true)
        assertEquals(5, packedStruct.getSizeInBytes())

        val packedArray = ArrayType(3, packedStruct)
        assertEquals(15, packedArray.getSizeInBytes())
    }

    @Test
    fun `nested structs honour inner alignment`() {
        val innerStruct = StructType.AnonymousStructType(listOf(IntegerType(8), IntegerType(32)))
        val outerStruct = StructType.AnonymousStructType(listOf(IntegerType(16), innerStruct))

        // innerStruct is 8 bytes aligned to 4; outer starts with i16 (2 bytes) then aligns inner to 4
        assertEquals(12, outerStruct.getSizeInBytes())
    }

    @Test
    fun `named struct uses definition and opaque structs are rejected`() {
        val namedStruct = StructType.NamedStructType("pair", listOf(IntegerType(32), IntegerType(32)))
        assertEquals(8, namedStruct.getSizeInBytes())

        val opaqueStruct = StructType.NamedStructType("opaque")
        assertThrows(IllegalArgumentException::class.java) {
            opaqueStruct.getSizeInBytes()
        }
    }
}
