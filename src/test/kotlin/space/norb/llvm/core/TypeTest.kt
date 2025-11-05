package space.norb.llvm.core

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import space.norb.llvm.types.*

/**
 * Unit tests for the Type sealed class.
 *
 * ## LLVM IR Compliance Notice
 *
 * **LEGACY TYPED POINTER IMPLEMENTATION**: These tests validate the current typed pointer
 * implementation which follows the older LLVM IR model where pointers contain explicit
 * pointee type information (e.g., "i32*", "float*").
 *
 * This implementation is **NOT compliant** with the latest LLVM IR standard, which has
 * moved to un-typed pointers (similar to `void*` in C) where all pointers are of a single
 * type and type information is conveyed through other mechanisms.
 *
 * ## Migration Impact
 *
 * When migrating to un-typed pointers, these tests will need significant updates:
 * - Pointer string representations will change from "i32*" to "ptr"
 * - Pointee type information will no longer be stored in the pointer type
 * - Type equality checks for pointers will change (all pointers will be equal)
 * - Hash code implementations for pointers will need updating
 * - Type classification methods will need updates for un-typed pointers
 *
 * See migration documentation: [`docs/ptr-migration-todo.md`](../../docs/ptr-migration-todo.md)
 *
 * ## Current Test Coverage
 *
 * These tests validate the legacy typed pointer type system including:
 * - Pointer type creation and string representation
 * - Pointer type equality and hash code consistency
 * - Nested pointer types and complex type compositions
 * - Type classification methods for pointers
 */
@DisplayName("Type Sealed Class Tests (Legacy Typed Pointer Implementation)")
class TypeTest {

    @Test
    @DisplayName("Type toString should return class name for unknown types")
    fun testTypeToString() {
        // Create an anonymous subclass of Type for testing
        val unknownType = object : Type() {
            override fun toString(): String = "UnknownType"
            override fun isPrimitiveType(): Boolean = false
            override fun isDerivedType(): Boolean = false
            override fun isIntegerType(): Boolean = false
            override fun isFloatingPointType(): Boolean = false
            override fun isPointerType(): Boolean = false
            override fun isFunctionType(): Boolean = false
            override fun isArrayType(): Boolean = false
            override fun isStructType(): Boolean = false
            override fun getPrimitiveSizeInBits(): Int? = null
        }
        
        val toString = unknownType.toString()
        assertTrue(toString.contains("UnknownType"), "toString should contain type name")
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
        // Test with un-typed pointers (default mode)
        val i32Type = IntegerType(32)
        val untypedPtr = Type.getPointerType(i32Type)
        assertEquals("ptr", untypedPtr.toString())
        assertEquals(UntypedPointerType, untypedPtr)
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
        
        // Test with un-typed pointers (default mode)
        val untypedPtr1 = Type.getPointerType(i32Type1)
        val untypedPtr2 = Type.getPointerType(i32Type2)
        val untypedPtr3 = Type.getPointerType(i64Type)
        
        assertEquals(untypedPtr1, untypedPtr2, "All un-typed pointers should be equal")
        assertEquals(untypedPtr1, untypedPtr3, "All un-typed pointers should be equal regardless of element type")
        assertEquals(UntypedPointerType, untypedPtr1, "All un-typed pointers should be UntypedPointerType")
    }

    @Test
    @DisplayName("Complex type equality should work correctly")
    fun testComplexTypeEquality() {
        // Test function type equality
        val funcType1 = FunctionType(TypeUtils.I32, listOf(TypeUtils.I8, TypeUtils.I16))
        val funcType2 = FunctionType(TypeUtils.I32, listOf(TypeUtils.I8, TypeUtils.I16))
        val funcType3 = FunctionType(TypeUtils.I64, listOf(TypeUtils.I8, TypeUtils.I16))
        val funcType4 = FunctionType(TypeUtils.I32, listOf(TypeUtils.I8, TypeUtils.I32))
        
        assertEquals(funcType1, funcType2, "Function types with same signature should be equal")
        assertNotEquals(funcType1, funcType3, "Function types with different return types should not be equal")
        assertNotEquals(funcType1, funcType4, "Function types with different parameter types should not be equal")
        
        // Test array type equality
        val arrayType1 = ArrayType(10, TypeUtils.I32)
        val arrayType2 = ArrayType(10, TypeUtils.I32)
        val arrayType3 = ArrayType(5, TypeUtils.I32)
        val arrayType4 = ArrayType(10, TypeUtils.I64)
        
        assertEquals(arrayType1, arrayType2, "Array types with same element type and size should be equal")
        assertNotEquals(arrayType1, arrayType3, "Array types with different sizes should not be equal")
        assertNotEquals(arrayType1, arrayType4, "Array types with different element types should not be equal")
        
        // Test struct type equality
        val structType1 = StructType(listOf(TypeUtils.I32, TypeUtils.I64))
        val structType2 = StructType(listOf(TypeUtils.I32, TypeUtils.I64))
        val structType3 = StructType(listOf(TypeUtils.I64, TypeUtils.I32))
        val structType4 = StructType(listOf(TypeUtils.I32, TypeUtils.I64), true)
        
        assertEquals(structType1, structType2, "Struct types with same element types should be equal")
        assertNotEquals(structType1, structType3, "Struct types with different element order should not be equal")
        assertNotEquals(structType1, structType4, "Struct types with different packing should not be equal")
    }

