package space.norb.llvm.core

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import space.norb.llvm.types.*

/**
 * Unit tests for the Type sealed class.
 */
@DisplayName("Type Sealed Class Tests")
class TypeTest {

    @Test
    @DisplayName("Type toString should return class name for unknown types")
    fun testTypeToString() {
        // Create an anonymous subclass of Type for testing
        val unknownType = object : Type() {}
        
        val toString = unknownType.toString()
        assertTrue(toString.contains("TypeTest"), "toString should contain class name")
    }

    @Test
    @DisplayName("VoidType should have correct string representation")
    fun testVoidType() {
        assertEquals("void", VoidType.toString(), "VoidType should have correct string representation")
    }

    @Test
    @DisplayName("LabelType should have correct string representation")
    fun testLabelType() {
        assertEquals("label", LabelType.toString(), "LabelType should have correct string representation")
    }

    @Test
    @DisplayName("MetadataType should have correct string representation")
    fun testMetadataType() {
        assertEquals("metadata", MetadataType.toString(), "MetadataType should have correct string representation")
    }

    @Test
    @DisplayName("IntegerType should have correct string representation")
    fun testIntegerType() {
        val i32 = IntegerType(32)
        assertEquals("i32", i32.toString(), "IntegerType should have correct string representation")
        
        val i64 = IntegerType(64)
        assertEquals("i64", i64.toString(), "IntegerType should have correct string representation")
    }

    @Test
    @DisplayName("IntegerType should create with valid bit width")
    fun testIntegerTypeCreation() {
        // Valid bit widths
        val i1 = IntegerType(1)
        assertEquals(1, i1.bitWidth)
        assertEquals("i1", i1.toString())
        
        val i8 = IntegerType(8)
        assertEquals(8, i8.bitWidth)
        assertEquals("i8", i8.toString())
        
        val i32 = IntegerType(32)
        assertEquals(32, i32.bitWidth)
        assertEquals("i32", i32.toString())
        
        val i64 = IntegerType(64)
        assertEquals(64, i64.bitWidth)
        assertEquals("i64", i64.toString())
    }

    @Test
    @DisplayName("FloatingPointType should have correct string representations")
    fun testFloatingPointType() {
        assertEquals("float", FloatingPointType.FloatType.toString())
        assertEquals("double", FloatingPointType.DoubleType.toString())
    }

    @Test
    @DisplayName("PointerType should have correct string representation")
    fun testPointerType() {
        val i32Type = IntegerType(32)
        val pointerType = PointerType(i32Type)
        assertEquals("i32*", pointerType.toString())
        assertEquals(i32Type, pointerType.pointeeType)
    }

    @Test
    @DisplayName("FunctionType should have correct string representation")
    fun testFunctionType() {
        val i32Type = IntegerType(32)
        val i64Type = IntegerType(64)
        
        // Function with no parameters
        val funcType1 = FunctionType(i32Type, emptyList())
        assertEquals("i32 ()", funcType1.toString())
        
        // Function with parameters
        val funcType2 = FunctionType(i32Type, listOf(i32Type, i64Type))
        assertEquals("i32 (i32, i64)", funcType2.toString())
        
        // Vararg function
        val funcType3 = FunctionType(i32Type, listOf(i32Type), true)
        assertEquals("i32 (i32, ...)", funcType3.toString())
        
        // Vararg function with no fixed parameters
        val funcType4 = FunctionType(i32Type, emptyList(), true)
        assertEquals("i32 (...)", funcType4.toString())
    }

    @Test
    @DisplayName("ArrayType should have correct string representation")
    fun testArrayType() {
        val i32Type = IntegerType(32)
        val arrayType = ArrayType(10, i32Type)
        assertEquals("[10 x i32]", arrayType.toString())
        assertEquals(10, arrayType.numElements)
        assertEquals(i32Type, arrayType.elementType)
    }

    @Test
    @DisplayName("StructType should have correct string representation")
    fun testStructType() {
        val i32Type = IntegerType(32)
        val i64Type = IntegerType(64)
        
        // Regular struct
        val structType1 = StructType(listOf(i32Type, i64Type))
        assertEquals("{ i32, i64 }", structType1.toString())
        assertEquals(listOf(i32Type, i64Type), structType1.elementTypes)
        assertFalse(structType1.isPacked)
        
        // Packed struct
        val structType2 = StructType(listOf(i32Type, i64Type), true)
        assertEquals("<{ i32, i64 }>", structType2.toString())
        assertEquals(listOf(i32Type, i64Type), structType2.elementTypes)
        assertTrue(structType2.isPacked)
    }

    // VectorType is not implemented yet, so we'll skip these tests for Phase 1

    @Test
    @DisplayName("Type equality should work correctly")
    fun testTypeEquality() {
        val i32Type1 = IntegerType(32)
        val i32Type2 = IntegerType(32)
        val i64Type = IntegerType(64)
        
        assertEquals(i32Type1, i32Type2, "Integer types with same bit width should be equal")
        assertNotEquals(i32Type1, i64Type, "Integer types with different bit widths should not be equal")
        
        val pointerType1 = PointerType(i32Type1)
        val pointerType2 = PointerType(i32Type2)
        val pointerType3 = PointerType(i64Type)
        
        assertEquals(pointerType1, pointerType2, "Pointer types with same pointee type should be equal")
        assertNotEquals(pointerType1, pointerType3, "Pointer types with different pointee types should not be equal")
    }
}