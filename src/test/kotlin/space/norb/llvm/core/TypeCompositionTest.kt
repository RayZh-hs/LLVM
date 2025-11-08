package space.norb.llvm.core

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import space.norb.llvm.types.*

/**
 * Comprehensive tests for complex type compositions and nested types.
 *
 * ## LLVM IR Compliance Notice
 *
 * **UN-TYPED POINTER IMPLEMENTATION**: These tests validate the new un-typed pointer
 * implementation which follows the latest LLVM IR model where all pointers are of a single
 * type (similar to `void*` in C) and type information is conveyed through other mechanisms.
 *
 * This implementation is **compliant** with the latest LLVM IR standard, which has
 * moved to un-typed pointers where all pointers are of a single type regardless of
 * the pointee type.
 *
 * ## Current Test Coverage
 *
 * This covers testing nested pointer types, complex function types, multi-dimensional arrays,
 * nested structs, and mixed compositions using the un-typed pointer model.
 */
@DisplayName("Type Composition Tests (Un-typed Pointer Implementation)")
class TypeCompositionTest {

    @Nested
    @DisplayName("Nested Pointer Type Tests")
    inner class NestedPointerTypeTests {

        @Test
        @DisplayName("Pointer to pointer types should work correctly")
        fun testPointerToPointer() {
            val i32Ptr = Type.getPointerType()
            val i32PtrPtr = Type.getPointerType() // All pointers are un-typed, so nested is same
            val i32PtrPtrPtr = Type.getPointerType()
            
            assertEquals("ptr", i32Ptr.toString(), "Simple pointer should have correct representation")
            assertEquals("ptr", i32PtrPtr.toString(), "Pointer to pointer should have correct representation")
            assertEquals("ptr", i32PtrPtrPtr.toString(), "Triple pointer should have correct representation")
            
            assertTrue(i32Ptr.isPointerType(), "Simple pointer should be pointer type")
            assertTrue(i32PtrPtr.isPointerType(), "Pointer to pointer should be pointer type")
            assertTrue(i32PtrPtrPtr.isPointerType(), "Triple pointer should be pointer type")
            
            // All un-typed pointers should be equal
            assertEquals(i32Ptr, i32PtrPtr, "All un-typed pointers should be equal")
            assertEquals(i32Ptr, i32PtrPtrPtr, "All un-typed pointers should be equal")
        }

        @Test
        @DisplayName("Pointer to array types should work correctly")
        fun testPointerToArray() {
            val i32Array = ArrayType(10, TypeUtils.I32)
            
            val arrayPtr = Type.getPointerType()
            assertEquals("ptr", arrayPtr.toString(), "Pointer to array should have correct representation")
            assertTrue(arrayPtr.isPointerType(), "Pointer to array should be pointer type")
            assertFalse(arrayPtr.isArrayType(), "Pointer to array should not be array type")
        }

        @Test
        @DisplayName("Pointer to struct types should work correctly")
        fun testPointerToStruct() {
            val structType = StructType.AnonymousStructType(listOf(TypeUtils.I32, TypeUtils.I64))
            
            val structPtr = Type.getPointerType()
            assertEquals("ptr", structPtr.toString(), "Pointer to struct should have correct representation")
            assertTrue(structPtr.isPointerType(), "Pointer to struct should be pointer type")
            assertFalse(structPtr.isStructType(), "Pointer to struct should not be struct type")
        }

        @Test
        @DisplayName("Pointer to function types should work correctly")
        fun testPointerToFunction() {
            val funcType = FunctionType(TypeUtils.I32, listOf(TypeUtils.I32, TypeUtils.I64))
            
            val funcPtr = Type.getPointerType()
            assertEquals("ptr", funcPtr.toString(), "Pointer to function should have correct representation")
            assertTrue(funcPtr.isPointerType(), "Pointer to function should be pointer type")
            assertFalse(funcPtr.isFunctionType(), "Pointer to function should not be function type")
        }
    }