    @Test
    @DisplayName("Nested type equality should work correctly")
    fun testNestedTypeEquality() {
        // Test pointer to pointer equality with un-typed pointers
        val i32Type = IntegerType(32)
        val i32Ptr1 = Type.getPointerType(i32Type)
        val i32Ptr2 = Type.getPointerType(i32Type)
        val i32PtrPtr1 = Type.getPointerType(i32Type) // All pointers are un-typed, so nested is same
        val i32PtrPtr2 = Type.getPointerType(i32Type)
        
        assertEquals(i32PtrPtr1, i32PtrPtr2, "All un-typed pointers should be equal")
        assertEquals(i32Ptr1, i32Ptr2, "All un-typed pointers should be equal")
        assertEquals(i32Ptr1, i32PtrPtr1, "All un-typed pointers should be equal regardless of nesting")
        
        // Test function with complex parameters equality
        val complexParam1 = ArrayType(10, StructType(listOf(TypeUtils.I32)))
        val complexParam2 = ArrayType(10, StructType(listOf(TypeUtils.I32)))
        val complexFunc1 = FunctionType(Type.getPointerType(TypeUtils.I32), listOf(complexParam1))
        val complexFunc2 = FunctionType(Type.getPointerType(TypeUtils.I32), listOf(complexParam2))
        
        assertEquals(complexFunc1, complexFunc2, "Functions with complex equal parameters should be equal")
        
        // Test array of structs equality
        val structType1 = StructType(listOf(TypeUtils.I32, TypeUtils.FLOAT))
        val structType2 = StructType(listOf(TypeUtils.I32, TypeUtils.FLOAT))
        val structArray1 = ArrayType(5, structType1)
        val structArray2 = ArrayType(5, structType2)
        
        assertEquals(structArray1, structArray2, "Arrays of equal structs should be equal")
    }

    @Test
    @DisplayName("Type hashCode should be consistent with equality")
    fun testTypeHashCodeConsistency() {
        // Test primitive types
        val i32Type1 = IntegerType(32)
        val i32Type2 = IntegerType(32)
        val i64Type = IntegerType(64)
        
        assertEquals(i32Type1.hashCode(), i32Type2.hashCode(), "Equal types should have equal hash codes")
        assertNotEquals(i32Type1.hashCode(), i64Type.hashCode(), "Different types should have different hash codes")
        
        // Test with un-typed pointers (default mode)
        val untypedPtr1 = Type.getPointerType(i32Type1)
        val untypedPtr2 = Type.getPointerType(i32Type2)
        val untypedPtr3 = Type.getPointerType(i64Type)
        
        assertEquals(untypedPtr1.hashCode(), untypedPtr2.hashCode(), "All un-typed pointers should have equal hash codes")
        assertEquals(untypedPtr1.hashCode(), untypedPtr3.hashCode(), "All un-typed pointers should have equal hash codes regardless of element type")
        
        // Test function types
        val funcType1 = FunctionType(TypeUtils.I32, listOf(TypeUtils.I8, TypeUtils.I16))
        val funcType2 = FunctionType(TypeUtils.I32, listOf(TypeUtils.I8, TypeUtils.I16))
        val funcType3 = FunctionType(TypeUtils.I64, listOf(TypeUtils.I8, TypeUtils.I16))
        
        assertEquals(funcType1.hashCode(), funcType2.hashCode(), "Equal function types should have equal hash codes")
        assertNotEquals(funcType1.hashCode(), funcType3.hashCode(), "Different function types should have different hash codes")
        
        // Test array types
        val arrayType1 = ArrayType(10, TypeUtils.I32)
        val arrayType2 = ArrayType(10, TypeUtils.I32)
        val arrayType3 = ArrayType(5, TypeUtils.I32)
        
        assertEquals(arrayType1.hashCode(), arrayType2.hashCode(), "Equal array types should have equal hash codes")
        assertNotEquals(arrayType1.hashCode(), arrayType3.hashCode(), "Different array types should have different hash codes")
        
        // Test struct types
        val structType1 = StructType(listOf(TypeUtils.I32, TypeUtils.I64))
        val structType2 = StructType(listOf(TypeUtils.I32, TypeUtils.I64))
        val structType3 = StructType(listOf(TypeUtils.I64, TypeUtils.I32))
        
        assertEquals(structType1.hashCode(), structType2.hashCode(), "Equal struct types should have equal hash codes")
        assertNotEquals(structType1.hashCode(), structType3.hashCode(), "Different struct types should have different hash codes")
    }

