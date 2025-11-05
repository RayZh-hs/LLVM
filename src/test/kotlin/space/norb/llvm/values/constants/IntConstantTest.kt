package space.norb.llvm.values.constants

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import space.norb.llvm.types.IntegerType

/**
 * Unit tests for IntConstant.
 */
@DisplayName("IntConstant Tests")
class IntConstantTest {

    @Test
    @DisplayName("IntConstant should be created with correct value and type")
    fun testIntConstantCreation() {
        val value = 42L
        val type = IntegerType.I32
        val constant = IntConstant(value, type)
        
        assertEquals(value, constant.value, "IntConstant should have the correct value")
        assertEquals(type, constant.type, "IntConstant should have the correct type")
        assertEquals(value.toString(), constant.name, "IntConstant name should be the string representation of the value")
    }

    @Test
    @DisplayName("IntConstant should work with different integer types")
    fun testIntConstantWithDifferentTypes() {
        val value = 123L
        
        val i8Constant = IntConstant(value, IntegerType.I8)
        assertEquals(IntegerType.I8, i8Constant.type)
        
        val i16Constant = IntConstant(value, IntegerType.I16)
        assertEquals(IntegerType.I16, i16Constant.type)
        
        val i32Constant = IntConstant(value, IntegerType.I32)
        assertEquals(IntegerType.I32, i32Constant.type)
        
        val i64Constant = IntConstant(value, IntegerType.I64)
        assertEquals(IntegerType.I64, i64Constant.type)
    }

    @Test
    @DisplayName("IntConstant should handle zero value")
    fun testIntConstantZeroValue() {
        val constant = IntConstant(0L, IntegerType.I32)
        
        assertEquals(0L, constant.value)
        assertEquals("0", constant.name)
        assertFalse(constant.isNullValue(), "Zero constant should not be null")
        assertTrue(constant.isZeroValue(), "Zero constant should be zero")
        assertFalse(constant.isOneValue(), "Zero constant should not be one")
    }

    @Test
    @DisplayName("IntConstant should handle one value")
    fun testIntConstantOneValue() {
        val constant = IntConstant(1L, IntegerType.I32)
        
        assertEquals(1L, constant.value)
        assertEquals("1", constant.name)
        assertFalse(constant.isNullValue(), "One constant should not be null")
        assertFalse(constant.isZeroValue(), "One constant should not be zero")
        assertTrue(constant.isOneValue(), "One constant should be one")
    }

    @Test
    @DisplayName("IntConstant should handle negative values")
    fun testIntConstantNegativeValues() {
        val value = -42L
        val constant = IntConstant(value, IntegerType.I32)
        
        assertEquals(value, constant.value)
        assertEquals(value.toString(), constant.name)
        assertFalse(constant.isNullValue(), "Negative constant should not be null")
        assertFalse(constant.isZeroValue(), "Negative constant should not be zero")
        assertFalse(constant.isOneValue(), "Negative constant should not be one")
    }

    @Test
    @DisplayName("IntConstant should handle maximum values")
    fun testIntConstantMaximumValues() {
        val maxInt8 = 127L
        val maxInt16 = 32767L
        val maxInt32 = 2147483647L
        val maxInt64 = Long.MAX_VALUE
        
        val i8Constant = IntConstant(maxInt8, IntegerType.I8)
        assertEquals(maxInt8, i8Constant.value)
        
        val i16Constant = IntConstant(maxInt16, IntegerType.I16)
        assertEquals(maxInt16, i16Constant.value)
        
        val i32Constant = IntConstant(maxInt32, IntegerType.I32)
        assertEquals(maxInt32, i32Constant.value)
        
        val i64Constant = IntConstant(maxInt64, IntegerType.I64)
        assertEquals(maxInt64, i64Constant.value)
    }

    @Test
    @DisplayName("IntConstant should handle minimum values")
    fun testIntConstantMinimumValues() {
        val minInt8 = -128L
        val minInt16 = -32768L
        val minInt32 = -2147483648L
        val minInt64 = Long.MIN_VALUE
        
        val i8Constant = IntConstant(minInt8, IntegerType.I8)
        assertEquals(minInt8, i8Constant.value)
        
        val i16Constant = IntConstant(minInt16, IntegerType.I16)
        assertEquals(minInt16, i16Constant.value)
        
        val i32Constant = IntConstant(minInt32, IntegerType.I32)
        assertEquals(minInt32, i32Constant.value)
        
        val i64Constant = IntConstant(minInt64, IntegerType.I64)
        assertEquals(minInt64, i64Constant.value)
    }

    @Test
    @DisplayName("IntConstant should implement Constant interface correctly")
    fun testIntConstantImplementsConstant() {
        val constant = IntConstant(42L, IntegerType.I32)
        
        // Should be assignable to Constant
        val const: space.norb.llvm.core.Constant = constant
        assertEquals(42L, (const as IntConstant).value)
        assertEquals(IntegerType.I32, const.type)
        
        // Should be assignable to Value
        val value: space.norb.llvm.core.Value = constant
        assertEquals("42", value.name)
        assertEquals(IntegerType.I32, value.type)
    }

    @Test
    @DisplayName("IntConstant equality should work correctly")
    fun testIntConstantEquality() {
        val constant1 = IntConstant(42L, IntegerType.I32)
        val constant2 = IntConstant(42L, IntegerType.I32)
        val constant3 = IntConstant(43L, IntegerType.I32)
        val constant4 = IntConstant(42L, IntegerType.I64)
        
        assertEquals(constant1, constant2, "Constants with same value and type should be equal")
        assertEquals(constant1.hashCode(), constant2.hashCode(), "Equal constants should have same hash code")
        
        assertNotEquals(constant1, constant3, "Constants with different values should not be equal")
        assertNotEquals(constant1, constant4, "Constants with different types should not be equal")
    }

    @Test
    @DisplayName("IntConstant toString should contain value and type information")
    fun testIntConstantToString() {
        val constant = IntConstant(42L, IntegerType.I32)
        val toString = constant.toString()
        
        assertTrue(toString.contains("42"), "toString should contain the value")
        assertTrue(toString.contains(IntegerType.I32.toString()), "toString should contain the type")
    }

    @Test
    @DisplayName("IntConstant should handle all-ones value correctly")
    fun testIntConstantAllOnesValue() {
        // Test with 8-bit all ones (0xFF = 255)
        val allOnes8 = IntConstant(255L, IntegerType.I8)
        assertTrue(allOnes8.isAllOnesValue(), "255 should be all ones for 8-bit integer")
        
        // Test with 16-bit all ones (0xFFFF = 65535)
        val allOnes16 = IntConstant(65535L, IntegerType.I16)
        assertTrue(allOnes16.isAllOnesValue(), "65535 should be all ones for 16-bit integer")
        
        // Test with 32-bit all ones (0xFFFFFFFF = 4294967295)
        val allOnes32 = IntConstant(4294967295L, IntegerType.I32)
        assertTrue(allOnes32.isAllOnesValue(), "4294967295 should be all ones for 32-bit integer")
        
        // Test with non-all-ones value
        val notAllOnes = IntConstant(42L, IntegerType.I32)
        assertFalse(notAllOnes.isAllOnesValue(), "42 should not be all ones")
    }
}