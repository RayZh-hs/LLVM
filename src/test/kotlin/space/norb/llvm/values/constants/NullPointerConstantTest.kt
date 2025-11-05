package space.norb.llvm.values.constants

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertThrows
import space.norb.llvm.types.IntegerType
import space.norb.llvm.types.PointerType

/**
 * Unit tests for NullPointerConstant.
 */
@DisplayName("NullPointerConstant Tests")
class NullPointerConstantTest {

    @Test
    @DisplayName("NullPointerConstant should be created with correct type")
    fun testNullPointerConstantCreation() {
        val elementType = IntegerType.I32
        val constant = NullPointerConstant.create(elementType)
        
        assertEquals("null", constant.name, "NullPointerConstant should have name 'null'")
        assertEquals(PointerType, constant.type, "NullPointerConstant should have PointerType")
        assertTrue(constant.isNullValue(), "NullPointerConstant should be null")
        assertFalse(constant.isZeroValue(), "NullPointerConstant should not be zero")
        assertFalse(constant.isOneValue(), "NullPointerConstant should not be one")
    }

    @Test
    @DisplayName("NullPointerConstant.create should work with different element types")
    fun testNullPointerConstantCreateWithDifferentElementTypes() {
        val i8Constant = NullPointerConstant.create(IntegerType.I8)
        assertEquals(PointerType, i8Constant.type)
        
        val i32Constant = NullPointerConstant.create(IntegerType.I32)
        assertEquals(PointerType, i32Constant.type)
        
        val i64Constant = NullPointerConstant.create(IntegerType.I64)
        assertEquals(PointerType, i64Constant.type)
    }

    @Test
    @DisplayName("NullPointerConstant.createWithPointerType should work with pointer types")
    fun testNullPointerConstantCreateWithPointerType() {
        val pointerType = PointerType
        val constant = NullPointerConstant.createWithPointerType(pointerType)
        
        assertEquals("null", constant.name, "NullPointerConstant should have name 'null'")
        assertEquals(pointerType, constant.type, "NullPointerConstant should have the specified pointer type")
        assertTrue(constant.isNullValue(), "NullPointerConstant should be null")
    }

    @Test
    @DisplayName("NullPointerConstant.createWithPointerType should reject non-pointer types")
    fun testNullPointerConstantCreateWithPointerTypeRejectsNonPointerTypes() {
        val nonPointerType = IntegerType.I32
        
        val exception = assertThrows<IllegalArgumentException> {
            NullPointerConstant.createWithPointerType(nonPointerType)
        }
        
        assertTrue(exception.message!!.contains("Type must be a pointer type"), 
                  "Exception message should indicate type must be a pointer type")
    }

    @Test
    @DisplayName("NullPointerConstant.createUntyped should create untyped null pointer")
    fun testNullPointerConstantCreateUntyped() {
        val constant = NullPointerConstant.createUntyped()
        
        assertEquals("null", constant.name, "NullPointerConstant should have name 'null'")
        assertEquals(PointerType, constant.type, "NullPointerConstant should have PointerType")
        assertTrue(constant.isNullValue(), "NullPointerConstant should be null")
    }

    @Test
    @DisplayName("NullPointerConstant should implement Constant interface correctly")
    fun testNullPointerConstantImplementsConstant() {
        val constant = NullPointerConstant.create(IntegerType.I32)
        
        // Should be assignable to Constant
        val const: space.norb.llvm.core.Constant = constant
        assertEquals("null", const.name)
        assertEquals(PointerType, const.type)
        assertTrue(const.isNullValue())
        
        // Should be assignable to Value
        val value: space.norb.llvm.core.Value = constant
        assertEquals("null", value.name)
        assertEquals(PointerType, value.type)
    }

    @Test
    @DisplayName("NullPointerConstant equality should work correctly")
    fun testNullPointerConstantEquality() {
        val constant1 = NullPointerConstant.create(IntegerType.I32)
        val constant2 = NullPointerConstant.create(IntegerType.I64)
        val constant3 = NullPointerConstant.createUntyped()
        val constant4 = NullPointerConstant.createWithPointerType(PointerType)
        
        // All null pointer constants should be equal regardless of element type
        assertEquals(constant1, constant2, "NullPointerConstants should be equal")
        assertEquals(constant1, constant3, "NullPointerConstants should be equal")
        assertEquals(constant1, constant4, "NullPointerConstants should be equal")
        
        assertEquals(constant1.hashCode(), constant2.hashCode(), "Equal constants should have same hash code")
        assertEquals(constant1.hashCode(), constant3.hashCode(), "Equal constants should have same hash code")
        assertEquals(constant1.hashCode(), constant4.hashCode(), "Equal constants should have same hash code")
    }