    @Test
    @DisplayName("Complex type hashCode should be consistent with equality")
    fun testComplexTypeHashCodeConsistency() {
        // Test deeply nested types
        val innerStruct1 = StructType(listOf(TypeUtils.I8, TypeUtils.I16))
        val innerStruct2 = StructType(listOf(TypeUtils.I8, TypeUtils.I16))
        val outerStruct1 = StructType(listOf(TypeUtils.I32, innerStruct1))
        val outerStruct2 = StructType(listOf(TypeUtils.I32, innerStruct2))
        
        assertEquals(outerStruct1.hashCode(), outerStruct2.hashCode(), "Equal nested structs should have equal hash codes")
        
        // Test function returning complex type
        val complexReturnType1 = PointerType(ArrayType(5, StructType(listOf(TypeUtils.I32))))
        val complexReturnType2 = PointerType(ArrayType(5, StructType(listOf(TypeUtils.I32))))
        val complexFunc1 = FunctionType(complexReturnType1, listOf(TypeUtils.I32))
        val complexFunc2 = FunctionType(complexReturnType2, listOf(TypeUtils.I32))
        
        assertEquals(complexFunc1.hashCode(), complexFunc2.hashCode(), "Equal complex functions should have equal hash codes")
        
        // Test array of function pointers
        val funcType1 = FunctionType(TypeUtils.I32, listOf(TypeUtils.I32))
        val funcType2 = FunctionType(TypeUtils.I32, listOf(TypeUtils.I32))
        val funcPtrArray1 = ArrayType(3, PointerType(funcType1))
        val funcPtrArray2 = ArrayType(3, PointerType(funcType2))
        
        assertEquals(funcPtrArray1.hashCode(), funcPtrArray2.hashCode(), "Equal arrays of function pointers should have equal hash codes")
    }

    @Test
    @DisplayName("Structurally identical types should be equal")
    fun testStructurallyIdenticalTypes() {
        val i32Type1 = IntegerType(32)
        val i32Type2 = IntegerType(32)
        
        val structType1 = StructType(listOf(i32Type1, Type.getPointerType(i32Type1)))
        val structType2 = StructType(listOf(i32Type2, Type.getPointerType(i32Type2)))
        
        assertEquals(structType1, structType2, "Structurally identical structs should be equal")
        assertEquals(structType1.hashCode(), structType2.hashCode(), "Structurally identical structs should have equal hash codes")
        
        // Test with more complex structures
        val complexType1 = FunctionType(
            ArrayType(5, StructType(listOf(i32Type1, Type.getPointerType(i32Type1)))),
            listOf(Type.getPointerType(FunctionType(i32Type1, listOf(i32Type1))))
        )
        
        val complexType2 = FunctionType(
            ArrayType(5, StructType(listOf(i32Type2, Type.getPointerType(i32Type2)))),
            listOf(Type.getPointerType(FunctionType(i32Type2, listOf(i32Type2))))
        )
        
        assertEquals(complexType1, complexType2, "Complex structurally identical types should be equal")
        assertEquals(complexType1.hashCode(), complexType2.hashCode(), "Complex structurally identical types should have equal hash codes")
    }

    @Test
    @DisplayName("Different types should not be equal")
    fun testDifferentTypesNotEqual() {
        val i32Type = IntegerType(32)
        val floatType = FloatingPointType.FloatType
        
        // Test different primitive types
        assertNotEquals(i32Type, floatType, "Different primitive types should not be equal")
        
        // Test with un-typed pointers (default mode)
        val i32Ptr = Type.getPointerType(i32Type)
        val i32Array = ArrayType(10, i32Type)
        val i32Struct = StructType(listOf(i32Type))
        val i32Func = FunctionType(i32Type, emptyList())
        
        assertNotEquals(i32Ptr, i32Array, "Pointer and array should not be equal")
        assertNotEquals(i32Ptr, i32Struct, "Pointer and struct should not be equal")
        assertNotEquals(i32Ptr, i32Func, "Pointer and function should not be equal")
        assertNotEquals(i32Array, i32Struct, "Array and struct should not be equal")
        assertNotEquals(i32Array, i32Func, "Array and function should not be equal")
        assertNotEquals(i32Struct, i32Func, "Struct and function should not be equal")
        
        // Test different complex types
        val struct1 = StructType(listOf(i32Type, floatType))
        val struct2 = StructType(listOf(floatType, i32Type))
        assertNotEquals(struct1, struct2, "Structs with different element order should not be equal")
        
        val func1 = FunctionType(i32Type, listOf(floatType))
        val func2 = FunctionType(floatType, listOf(i32Type))
        assertNotEquals(func1, func2, "Functions with different signatures should not be equal")
    }
}