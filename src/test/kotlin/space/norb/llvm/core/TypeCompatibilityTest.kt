package space.norb.llvm.core

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import space.norb.llvm.types.*

/**
 * Comprehensive tests for type compatibility checks and TypeUtils functions.
 * This covers Phase 2 requirements for type system validation.
 */
@DisplayName("Type Compatibility Tests")
class TypeCompatibilityTest {

    @Nested
    @DisplayName("First Class Type Tests")
    inner class FirstClassTypeTests {

        @Test
        @DisplayName("Primitive types should be first-class except void, label, and metadata")
        fun testPrimitiveTypesFirstClass() {
            // First-class primitive types
            assertTrue(TypeUtils.isFirstClassType(TypeUtils.I1), "i1 should be first-class")
            assertTrue(TypeUtils.isFirstClassType(TypeUtils.I8), "i8 should be first-class")
            assertTrue(TypeUtils.isFirstClassType(TypeUtils.I16), "i16 should be first-class")
            assertTrue(TypeUtils.isFirstClassType(TypeUtils.I32), "i32 should be first-class")
            assertTrue(TypeUtils.isFirstClassType(TypeUtils.I64), "i64 should be first-class")
            assertTrue(TypeUtils.isFirstClassType(TypeUtils.I128), "i128 should be first-class")
            assertTrue(TypeUtils.isFirstClassType(TypeUtils.FLOAT), "float should be first-class")
            assertTrue(TypeUtils.isFirstClassType(TypeUtils.DOUBLE), "double should be first-class")
            
            // Non-first-class primitive types
            assertFalse(TypeUtils.isFirstClassType(TypeUtils.VOID), "void should not be first-class")
            assertFalse(TypeUtils.isFirstClassType(TypeUtils.LABEL), "label should not be first-class")
            assertFalse(TypeUtils.isFirstClassType(TypeUtils.METADATA), "metadata should not be first-class")
        }

        @Test
        @DisplayName("Derived types should be first-class")
        fun testDerivedTypesFirstClass() {
            val i32Type = TypeUtils.I32
            
            // Pointer types
            val ptrType = PointerType(i32Type)
            assertTrue(TypeUtils.isFirstClassType(ptrType), "pointer types should be first-class")
            
            // Function types
            val funcType = FunctionType(i32Type, listOf(i32Type))
            assertTrue(TypeUtils.isFirstClassType(funcType), "function types should be first-class")
            
            // Array types
            val arrayType = ArrayType(10, i32Type)
            assertTrue(TypeUtils.isFirstClassType(arrayType), "array types should be first-class")
            
            // Struct types
            val structType = StructType(listOf(i32Type, TypeUtils.I64))
            assertTrue(TypeUtils.isFirstClassType(structType), "struct types should be first-class")
        }
    }

    @Nested
    @DisplayName("Single Value Type Tests")
    inner class SingleValueTypeTests {

        @Test
        @DisplayName("Integer and floating-point types should be single value types")
        fun testPrimitiveSingleValueTypes() {
            // Integer types
            assertTrue(TypeUtils.isSingleValueType(TypeUtils.I1), "i1 should be single value")
            assertTrue(TypeUtils.isSingleValueType(TypeUtils.I8), "i8 should be single value")
            assertTrue(TypeUtils.isSingleValueType(TypeUtils.I16), "i16 should be single value")
            assertTrue(TypeUtils.isSingleValueType(TypeUtils.I32), "i32 should be single value")
            assertTrue(TypeUtils.isSingleValueType(TypeUtils.I64), "i64 should be single value")
            assertTrue(TypeUtils.isSingleValueType(TypeUtils.I128), "i128 should be single value")
            
            // Floating-point types
            assertTrue(TypeUtils.isSingleValueType(TypeUtils.FLOAT), "float should be single value")
            assertTrue(TypeUtils.isSingleValueType(TypeUtils.DOUBLE), "double should be single value")
        }

        @Test
        @DisplayName("Pointer types should be single value types")
        fun testPointerSingleValueTypes() {
            val i32Ptr = PointerType(TypeUtils.I32)
            assertTrue(TypeUtils.isSingleValueType(i32Ptr), "i32* should be single value")
            
            val floatPtr = PointerType(TypeUtils.FLOAT)
            assertTrue(TypeUtils.isSingleValueType(floatPtr), "float* should be single value")
            
            val voidPtr = PointerType(TypeUtils.VOID)
            assertTrue(TypeUtils.isSingleValueType(voidPtr), "void* should be single value")
        }

        @Test
        @DisplayName("Aggregate types should not be single value types")
        fun testAggregateNotSingleValue() {
            val arrayType = ArrayType(10, TypeUtils.I32)
            assertFalse(TypeUtils.isSingleValueType(arrayType), "array types should not be single value")
            
            val structType = StructType(listOf(TypeUtils.I32, TypeUtils.I64))
            assertFalse(TypeUtils.isSingleValueType(structType), "struct types should not be single value")
        }

        @Test
        @DisplayName("Function and special types should not be single value types")
        fun testFunctionAndSpecialNotSingleValue() {
            val funcType = FunctionType(TypeUtils.I32, listOf(TypeUtils.I32))
            assertFalse(TypeUtils.isSingleValueType(funcType), "function types should not be single value")
            
            assertFalse(TypeUtils.isSingleValueType(TypeUtils.VOID), "void should not be single value")
            assertFalse(TypeUtils.isSingleValueType(TypeUtils.LABEL), "label should not be single value")
            assertFalse(TypeUtils.isSingleValueType(TypeUtils.METADATA), "metadata should not be single value")
        }
    }

