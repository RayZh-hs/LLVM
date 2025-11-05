package space.norb.llvm.values.constants

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import space.norb.llvm.types.FloatingPointType

/**
 * Unit tests for FloatConstant.
 */
@DisplayName("FloatConstant Tests")
class FloatConstantTest {

    @Test
    @DisplayName("FloatConstant should be created with correct value and type")
    fun testFloatConstantCreation() {
        val value = 3.14
        val type = FloatingPointType.FloatType
        val constant = FloatConstant(value, type)
        
        assertEquals(value, constant.value, "FloatConstant should have the correct value")
        assertEquals(type, constant.type, "FloatConstant should have the correct type")
        assertEquals(value.toString(), constant.name, "FloatConstant name should be the string representation of the value")
    }

    @Test
    @DisplayName("FloatConstant should work with different floating-point types")
    fun testFloatConstantWithDifferentTypes() {
        val value = 2.71828
        
        val floatConstant = FloatConstant(value, FloatingPointType.FloatType)
        assertEquals(FloatingPointType.FloatType, floatConstant.type)
        
        val doubleConstant = FloatConstant(value, FloatingPointType.DoubleType)
        assertEquals(FloatingPointType.DoubleType, doubleConstant.type)
    }

    @Test
    @DisplayName("FloatConstant should handle zero value")
    fun testFloatConstantZeroValue() {
        val constant = FloatConstant(0.0, FloatingPointType.FloatType)
        
        assertEquals(0.0, constant.value, 0.0, "Zero constant should have zero value")
        assertEquals("0.0", constant.name)
        assertFalse(constant.isNullValue(), "Zero constant should not be null")
        assertTrue(constant.isZeroValue(), "Zero constant should be zero")
        assertFalse(constant.isOneValue(), "Zero constant should not be one")
    }

    @Test
    @DisplayName("FloatConstant should handle one value")
    fun testFloatConstantOneValue() {
        val constant = FloatConstant(1.0, FloatingPointType.FloatType)
        
        assertEquals(1.0, constant.value, 0.0, "One constant should have value 1.0")
        assertEquals("1.0", constant.name)
        assertFalse(constant.isNullValue(), "One constant should not be null")
        assertFalse(constant.isZeroValue(), "One constant should not be zero")
        assertTrue(constant.isOneValue(), "One constant should be one")
    }

    @Test
    @DisplayName("FloatConstant should handle negative values")
    fun testFloatConstantNegativeValues() {
        val value = -3.14
        val constant = FloatConstant(value, FloatingPointType.FloatType)
        
        assertEquals(value, constant.value, 0.0, "Negative constant should have correct value")
        assertEquals(value.toString(), constant.name)
        assertFalse(constant.isNullValue(), "Negative constant should not be null")
        assertFalse(constant.isZeroValue(), "Negative constant should not be zero")
        assertFalse(constant.isOneValue(), "Negative constant should not be one")
    }

    @Test
    @DisplayName("FloatConstant should handle very small values")
    fun testFloatConstantVerySmallValues() {
        val smallValue = 1e-10
        val constant = FloatConstant(smallValue, FloatingPointType.DoubleType)
        
        assertEquals(smallValue, constant.value, 0.0, "Very small constant should have correct value")
        assertFalse(constant.isNullValue(), "Very small constant should not be null")
        assertFalse(constant.isZeroValue(), "Very small constant should not be zero")
        assertFalse(constant.isOneValue(), "Very small constant should not be one")
    }

    @Test
    @DisplayName("FloatConstant should handle very large values")
    fun testFloatConstantVeryLargeValues() {
        val largeValue = 1e10
        val constant = FloatConstant(largeValue, FloatingPointType.DoubleType)
        
        assertEquals(largeValue, constant.value, 0.0, "Very large constant should have correct value")
        assertFalse(constant.isNullValue(), "Very large constant should not be null")
        assertFalse(constant.isZeroValue(), "Very large constant should not be zero")
        assertFalse(constant.isOneValue(), "Very large constant should not be one")
    }

    @Test
    @DisplayName("FloatConstant should handle special floating-point values")
    fun testFloatConstantSpecialValues() {
        // Test positive infinity
        val posInf = FloatConstant(Double.POSITIVE_INFINITY, FloatingPointType.DoubleType)
        assertEquals(Double.POSITIVE_INFINITY, posInf.value, 0.0)
        assertFalse(posInf.isNullValue())
        assertFalse(posInf.isZeroValue())
        assertFalse(posInf.isOneValue())
        
        // Test negative infinity
        val negInf = FloatConstant(Double.NEGATIVE_INFINITY, FloatingPointType.DoubleType)
        assertEquals(Double.NEGATIVE_INFINITY, negInf.value, 0.0)
        assertFalse(negInf.isNullValue())
        assertFalse(negInf.isZeroValue())
        assertFalse(negInf.isOneValue())
        
        // Test NaN (Not a Number)
        val nan = FloatConstant(Double.NaN, FloatingPointType.DoubleType)
        assertTrue(nan.value.isNaN(), "NaN constant should have NaN value")
        assertFalse(nan.isNullValue())
        assertFalse(nan.isZeroValue())
        assertFalse(nan.isOneValue())
    }

