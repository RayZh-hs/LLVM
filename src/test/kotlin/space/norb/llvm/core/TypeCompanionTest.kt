package space.norb.llvm.core

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import space.norb.llvm.types.*

/**
 * Unit tests for the Type companion object methods.
 */
@DisplayName("Type Companion Object Tests")
class TypeCompanionTest {

    @Test
    @DisplayName("getVoidType should return VoidType instance")
    fun testGetVoidType() {
        val voidType = Type.getVoidType()
        assertTrue(voidType is VoidType, "getVoidType should return VoidType")
        assertEquals("void", voidType.toString())
    }

    @Test
    @DisplayName("getLabelType should return LabelType instance")
    fun testGetLabelType() {
        val labelType = Type.getLabelType()
        assertTrue(labelType is LabelType, "getLabelType should return LabelType")
        assertEquals("label", labelType.toString())
    }

    @Test
    @DisplayName("getMetadataType should return MetadataType instance")
    fun testGetMetadataType() {
        val metadataType = Type.getMetadataType()
        assertTrue(metadataType is MetadataType, "getMetadataType should return MetadataType")
        assertEquals("metadata", metadataType.toString())
    }

    @Test
    @DisplayName("getIntegerType should return IntegerType with correct bit width")
    fun testGetIntegerType() {
        val i32 = Type.getIntegerType(32)
        assertTrue(i32 is IntegerType, "getIntegerType should return IntegerType")
        assertEquals("i32", i32.toString())
        assertEquals(32, (i32 as IntegerType).bitWidth)

        val i64 = Type.getIntegerType(64)
        assertTrue(i64 is IntegerType, "getIntegerType should return IntegerType")
        assertEquals("i64", i64.toString())
        assertEquals(64, (i64 as IntegerType).bitWidth)
    }

    @Test
    @DisplayName("getIntegerType should throw exception for invalid bit width")
    fun testGetIntegerTypeInvalidBitWidth() {
        assertThrows(IllegalArgumentException::class.java) {
            Type.getIntegerType(0)
        }
        assertThrows(IllegalArgumentException::class.java) {
            Type.getIntegerType(-1)
        }
    }

    @Test
    @DisplayName("getFloatType should return FloatType instance")
    fun testGetFloatType() {
        val floatType = Type.getFloatType()
        assertTrue(floatType is FloatingPointType.FloatType, "getFloatType should return FloatType")
        assertEquals("float", floatType.toString())
    }

    @Test
    @DisplayName("getDoubleType should return DoubleType instance")
    fun testGetDoubleType() {
        val doubleType = Type.getDoubleType()
        assertTrue(doubleType is FloatingPointType.DoubleType, "getDoubleType should return DoubleType")
        assertEquals("double", doubleType.toString())
    }

    @Test
    @DisplayName("getPointerType should return PointerType for given element type")
    fun testGetPointerType() {
        val i32 = Type.getIntegerType(32)
        val pointerType = Type.getPointerType(i32)
        assertTrue(pointerType is PointerType, "getPointerType should return PointerType")
        assertEquals("i32*", pointerType.toString())
        assertEquals(i32, (pointerType as PointerType).pointeeType)
    }

    @Test
    @DisplayName("getFunctionType should return FunctionType with correct signature")
    fun testGetFunctionType() {
        val i32 = Type.getIntegerType(32)
        val i64 = Type.getIntegerType(64)
        
        // Function with no parameters
        val funcType1 = Type.getFunctionType(i32, emptyList())
        assertTrue(funcType1 is FunctionType, "getFunctionType should return FunctionType")
        assertEquals("i32 ()", funcType1.toString())
        
        // Function with parameters
        val funcType2 = Type.getFunctionType(i32, listOf(i32, i64))
        assertEquals("i32 (i32, i64)", funcType2.toString())
    }

    @Test
    @DisplayName("getArrayType should return ArrayType with correct element type and size")
    fun testGetArrayType() {
        val i32 = Type.getIntegerType(32)
        val arrayType = Type.getArrayType(i32, 10)
        assertTrue(arrayType is ArrayType, "getArrayType should return ArrayType")
        assertEquals("[10 x i32]", arrayType.toString())
        assertEquals(10, (arrayType as ArrayType).numElements)
        assertEquals(i32, arrayType.elementType)
    }

    @Test
    @DisplayName("getArrayType should throw exception for invalid element count")
    fun testGetArrayTypeInvalidElementCount() {
        val i32 = Type.getIntegerType(32)
        assertThrows(IllegalArgumentException::class.java) {
            Type.getArrayType(i32, 0)
        }
        assertThrows(IllegalArgumentException::class.java) {
            Type.getArrayType(i32, -1)
        }
    }

    @Test
    @DisplayName("getStructType should return StructType with correct element types")
    fun testGetStructType() {
        val i32 = Type.getIntegerType(32)
        val i64 = Type.getIntegerType(64)
        
        val structType = Type.getStructType(listOf(i32, i64))
        assertTrue(structType is StructType, "getStructType should return StructType")
        assertEquals("{ i32, i64 }", structType.toString())
        assertEquals(listOf(i32, i64), (structType as StructType).elementTypes)
        assertFalse(structType.isPacked)
    }
}