    @Nested
    @DisplayName("Aggregate Type Tests")
    inner class AggregateTypeTests {

        @Test
        @DisplayName("Array types should be aggregate types")
        fun testArrayAggregateTypes() {
            val i32Array = ArrayType(10, TypeUtils.I32)
            assertTrue(TypeUtils.isAggregateType(i32Array), "i32[10] should be aggregate")
            
            val floatArray = ArrayType(5, TypeUtils.FLOAT)
            assertTrue(TypeUtils.isAggregateType(floatArray), "float[5] should be aggregate")
            
            val structArray = ArrayType(3, StructType(listOf(TypeUtils.I32, TypeUtils.I64)))
            assertTrue(TypeUtils.isAggregateType(structArray), "struct array should be aggregate")
        }

        @Test
        @DisplayName("Struct types should be aggregate types")
        fun testStructAggregateTypes() {
            val simpleStruct = StructType(listOf(TypeUtils.I32, TypeUtils.I64))
            assertTrue(TypeUtils.isAggregateType(simpleStruct), "simple struct should be aggregate")
            
            val packedStruct = StructType(listOf(TypeUtils.I8, TypeUtils.I16), true)
            assertTrue(TypeUtils.isAggregateType(packedStruct), "packed struct should be aggregate")
            
            val emptyStruct = StructType(emptyList())
            assertTrue(TypeUtils.isAggregateType(emptyStruct), "empty struct should be aggregate")
            
            val nestedStruct = StructType(listOf(
                TypeUtils.I32,
                ArrayType(5, TypeUtils.I8),
                StructType(listOf(TypeUtils.FLOAT, TypeUtils.DOUBLE))
            ))
            assertTrue(TypeUtils.isAggregateType(nestedStruct), "nested struct should be aggregate")
        }

        @Test
        @DisplayName("Non-aggregate types should not be aggregate types")
        fun testNonAggregateTypes() {
            // Primitive types
            assertFalse(TypeUtils.isAggregateType(TypeUtils.I32), "i32 should not be aggregate")
            assertFalse(TypeUtils.isAggregateType(TypeUtils.FLOAT), "float should not be aggregate")
            assertFalse(TypeUtils.isAggregateType(TypeUtils.VOID), "void should not be aggregate")
            
            // Derived types (except array and struct)
            val ptrType = PointerType(TypeUtils.I32)
            assertFalse(TypeUtils.isAggregateType(ptrType), "pointer should not be aggregate")
            
            val funcType = FunctionType(TypeUtils.I32, listOf(TypeUtils.I32))
            assertFalse(TypeUtils.isAggregateType(funcType), "function should not be aggregate")
        }
    }

