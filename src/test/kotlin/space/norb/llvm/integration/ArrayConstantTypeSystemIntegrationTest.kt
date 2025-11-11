package space.norb.llvm.integration

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import space.norb.llvm.types.ArrayType
import space.norb.llvm.types.IntegerType
import space.norb.llvm.types.FloatingPointType
import space.norb.llvm.types.PointerType
import space.norb.llvm.values.constants.ArrayConstant
import space.norb.llvm.values.constants.IntConstant
import space.norb.llvm.values.constants.FloatConstant
import space.norb.llvm.values.constants.NullPointerConstant

/**
 * Integration tests for ArrayConstant with Type system.
 */
@DisplayName("ArrayConstant Type System Integration Tests")
class ArrayConstantTypeSystemIntegrationTest {

    @Test
    @DisplayName("ArrayType should correctly identify as array type")
    fun testArrayTypeIdentification() {
        val arrayType = ArrayType(5, IntegerType.I32)
        
        assertTrue(arrayType.isArrayType())
        assertFalse(arrayType.isPrimitiveType())
        assertTrue(arrayType.isDerivedType())
        assertFalse(arrayType.isIntegerType())
        assertFalse(arrayType.isFloatingPointType())
        assertFalse(arrayType.isPointerType())
        assertFalse(arrayType.isFunctionType())
        assertFalse(arrayType.isStructType())
        
        assertEquals("[5 x i32]", arrayType.toString())
        assertNull(arrayType.getPrimitiveSizeInBits())
    }

    @Test
    @DisplayName("ArrayConstant should have correct type properties")
    fun testArrayConstantTypeProperties() {
        val arrayType = ArrayType(3, IntegerType.I32)
        val arrayConstant = ArrayConstant.create(arrayType,
            IntConstant(1L, IntegerType.I32),
            IntConstant(2L, IntegerType.I32),
            IntConstant(3L, IntegerType.I32)
        )
        
        // Check type properties
        assertEquals(arrayType, arrayConstant.type)
        assertTrue(arrayConstant.type.isArrayType())
        assertEquals(3, (arrayConstant.type as ArrayType).numElements)
        assertEquals(IntegerType.I32, (arrayConstant.type as ArrayType).elementType)
    }

    @Test
    @DisplayName("ArrayConstant should enforce type compatibility")
    fun testArrayConstantTypeCompatibility() {
        val arrayType = ArrayType(2, IntegerType.I32)
        
        // Valid: all elements have correct type
        assertDoesNotThrow {
            ArrayConstant.create(arrayType,
                IntConstant(1L, IntegerType.I32),
                IntConstant(2L, IntegerType.I32)
            )
        }
        
        // Invalid: element has wrong type
        assertThrows(IllegalArgumentException::class.java) {
            ArrayConstant.create(arrayType,
                IntConstant(1L, IntegerType.I32),
                FloatConstant(2.0, FloatingPointType.FloatType) // Wrong type
            )
        }
        
        // Invalid: wrong number of elements
        assertThrows(IllegalArgumentException::class.java) {
            ArrayConstant.create(arrayType,
                IntConstant(1L, IntegerType.I32)
                // Missing second element
            )
        }
    }

    @Test
    @DisplayName("ArrayConstant should support nested array types")
    fun testArrayConstantNestedTypes() {
        val innerArrayType = ArrayType(2, IntegerType.I32)
        val outerArrayType = ArrayType(3, innerArrayType)
        
        val innerArray1 = ArrayConstant.create(innerArrayType,
            IntConstant(1L, IntegerType.I32),
            IntConstant(2L, IntegerType.I32)
        )
        
        val innerArray2 = ArrayConstant.create(innerArrayType,
            IntConstant(3L, IntegerType.I32),
            IntConstant(4L, IntegerType.I32)
        )
        
        val outerArray = ArrayConstant.create(outerArrayType, innerArray1, innerArray2, innerArray1)
        
        // Check type hierarchy
        assertTrue(outerArray.type.isArrayType())
        val outerArrayTypeCast = outerArray.type as ArrayType
        assertEquals(3, outerArrayTypeCast.numElements)
        assertTrue(outerArrayTypeCast.elementType.isArrayType())
        
        val innerArrayTypeCast = outerArrayTypeCast.elementType as ArrayType
        assertEquals(2, innerArrayTypeCast.numElements)
        assertEquals(IntegerType.I32, innerArrayTypeCast.elementType)
        
        assertEquals("[3 x [2 x i32]]", outerArray.type.toString())
    }