    @Nested
    @DisplayName("Complex Function Type Tests")
    inner class ComplexFunctionTypeTests {

        @Test
        @DisplayName("Function types with multiple parameters should work correctly")
        fun testMultipleParameterFunction() {
            val params = listOf(TypeUtils.I8, TypeUtils.I16, TypeUtils.I32, TypeUtils.I64, TypeUtils.FLOAT, TypeUtils.DOUBLE)
            val funcType = FunctionType(TypeUtils.VOID, params)
            
            assertEquals("void (i8, i16, i32, i64, float, double)", funcType.toString())
            assertEquals(TypeUtils.VOID, funcType.returnType)
            assertEquals(6, funcType.paramTypes.size)
            assertEquals(params, funcType.paramTypes)
            assertFalse(funcType.isVarArg)
            assertTrue(funcType.isFunctionType())
        }

        @Test
        @DisplayName("Function types with complex parameter types should work correctly")
        fun testComplexParameterFunction() {
            val i32Array = ArrayType(10, TypeUtils.I32)
            val structType = StructType.AnonymousStructType(listOf(TypeUtils.FLOAT, TypeUtils.DOUBLE))
            
            val i32Ptr = Type.getPointerType()
            val funcPtrType = Type.getPointerType()
            val structPtr = Type.getPointerType()
            
            val params = listOf(i32Array, structType, i32Ptr, funcPtrType)
            val funcType = FunctionType(structPtr, params)
            
            assertEquals("ptr ([10 x i32], { float, double }, ptr, ptr)", funcType.toString())
            assertTrue(funcType.returnType.isPointerType())
            assertEquals(4, funcType.paramTypes.size)
            assertEquals(i32Array, funcType.paramTypes[0])
            assertEquals(structType, funcType.paramTypes[1])
            assertEquals(i32Ptr, funcType.paramTypes[2])
            assertEquals(funcPtrType, funcType.paramTypes[3])
        }

        @Test
        @DisplayName("Function types returning complex types should work correctly")
        fun testComplexReturnFunction() {
            val arrayType = ArrayType(5, ArrayType(10, TypeUtils.I32))
            
            val floatPtr = Type.getPointerType()
            val structType = StructType.AnonymousStructType(listOf(TypeUtils.I32, floatPtr))
            val funcType = FunctionType(arrayType, listOf(structType))
            
            assertEquals("[5 x [10 x i32]] ({ i32, ptr })", funcType.toString())
            assertTrue(funcType.returnType.isArrayType())
            assertEquals(1, funcType.paramTypes.size)
            assertTrue(funcType.paramTypes[0].isStructType())
        }

        @Test
        @DisplayName("Vararg function types should work correctly")
        fun testVarargFunction() {
            val funcType1 = FunctionType(TypeUtils.I32, listOf(TypeUtils.I32), true)
            assertEquals("i32 (i32, ...)", funcType1.toString())
            assertTrue(funcType1.isVarArg)
            
            val funcType2 = FunctionType(TypeUtils.VOID, emptyList(), true)
            assertEquals("void (...)", funcType2.toString())
            assertTrue(funcType2.isVarArg)
            assertEquals(0, funcType2.paramTypes.size)
            
            val funcType3 = FunctionType(TypeUtils.DOUBLE, listOf(TypeUtils.FLOAT, TypeUtils.I64), true)
            assertEquals("double (float, i64, ...)", funcType3.toString())
            assertTrue(funcType3.isVarArg)
            assertEquals(2, funcType3.paramTypes.size)
        }
    }

    @Nested
    @DisplayName("Multi-dimensional Array Tests")
    inner class MultiDimensionalArrayTests {

        @Test
        @DisplayName("2D arrays should work correctly")
        fun testTwoDimensionalArrays() {
            val i32Type = TypeUtils.I32
            val i32Array1D = ArrayType(10, i32Type)
            val i32Array2D = ArrayType(5, i32Array1D)
            
            assertEquals("[10 x i32]", i32Array1D.toString())
            assertEquals("[5 x [10 x i32]]", i32Array2D.toString())
            
            assertEquals(10, i32Array1D.numElements)
            assertEquals(i32Type, i32Array1D.elementType)
            
            assertEquals(5, i32Array2D.numElements)
            assertEquals(i32Array1D, i32Array2D.elementType)
            
            assertTrue(i32Array1D.isArrayType())
            assertTrue(i32Array2D.isArrayType())
        }

        @Test
        @DisplayName("3D arrays should work correctly")
        fun testThreeDimensionalArrays() {
            val floatType = TypeUtils.FLOAT
            val floatArray1D = ArrayType(8, floatType)
            val floatArray2D = ArrayType(4, floatArray1D)
            val floatArray3D = ArrayType(2, floatArray2D)
            
            assertEquals("[8 x float]", floatArray1D.toString())
            assertEquals("[4 x [8 x float]]", floatArray2D.toString())
            assertEquals("[2 x [4 x [8 x float]]]", floatArray3D.toString())
            
            assertEquals(2, floatArray3D.numElements)
            assertEquals(floatArray2D, floatArray3D.elementType)
            assertTrue(floatArray3D.elementType.isArrayType())
            assertTrue((floatArray3D.elementType as ArrayType).elementType.isArrayType())
        }

        @Test
        @DisplayName("Arrays of complex element types should work correctly")
        fun testComplexElementArrays() {
            val structType = StructType.AnonymousStructType(listOf(TypeUtils.I32, TypeUtils.I64))
            val structArray = ArrayType(3, structType)
            
            val funcType = FunctionType(TypeUtils.VOID, listOf(TypeUtils.I32))
            val funcArray = ArrayType(5, funcType)
            
            val ptrType = Type.getPointerType()
            val ptrArray = ArrayType(7, ptrType)
            
            assertEquals("[3 x { i32, i64 }]", structArray.toString())
            assertEquals("[5 x void (i32)]", funcArray.toString())
            assertEquals("[7 x ptr]", ptrArray.toString())
            
            assertTrue(structArray.elementType.isStructType())
            assertTrue(funcArray.elementType.isFunctionType())
            assertTrue(ptrArray.elementType.isPointerType())
        }
    }