    @Nested
    @DisplayName("Type Classification Tests")
    inner class TypeClassificationTests {

        @Test
        @DisplayName("Integer type classification should work correctly")
        fun testIntegerTypeClassification() {
            assertTrue(TypeUtils.isIntegerType(TypeUtils.I1), "i1 should be integer")
            assertTrue(TypeUtils.isIntegerType(TypeUtils.I8), "i8 should be integer")
            assertTrue(TypeUtils.isIntegerType(TypeUtils.I16), "i16 should be integer")
            assertTrue(TypeUtils.isIntegerType(TypeUtils.I32), "i32 should be integer")
            assertTrue(TypeUtils.isIntegerType(TypeUtils.I64), "i64 should be integer")
            assertTrue(TypeUtils.isIntegerType(TypeUtils.I128), "i128 should be integer")
            
            assertFalse(TypeUtils.isIntegerType(TypeUtils.FLOAT), "float should not be integer")
            assertFalse(TypeUtils.isIntegerType(TypeUtils.DOUBLE), "double should not be integer")
            assertFalse(TypeUtils.isIntegerType(TypeUtils.VOID), "void should not be integer")
            
            val ptrType = PointerType(TypeUtils.I32)
            assertFalse(TypeUtils.isIntegerType(ptrType), "pointer should not be integer")
        }

        @Test
        @DisplayName("Floating-point type classification should work correctly")
        fun testFloatingPointTypeClassification() {
            assertTrue(TypeUtils.isFloatingPointType(TypeUtils.FLOAT), "float should be floating-point")
            assertTrue(TypeUtils.isFloatingPointType(TypeUtils.DOUBLE), "double should be floating-point")
            
            assertFalse(TypeUtils.isFloatingPointType(TypeUtils.I32), "i32 should not be floating-point")
            assertFalse(TypeUtils.isFloatingPointType(TypeUtils.VOID), "void should not be floating-point")
            
            val ptrType = PointerType(TypeUtils.FLOAT)
            assertFalse(TypeUtils.isFloatingPointType(ptrType), "pointer should not be floating-point")
        }

        @Test
        @DisplayName("Pointer type classification should work correctly")
        fun testPointerTypeClassification() {
            val i32Ptr = PointerType(TypeUtils.I32)
            assertTrue(TypeUtils.isPointerTy(i32Ptr), "i32* should be pointer")
            
            val floatPtr = PointerType(TypeUtils.FLOAT)
            assertTrue(TypeUtils.isPointerTy(floatPtr), "float* should be pointer")
            
            val voidPtr = PointerType(TypeUtils.VOID)
            assertTrue(TypeUtils.isPointerTy(voidPtr), "void* should be pointer")
            
            val funcPtr = PointerType(FunctionType(TypeUtils.I32, emptyList()))
            assertTrue(TypeUtils.isPointerTy(funcPtr), "function pointer should be pointer")
            
            assertFalse(TypeUtils.isPointerTy(TypeUtils.I32), "i32 should not be pointer")
            assertFalse(TypeUtils.isPointerTy(TypeUtils.VOID), "void should not be pointer")
        }
    }

