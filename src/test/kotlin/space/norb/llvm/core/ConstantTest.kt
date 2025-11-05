package space.norb.llvm.core

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import space.norb.llvm.types.IntegerType

/**
 * Unit tests for the Constant abstract class.
 */
@DisplayName("Constant Abstract Class Tests")
class ConstantTest {

    /**
     * Mock implementation of Constant for testing purposes.
     */
    private data class MockConstant(
        override val name: String,
        override val type: Type,
        private val isNull: Boolean = false,
        private val isZero: Boolean = false,
        private val isOne: Boolean = false,
        private val isAllOnes: Boolean = false
    ) : Constant(name, type) {
        override fun isNullValue(): Boolean = isNull
        override fun isZeroValue(): Boolean = isZero
        override fun isOneValue(): Boolean = isOne
        override fun isAllOnesValue(): Boolean = isAllOnes
        
        override fun <T> accept(visitor: space.norb.llvm.visitors.IRVisitor<T>): T {
            return visitor.visitConstant(this)
        }
        
        override fun toString(): String = "MockConstant(name='$name', type=$type)"
    }

    @Test
    @DisplayName("Constant should have correct name and type")
    fun testConstantNameAndType() {
        val name = "testConstant"
        val type = IntegerType.I32
        val constant = MockConstant(name, type)
        
        assertEquals(name, constant.name, "Constant should have the correct name")
        assertEquals(type, constant.type, "Constant should have the correct type")
    }

    @Test
    @DisplayName("Constant should correctly implement Value interface")
    fun testConstantImplementsValue() {
        val name = "testConstant"
        val type = IntegerType.I32
        val constant = MockConstant(name, type)
        
        // Constant should be assignable to Value
        val value: Value = constant
        assertEquals(name, value.name, "Constant as Value should have correct name")
        assertEquals(type, value.type, "Constant as Value should have correct type")
    }

    @Test
    @DisplayName("Constant should return default values for value checks")
    fun testConstantDefaultValueChecks() {
        val name = "testConstant"
        val type = IntegerType.I32
        val constant = MockConstant(name, type)
        
        assertFalse(constant.isNullValue(), "Default isNullValue should be false")
        assertFalse(constant.isZeroValue(), "Default isZeroValue should be false")
        assertFalse(constant.isOneValue(), "Default isOneValue should be false")
        assertFalse(constant.isAllOnesValue(), "Default isAllOnesValue should be false")
    }

    @Test
    @DisplayName("Constant should return correct null value status")
    fun testConstantIsNullValue() {
        val name = "testConstant"
        val type = IntegerType.I32
        
        val nullConstant = MockConstant(name, type, isNull = true)
        assertTrue(nullConstant.isNullValue(), "isNullValue should return true when set")
        
        val nonNullConstant = MockConstant(name, type, isNull = false)
        assertFalse(nonNullConstant.isNullValue(), "isNullValue should return false when not set")
    }

    @Test
    @DisplayName("Constant should return correct zero value status")
    fun testConstantIsZeroValue() {
        val name = "testConstant"
        val type = IntegerType.I32
        
        val zeroConstant = MockConstant(name, type, isZero = true)
        assertTrue(zeroConstant.isZeroValue(), "isZeroValue should return true when set")
        
        val nonZeroConstant = MockConstant(name, type, isZero = false)
        assertFalse(nonZeroConstant.isZeroValue(), "isZeroValue should return false when not set")
    }

    @Test
    @DisplayName("Constant should return correct one value status")
    fun testConstantIsOneValue() {
        val name = "testConstant"
        val type = IntegerType.I32
        
        val oneConstant = MockConstant(name, type, isOne = true)
        assertTrue(oneConstant.isOneValue(), "isOneValue should return true when set")
        
        val nonOneConstant = MockConstant(name, type, isOne = false)
        assertFalse(nonOneConstant.isOneValue(), "isOneValue should return false when not set")
    }