    @Test
    @DisplayName("FloatConstant should implement Constant interface correctly")
    fun testFloatConstantImplementsConstant() {
        val constant = FloatConstant(3.14, FloatingPointType.FloatType)
        
        // Should be assignable to Constant
        val const: space.norb.llvm.core.Constant = constant
        assertEquals(3.14, (const as FloatConstant).value, 0.0)
        assertEquals(FloatingPointType.FloatType, const.type)
        
        // Should be assignable to Value
        val value: space.norb.llvm.core.Value = constant
        assertEquals("3.14", value.name)
        assertEquals(FloatingPointType.FloatType, value.type)
    }

    @Test
    @DisplayName("FloatConstant equality should work correctly")
    fun testFloatConstantEquality() {
        val constant1 = FloatConstant(3.14, FloatingPointType.FloatType)
        val constant2 = FloatConstant(3.14, FloatingPointType.FloatType)
        val constant3 = FloatConstant(2.71, FloatingPointType.FloatType)
        val constant4 = FloatConstant(3.14, FloatingPointType.DoubleType)
        
        assertEquals(constant1, constant2, "Constants with same value and type should be equal")
        assertEquals(constant1.hashCode(), constant2.hashCode(), "Equal constants should have same hash code")
        
        assertNotEquals(constant1, constant3, "Constants with different values should not be equal")
        assertNotEquals(constant1, constant4, "Constants with different types should not be equal")
    }

    @Test
    @DisplayName("FloatConstant equality should handle floating-point precision")
    fun testFloatConstantEqualityPrecision() {
        val constant1 = FloatConstant(1.0 / 3.0, FloatingPointType.DoubleType)
        val constant2 = FloatConstant(1.0 / 3.0, FloatingPointType.DoubleType)
        
        assertEquals(constant1, constant2, "Constants with same computed value should be equal")
        
        // Test with very close but not exactly equal values
        val constant3 = FloatConstant(0.3333333333333333, FloatingPointType.DoubleType)
        val constant4 = FloatConstant(0.3333333333333334, FloatingPointType.DoubleType)
        
        assertNotEquals(constant3, constant4, "Constants with slightly different values should not be equal")
    }

    @Test
    @DisplayName("FloatConstant toString should contain value and type information")
    fun testFloatConstantToString() {
        val constant = FloatConstant(3.14159, FloatingPointType.DoubleType)
        val toString = constant.toString()
        
        assertTrue(toString.contains("3.14159"), "toString should contain the value")
        assertTrue(toString.contains(FloatingPointType.DoubleType.toString()), "toString should contain the type")
    }

    @Test
    @DisplayName("FloatConstant should handle mathematical constants")
    fun testFloatConstantMathematicalConstants() {
        val pi = FloatConstant(Math.PI, FloatingPointType.DoubleType)
        assertEquals(Math.PI, pi.value, 0.0)
        assertFalse(pi.isNullValue())
        assertFalse(pi.isZeroValue())
        assertFalse(pi.isOneValue())
        
        val e = FloatConstant(Math.E, FloatingPointType.DoubleType)
        assertEquals(Math.E, e.value, 0.0)
        assertFalse(e.isNullValue())
        assertFalse(e.isZeroValue())
        assertFalse(e.isOneValue())
    }

    @Test
    @DisplayName("FloatConstant should handle all-ones value correctly")
    fun testFloatConstantAllOnesValue() {
        // For floating-point types, all-ones is typically represented as NaN
        val nanFloat = FloatConstant(Float.NaN.toDouble(), FloatingPointType.FloatType)
        assertTrue(nanFloat.isAllOnesValue(), "NaN should be considered all-ones for floating-point types")
        
        val nanDouble = FloatConstant(Double.NaN, FloatingPointType.DoubleType)
        assertTrue(nanDouble.isAllOnesValue(), "NaN should be considered all-ones for floating-point types")
        
        // Test with non-all-ones value
        val notAllOnes = FloatConstant(3.14, FloatingPointType.FloatType)
        assertFalse(notAllOnes.isAllOnesValue(), "Regular floating-point value should not be all-ones")
    }
}