    @Nested
    @DisplayName("Type Relationship Tests")
    inner class TypeRelationshipTests {

        @Test
        @DisplayName("Element type extraction should work correctly")
        fun testElementTypeExtraction() {
            // Array types
            val i32Array = ArrayType(10, TypeUtils.I32)
            assertEquals(TypeUtils.I32, TypeUtils.getElementType(i32Array), "Array element type should be correct")
            
            val floatArray = ArrayType(5, TypeUtils.FLOAT)
            assertEquals(TypeUtils.FLOAT, TypeUtils.getElementType(floatArray), "Array element type should be correct")
            
            // Pointer types
            val i32Ptr = PointerType(TypeUtils.I32)
            assertEquals(TypeUtils.I32, TypeUtils.getElementType(i32Ptr), "Pointer element type should be correct")
            
            val voidPtr = PointerType(TypeUtils.VOID)
            assertEquals(TypeUtils.VOID, TypeUtils.getElementType(voidPtr), "Pointer element type should be correct")
            
            // Non-applicable types
            assertNull(TypeUtils.getElementType(TypeUtils.I32), "Non-array/pointer should return null")
            assertNull(TypeUtils.getElementType(TypeUtils.VOID), "Non-array/pointer should return null")
            
            val funcType = FunctionType(TypeUtils.I32, listOf(TypeUtils.I32))
            assertNull(TypeUtils.getElementType(funcType), "Function type should return null")
            
            val structType = StructType(listOf(TypeUtils.I32, TypeUtils.I64))
            assertNull(TypeUtils.getElementType(structType), "Struct type should return null")
        }

        @Test
        @DisplayName("Function parameter count should work correctly")
        fun testFunctionParameterCount() {
            val noParamsFunc = FunctionType(TypeUtils.I32, emptyList())
            assertEquals(0, TypeUtils.getFunctionNumParams(noParamsFunc), "No-param function should have 0 parameters")
            
            val singleParamFunc = FunctionType(TypeUtils.I32, listOf(TypeUtils.I32))
            assertEquals(1, TypeUtils.getFunctionNumParams(singleParamFunc), "Single-param function should have 1 parameter")
            
            val multiParamFunc = FunctionType(TypeUtils.VOID, listOf(TypeUtils.I32, TypeUtils.FLOAT, TypeUtils.DOUBLE))
            assertEquals(3, TypeUtils.getFunctionNumParams(multiParamFunc), "Multi-param function should have 3 parameters")
            
            val varArgFunc = FunctionType(TypeUtils.I32, listOf(TypeUtils.I32), true)
            assertEquals(1, TypeUtils.getFunctionNumParams(varArgFunc), "Vararg function should count only fixed parameters")
            
            // Non-function types
            assertNull(TypeUtils.getFunctionNumParams(TypeUtils.I32), "Non-function should return null")
            assertNull(TypeUtils.getFunctionNumParams(PointerType(TypeUtils.I32)), "Pointer should return null")
        }

        @Test
        @DisplayName("Function parameter type extraction should work correctly")
        fun testFunctionParameterTypeExtraction() {
            val funcType = FunctionType(TypeUtils.I32, listOf(TypeUtils.I8, TypeUtils.I16, TypeUtils.I32))
            
            assertEquals(TypeUtils.I8, TypeUtils.getFunctionParamType(funcType, 0), "First parameter should be i8")
            assertEquals(TypeUtils.I16, TypeUtils.getFunctionParamType(funcType, 1), "Second parameter should be i16")
            assertEquals(TypeUtils.I32, TypeUtils.getFunctionParamType(funcType, 2), "Third parameter should be i32")
            
            // Out of bounds
            assertNull(TypeUtils.getFunctionParamType(funcType, -1), "Negative index should return null")
            assertNull(TypeUtils.getFunctionParamType(funcType, 3), "Out of bounds index should return null")
            assertNull(TypeUtils.getFunctionParamType(funcType, 100), "Out of bounds index should return null")
            
            // Non-function types
            assertNull(TypeUtils.getFunctionParamType(TypeUtils.I32, 0), "Non-function should return null")
            assertNull(TypeUtils.getFunctionParamType(PointerType(TypeUtils.I32), 0), "Pointer should return null")
        }
    }