    @Test
    @DisplayName("Constant should return correct all-ones value status")
    fun testConstantIsAllOnesValue() {
        val name = "testConstant"
        val type = IntegerType.I32
        
        val allOnesConstant = MockConstant(name, type, isAllOnes = true)
        assertTrue(allOnesConstant.isAllOnesValue(), "isAllOnesValue should return true when set")
        
        val nonAllOnesConstant = MockConstant(name, type, isAllOnes = false)
        assertFalse(nonAllOnesConstant.isAllOnesValue(), "isAllOnesValue should return false when not set")
    }

    @Test
    @DisplayName("Constant should handle multiple value properties correctly")
    fun testConstantMultipleValueProperties() {
        val name = "testConstant"
        val type = IntegerType.I32
        
        // Constant with multiple properties set to true
        val multiConstant = MockConstant(
            name = name,
            type = type,
            isNull = true,
            isZero = true,
            isOne = false,
            isAllOnes = true
        )
        
        assertTrue(multiConstant.isNullValue(), "isNullValue should be true")
        assertTrue(multiConstant.isZeroValue(), "isZeroValue should be true")
        assertFalse(multiConstant.isOneValue(), "isOneValue should be false")
        assertTrue(multiConstant.isAllOnesValue(), "isAllOnesValue should be true")
    }

    @Test
    @DisplayName("Constants with same properties should be equal")
    fun testConstantEquality() {
        val name = "constant"
        val type = IntegerType.I32
        
        val constant1 = MockConstant(name, type)
        val constant2 = MockConstant(name, type)
        
        // Since we're using a data class for MockConstant, equals should work
        assertEquals(constant1, constant2, "Constants with same properties should be equal")
        assertEquals(constant1.hashCode(), constant2.hashCode(), "Constants with same properties should have same hash code")
    }

    @Test
    @DisplayName("Constants with different names should not be equal")
    fun testConstantInequalityByName() {
        val type = IntegerType.I32
        
        val constant1 = MockConstant("constant1", type)
        val constant2 = MockConstant("constant2", type)
        
        assertNotEquals(constant1, constant2, "Constants with different names should not be equal")
    }

    @Test
    @DisplayName("Constants with different types should not be equal")
    fun testConstantInequalityByType() {
        val name = "constant"
        
        val constant1 = MockConstant(name, IntegerType.I32)
        val constant2 = MockConstant(name, IntegerType.I64)
        
        assertNotEquals(constant1, constant2, "Constants with different types should not be equal")
    }

    @Test
    @DisplayName("Constants with different value properties should not be equal")
    fun testConstantInequalityByValueProperties() {
        val name = "constant"
        val type = IntegerType.I32
        
        val constant1 = MockConstant(name, type, isZero = true)
        val constant2 = MockConstant(name, type, isZero = false)
        
        assertNotEquals(constant1, constant2, "Constants with different value properties should not be equal")
    }

    @Test
    @DisplayName("Constant toString should contain name and type information")
    fun testConstantToString() {
        val name = "testConstant"
        val type = IntegerType.I32
        val constant = MockConstant(name, type)
        
        val toString = constant.toString()
        assertTrue(toString.contains(name), "toString should contain the constant name")
        assertTrue(toString.contains(type.toString()), "toString should contain the type information")
    }

    @Test
    @DisplayName("Constant should handle empty name")
    fun testConstantEmptyName() {
        val name = ""
        val type = IntegerType.I32
        val constant = MockConstant(name, type)
        
        assertEquals(name, constant.name, "Constant should handle empty name")
    }

    @Test
    @DisplayName("Constant should work with different types")
    fun testConstantWithDifferentTypes() {
        val name = "testConstant"
        
        // Test with different types
        val i32Constant = MockConstant(name, IntegerType.I32)
        assertEquals(IntegerType.I32, i32Constant.type)
        
        val i64Constant = MockConstant(name, IntegerType.I64)
        assertEquals(IntegerType.I64, i64Constant.type)
    }
}