    @Nested
    @DisplayName("Nested Struct Type Tests")
    inner class NestedStructTypeTests {

        @Test
        @DisplayName("Structs containing arrays should work correctly")
        fun testStructContainingArrays() {
            val i32Array = ArrayType(10, TypeUtils.I32)
            val floatArray = ArrayType(5, TypeUtils.FLOAT)
            val structType = StructType.AnonymousStructType(listOf(TypeUtils.I64, i32Array, floatArray))
            
            assertEquals("{ i64, [10 x i32], [5 x float] }", structType.toString())
            assertEquals(3, structType.elementTypes.size)
            assertEquals(TypeUtils.I64, structType.elementTypes[0])
            assertEquals(i32Array, structType.elementTypes[1])
            assertEquals(floatArray, structType.elementTypes[2])
            
            assertTrue(structType.elementTypes[1].isArrayType())
            assertTrue(structType.elementTypes[2].isArrayType())
        }

        @Test
        @DisplayName("Structs containing other structs should work correctly")
        fun testStructContainingStructs() {
            val innerStruct1 = StructType.AnonymousStructType(listOf(TypeUtils.I8, TypeUtils.I16))
            val innerStruct2 = StructType.AnonymousStructType(listOf(TypeUtils.FLOAT, TypeUtils.DOUBLE))
            val outerStruct = StructType.AnonymousStructType(listOf(TypeUtils.I32, innerStruct1, innerStruct2))
            
            assertEquals("{ i8, i16 }", innerStruct1.toString())
            assertEquals("{ float, double }", innerStruct2.toString())
            assertEquals("{ i32, { i8, i16 }, { float, double } }", outerStruct.toString())
            
            assertEquals(3, outerStruct.elementTypes.size)
            assertTrue(outerStruct.elementTypes[1].isStructType())
            assertTrue(outerStruct.elementTypes[2].isStructType())
        }

        @Test
        @DisplayName("Deeply nested structs should work correctly")
        fun testDeeplyNestedStructs() {
            val level1Struct = StructType.AnonymousStructType(listOf(TypeUtils.I8))
            val level2Struct = StructType.AnonymousStructType(listOf(TypeUtils.I16, level1Struct))
            val level3Struct = StructType.AnonymousStructType(listOf(TypeUtils.I32, level2Struct))
            val level4Struct = StructType.AnonymousStructType(listOf(TypeUtils.I64, level3Struct))
            
            assertEquals("{ i8 }", level1Struct.toString())
            assertEquals("{ i16, { i8 } }", level2Struct.toString())
            assertEquals("{ i32, { i16, { i8 } } }", level3Struct.toString())
            assertEquals("{ i64, { i32, { i16, { i8 } } } }", level4Struct.toString())
            
            // Test deep nesting access
            val deepestStruct = level4Struct.elementTypes[1] as StructType.AnonymousStructType
            val deeperStruct = deepestStruct.elementTypes[1] as StructType.AnonymousStructType
            val deepStruct = deeperStruct.elementTypes[1] as StructType.AnonymousStructType
            assertEquals(TypeUtils.I8, deepStruct.elementTypes[0])
        }

        @Test
        @DisplayName("Packed nested structs should work correctly")
        fun testPackedNestedStructs() {
            val innerPacked = StructType.AnonymousStructType(listOf(TypeUtils.I8, TypeUtils.I16), true)
            val innerUnpacked = StructType.AnonymousStructType(listOf(TypeUtils.I32, TypeUtils.I64), false)
            val outerPacked = StructType.AnonymousStructType(listOf(innerPacked, innerUnpacked), true)
            val outerUnpacked = StructType.AnonymousStructType(listOf(innerPacked, innerUnpacked), false)
            
            assertEquals("<{ i8, i16 }>", innerPacked.toString())
            assertEquals("{ i32, i64 }", innerUnpacked.toString())
            assertEquals("<{ <{ i8, i16 }>, { i32, i64 } }>", outerPacked.toString())
            assertEquals("{ <{ i8, i16 }>, { i32, i64 } }", outerUnpacked.toString())
            
            assertTrue(innerPacked.isPacked)
            assertFalse(innerUnpacked.isPacked)
            assertTrue(outerPacked.isPacked)
            assertFalse(outerUnpacked.isPacked)
        }
    }