    @Nested
    @DisplayName("Type Casting Tests")
    inner class TypeCastingTests {

        @Test
        @DisplayName("Lossless bitcast compatibility should work correctly")
        fun testLosslessBitcastCompatibility() {
            // Same types
            assertTrue(TypeUtils.canLosslesslyBitCastTo(TypeUtils.I32, TypeUtils.I32), "Same types should be bitcastable")
            assertTrue(TypeUtils.canLosslesslyBitCastTo(TypeUtils.FLOAT, TypeUtils.FLOAT), "Same types should be bitcastable")
            
            // Pointer to pointer
            val i32Ptr = PointerType(TypeUtils.I32)
            val floatPtr = PointerType(TypeUtils.FLOAT)
            assertTrue(TypeUtils.canLosslesslyBitCastTo(i32Ptr, floatPtr), "Any pointer to any pointer should be bitcastable")
            assertTrue(TypeUtils.canLosslesslyBitCastTo(floatPtr, i32Ptr), "Any pointer to any pointer should be bitcastable")
            
            // Integer types of same size
            assertTrue(TypeUtils.canLosslesslyBitCastTo(TypeUtils.I32, IntegerType(32)), "Same size integers should be bitcastable")
            assertTrue(TypeUtils.canLosslesslyBitCastTo(TypeUtils.I64, IntegerType(64)), "Same size integers should be bitcastable")
            
            // Floating-point types of same size
            assertTrue(TypeUtils.canLosslesslyBitCastTo(TypeUtils.FLOAT, FloatingPointType.FloatType), "Same size floats should be bitcastable")
            assertTrue(TypeUtils.canLosslesslyBitCastTo(TypeUtils.DOUBLE, FloatingPointType.DoubleType), "Same size doubles should be bitcastable")
            
            // Integer to floating-point of same size
            assertTrue(TypeUtils.canLosslesslyBitCastTo(TypeUtils.I32, TypeUtils.FLOAT), "i32 to float should be bitcastable")
            assertTrue(TypeUtils.canLosslesslyBitCastTo(TypeUtils.I64, TypeUtils.DOUBLE), "i64 to double should be bitcastable")
            assertTrue(TypeUtils.canLosslesslyBitCastTo(TypeUtils.FLOAT, TypeUtils.I32), "float to i32 should be bitcastable")
            assertTrue(TypeUtils.canLosslesslyBitCastTo(TypeUtils.DOUBLE, TypeUtils.I64), "double to i64 should be bitcastable")
            
            // Different sizes should not be bitcastable
            assertFalse(TypeUtils.canLosslesslyBitCastTo(TypeUtils.I32, TypeUtils.I64), "Different size integers should not be bitcastable")
            assertFalse(TypeUtils.canLosslesslyBitCastTo(TypeUtils.FLOAT, TypeUtils.DOUBLE), "Different size floats should not be bitcastable")
            assertFalse(TypeUtils.canLosslesslyBitCastTo(TypeUtils.I32, TypeUtils.DOUBLE), "Different size types should not be bitcastable")
            
            // Non-scalar types should not be bitcastable
            val arrayType = ArrayType(10, TypeUtils.I32)
            val structType = StructType(listOf(TypeUtils.I32, TypeUtils.I64))
            assertFalse(TypeUtils.canLosslesslyBitCastTo(arrayType, TypeUtils.I32), "Array should not be bitcastable to scalar")
            assertFalse(TypeUtils.canLosslesslyBitCastTo(structType, TypeUtils.I32), "Struct should not be bitcastable to scalar")
        }

        @Test
        @DisplayName("Common type finding should work correctly")
        fun testCommonTypeFinding() {
            // Same types
            assertEquals(TypeUtils.I32, TypeUtils.getCommonType(TypeUtils.I32, TypeUtils.I32), "Same types should return that type")
            assertEquals(TypeUtils.FLOAT, TypeUtils.getCommonType(TypeUtils.FLOAT, TypeUtils.FLOAT), "Same types should return that type")
            
            // Integer types - should return larger
            assertEquals(TypeUtils.I64, TypeUtils.getCommonType(TypeUtils.I32, TypeUtils.I64), "Should return larger integer")
            assertEquals(TypeUtils.I64, TypeUtils.getCommonType(TypeUtils.I64, TypeUtils.I32), "Should return larger integer")
            
            // Floating-point types - should return larger
            assertEquals(TypeUtils.DOUBLE, TypeUtils.getCommonType(TypeUtils.FLOAT, TypeUtils.DOUBLE), "Should return larger float")
            assertEquals(TypeUtils.DOUBLE, TypeUtils.getCommonType(TypeUtils.DOUBLE, TypeUtils.FLOAT), "Should return larger float")
            
            // Pointer types - should return i8*
            val i32Ptr = PointerType(TypeUtils.I32)
            val floatPtr = PointerType(TypeUtils.FLOAT)
            val voidPtr = PointerType(TypeUtils.VOID)
            val commonPtr = TypeUtils.getCommonType(i32Ptr, floatPtr)
            assertTrue(commonPtr is PointerType, "Should return pointer type")
            assertEquals(TypeUtils.I8, (commonPtr as PointerType).pointeeType, "Should return i8*")
            
            // Bitcastable types
            assertEquals(TypeUtils.FLOAT, TypeUtils.getCommonType(TypeUtils.I32, TypeUtils.FLOAT), "Bitcastable should return target")
            assertEquals(TypeUtils.I32, TypeUtils.getCommonType(TypeUtils.FLOAT, TypeUtils.I32), "Bitcastable should return target")
            
            // No common type
            assertNull(TypeUtils.getCommonType(TypeUtils.I32, TypeUtils.VOID), "No common type for scalar and void")
            assertNull(TypeUtils.getCommonType(TypeUtils.I32, ArrayType(10, TypeUtils.I32)), "No common type for scalar and array")
        }
    }
}