    @Test
    @DisplayName("ArrayConstant should support different element types")
    fun testArrayConstantDifferentElementTypes() {
        // Integer array
        val intArray = ArrayConstant.create(IntegerType.I32,
            IntConstant(10L, IntegerType.I32),
            IntConstant(20L, IntegerType.I32)
        )
        assertTrue(intArray.type.isArrayType())
        assertEquals(IntegerType.I32, (intArray.type as ArrayType).elementType)
        
        // Float array
        val floatArray = ArrayConstant.create(FloatingPointType.FloatType,
            FloatConstant(1.5, FloatingPointType.FloatType),
            FloatConstant(2.5, FloatingPointType.FloatType)
        )
        assertTrue(floatArray.type.isArrayType())
        assertEquals(FloatingPointType.FloatType, (floatArray.type as ArrayType).elementType)
        
        // Pointer array
        val pointerArray = ArrayConstant.create(PointerType,
            NullPointerConstant.create(IntegerType.I32),
            NullPointerConstant.create(IntegerType.I32)
        )
        assertTrue(pointerArray.type.isArrayType())
        assertEquals(PointerType, (pointerArray.type as ArrayType).elementType)
    }

    @Test
    @DisplayName("ArrayConstant should handle type equality correctly")
    fun testArrayConstantTypeEquality() {
        val arrayType1 = ArrayType(3, IntegerType.I32)
        val arrayType2 = ArrayType(3, IntegerType.I32)
        val arrayType3 = ArrayType(3, IntegerType.I64) // Different element type
        val arrayType4 = ArrayType(4, IntegerType.I32) // Different size
        
        assertEquals(arrayType1, arrayType2) // Same type
        assertNotEquals(arrayType1, arrayType3) // Different element type
        assertNotEquals(arrayType1, arrayType4) // Different size
        
        // Test ArrayConstant type equality
        val arrayConstant1 = ArrayConstant.create(arrayType1,
            IntConstant(1L, IntegerType.I32),
            IntConstant(2L, IntegerType.I32),
            IntConstant(3L, IntegerType.I32)
        )
        
        val arrayConstant2 = ArrayConstant.create(arrayType2,
            IntConstant(1L, IntegerType.I32),
            IntConstant(2L, IntegerType.I32),
            IntConstant(3L, IntegerType.I32)
        )
        
        assertEquals(arrayConstant1.type, arrayConstant2.type)
    }

    @Test
    @DisplayName("ArrayConstant should work with complex type combinations")
    fun testArrayConstantComplexTypes() {
        // Array of pointers to integers
        val intPtrArrayType = ArrayType(2, PointerType)
        val intPtrArray = ArrayConstant.create(intPtrArrayType,
            NullPointerConstant.create(IntegerType.I32),
            NullPointerConstant.create(IntegerType.I64)
        )
        assertEquals("[2 x ptr]", intPtrArray.type.toString())
        
        // Array of arrays of floats
        val floatArrayType = ArrayType(2, FloatingPointType.FloatType)
        val floatArrayArrayType = ArrayType(3, floatArrayType)
        val floatArray = ArrayConstant.create(floatArrayType,
            FloatConstant(1.0, FloatingPointType.FloatType),
            FloatConstant(2.0, FloatingPointType.FloatType)
        )
        val floatArrayArray = ArrayConstant.create(floatArrayArrayType, floatArray, floatArray, floatArray)
        assertEquals("[3 x [2 x float]]", floatArrayArray.type.toString())
    }

    @Test
    @DisplayName("ArrayConstant should validate element count matches type")
    fun testArrayConstantElementCountValidation() {
        val arrayType = ArrayType(3, IntegerType.I32)
        
        // Valid: correct number of elements
        assertDoesNotThrow {
            ArrayConstant.create(arrayType,
                IntConstant(1L, IntegerType.I32),
                IntConstant(2L, IntegerType.I32),
                IntConstant(3L, IntegerType.I32)
            )
        }
        
        // Invalid: too few elements
        assertThrows(IllegalArgumentException::class.java) {
            ArrayConstant.create(arrayType,
                IntConstant(1L, IntegerType.I32),
                IntConstant(2L, IntegerType.I32)
                // Missing third element
            )
        }
        
        // Invalid: too many elements
        assertThrows(IllegalArgumentException::class.java) {
            ArrayConstant.create(arrayType,
                IntConstant(1L, IntegerType.I32),
                IntConstant(2L, IntegerType.I32),
                IntConstant(3L, IntegerType.I32),
                IntConstant(4L, IntegerType.I32) // Extra element
            )
        }
    }
}