    @Test
    @DisplayName("NullPointerConstant toString should contain null information")
    fun testNullPointerConstantToString() {
        val constant = NullPointerConstant.create(IntegerType.I32)
        val toString = constant.toString()
        
        assertTrue(toString.contains("null"), "toString should contain 'null'")
        assertTrue(toString.contains(PointerType.toString()), "toString should contain the type")
    }

    @Test
    @DisplayName("NullPointerConstant factory methods should return equivalent instances")
    fun testNullPointerConstantFactoryMethodsEquivalence() {
        val fromCreate = NullPointerConstant.create(IntegerType.I32)
        val fromCreateWithPointerType = NullPointerConstant.createWithPointerType(PointerType)
        val fromCreateUntyped = NullPointerConstant.createUntyped()
        
        assertEquals(fromCreate, fromCreateWithPointerType, "Factory methods should return equivalent instances")
        assertEquals(fromCreate, fromCreateUntyped, "Factory methods should return equivalent instances")
        assertEquals(fromCreateWithPointerType, fromCreateUntyped, "Factory methods should return equivalent instances")
    }

    @Test
    @DisplayName("NullPointerConstant should always return true for isNullValue")
    fun testNullPointerConstantIsNullValue() {
        val constant1 = NullPointerConstant.create(IntegerType.I32)
        val constant2 = NullPointerConstant.createUntyped()
        val constant3 = NullPointerConstant.createWithPointerType(PointerType)
        
        assertTrue(constant1.isNullValue(), "NullPointerConstant should always be null")
        assertTrue(constant2.isNullValue(), "NullPointerConstant should always be null")
        assertTrue(constant3.isNullValue(), "NullPointerConstant should always be null")
    }

    @Test
    @DisplayName("NullPointerConstant should not be zero or one")
    fun testNullPointerConstantIsNotZeroOrOne() {
        val constant = NullPointerConstant.create(IntegerType.I32)
        
        assertFalse(constant.isZeroValue(), "NullPointerConstant should not be zero")
        assertFalse(constant.isOneValue(), "NullPointerConstant should not be one")
    }

    @Test
    @DisplayName("NullPointerConstant should not be all-ones")
    fun testNullPointerConstantIsNotAllOnes() {
        val constant = NullPointerConstant.create(IntegerType.I32)
        
        assertFalse(constant.isAllOnesValue(), "NullPointerConstant should not be all-ones")
    }

    @Test
    @DisplayName("NullPointerConstant should work with complex element types")
    fun testNullPointerConstantWithComplexElementTypes() {
        // Test with array type (if available)
        // Test with struct type (if available)
        // For now, just test with different integer types
        
        val i1Null = NullPointerConstant.create(IntegerType.I1)
        val i8Null = NullPointerConstant.create(IntegerType.I8)
        val i16Null = NullPointerConstant.create(IntegerType.I16)
        val i32Null = NullPointerConstant.create(IntegerType.I32)
        val i64Null = NullPointerConstant.create(IntegerType.I64)
        
        assertEquals(i1Null, i8Null, "NullPointerConstants with different element types should be equal")
        assertEquals(i8Null, i16Null, "NullPointerConstants with different element types should be equal")
        assertEquals(i16Null, i32Null, "NullPointerConstants with different element types should be equal")
        assertEquals(i32Null, i64Null, "NullPointerConstants with different element types should be equal")
    }

    @Test
    @DisplayName("NullPointerConstant should maintain type information")
    fun testNullPointerConstantTypeInformation() {
        val constant = NullPointerConstant.create(IntegerType.I32)
        
        assertTrue(constant.type.isPointerType(), "NullPointerConstant type should be a pointer type")
        assertFalse(constant.type.isIntegerType(), "NullPointerConstant type should not be an integer type")
        assertFalse(constant.type.isFloatingPointType(), "NullPointerConstant type should not be a floating-point type")
    }
}