    @Nested
    @DisplayName("Mixed Composition Tests")
    inner class MixedCompositionTests {

        @Test
        @DisplayName("Function returning pointer to struct should work correctly")
        fun testFunctionReturningPointerToStruct() {
            val structType = StructType.AnonymousStructType(listOf(TypeUtils.I32, TypeUtils.FLOAT))
            
            val structPtr = Type.getPointerType()
            val funcType = FunctionType(structPtr, listOf(TypeUtils.I8, TypeUtils.I16))
            
            assertEquals("ptr (i8, i16)", funcType.toString())
            assertTrue(funcType.returnType.isPointerType())
            assertEquals(2, funcType.paramTypes.size)
        }

        @Test
        @DisplayName("Array of function pointers should work correctly")
        fun testArrayOfFunctionPointers() {
            val funcType = FunctionType(TypeUtils.I64, listOf(TypeUtils.I32))
            
            val funcPtr = Type.getPointerType()
            val funcPtrArray = ArrayType(5, funcPtr)
            
            assertEquals("ptr", funcPtr.toString())
            assertEquals("[5 x ptr]", funcPtrArray.toString())
            
            assertTrue(funcPtrArray.elementType.isPointerType())
        }

        @Test
        @DisplayName("Struct containing function pointers and arrays should work correctly")
        fun testStructWithFunctionPointersAndArrays() {
            val funcType1 = FunctionType(TypeUtils.VOID, listOf(TypeUtils.I32))
            val funcType2 = FunctionType(TypeUtils.FLOAT, listOf(TypeUtils.DOUBLE))
            val i32Array = ArrayType(10, TypeUtils.I32)
            
            val funcPtr1 = Type.getPointerType()
            val funcPtr2 = Type.getPointerType()
            val structType = StructType(listOf(funcPtr1, i32Array, funcPtr2))
            
            assertEquals("{ ptr, [10 x i32], ptr }", structType.toString())
            
            assertTrue(structType.elementTypes[0].isPointerType())
            assertTrue(structType.elementTypes[1].isArrayType())
            assertTrue(structType.elementTypes[2].isPointerType())
        }

        @Test
        @DisplayName("Complex nested composition should work correctly")
        fun testComplexNestedComposition() {
            // Create a complex type: pointer to array of structs containing function pointers
            val innerFuncType = FunctionType(TypeUtils.I32, listOf(TypeUtils.I8))
            val innerStruct = StructType.AnonymousStructType(listOf(TypeUtils.I64, Type.getPointerType()))
            val structArray = ArrayType(3, innerStruct)
            
            // Test with un-typed pointers (default mode)
            val innerFuncPtr = Type.getPointerType()
            val complexType = Type.getPointerType()
            
            assertEquals("ptr", innerFuncPtr.toString())
            assertEquals("{ i64, ptr }", innerStruct.toString())
            assertEquals("[3 x { i64, ptr }]", structArray.toString())
            assertEquals("ptr", complexType.toString())
            
            // Verify the structure
            assertTrue(complexType.isPointerType())
            assertTrue(structArray.isArrayType())
            assertTrue(innerStruct.isStructType())
            assertTrue(innerStruct.elementTypes[0].isIntegerType())
            assertTrue(innerStruct.elementTypes[1].isPointerType())
        }

        @Test
        @DisplayName("Function with complex parameters and return type should work correctly")
        fun testFunctionWithComplexParametersAndReturn() {
            // Create complex parameter types
            val structType = StructType.AnonymousStructType(listOf(TypeUtils.I32, TypeUtils.FLOAT))
            val structArray = ArrayType(5, structType)
            val funcType = FunctionType(TypeUtils.VOID, listOf(TypeUtils.I8))
            
            val structArrayPtr = Type.getPointerType()
            val funcPtr = Type.getPointerType()
            val complexStruct = StructType.AnonymousStructType(listOf(TypeUtils.I64, structArrayPtr, funcPtr))
            
            // Create function with complex parameters and return type
            val complexFuncType = FunctionType(
                Type.getPointerType(),
                listOf(complexStruct, structArrayPtr, funcPtr)
            )
            
            assertEquals("ptr ({ i64, ptr, ptr }, ptr, ptr)", complexFuncType.toString())
            
            assertTrue(complexFuncType.returnType.isPointerType())
            assertEquals(3, complexFuncType.paramTypes.size)
            assertTrue(complexFuncType.paramTypes[0].isStructType())
            assertTrue(complexFuncType.paramTypes[1].isPointerType())
            assertTrue(complexFuncType.paramTypes[2].isPointerType())
        }
    }
}