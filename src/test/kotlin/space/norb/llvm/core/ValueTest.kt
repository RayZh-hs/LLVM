package space.norb.llvm.core

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import space.norb.llvm.types.IntegerType
import space.norb.llvm.types.VoidType

/**
 * Unit tests for the Value interface.
 */
@DisplayName("Value Interface Tests")
class ValueTest {

    /**
     * Mock implementation of Value for testing purposes.
     */
    private class MockValue(
        override val name: String,
        override val type: Type
    ) : Value {
        override fun <T> accept(visitor: space.norb.llvm.visitors.IRVisitor<T>): T {
            TODO("Not implemented for mock")
        }
    }

    @Test
    @DisplayName("Value should have correct name")
    fun testValueName() {
        val testName = "testValue"
        val testType = IntegerType.I32
        val value = MockValue(testName, testType)
        
        assertEquals(testName, value.name, "Value should have the correct name")
    }

    @Test
    @DisplayName("Value should have correct type")
    fun testValueType() {
        val testName = "testValue"
        val testType = IntegerType.I64
        val value = MockValue(testName, testType)
        
        assertEquals(testType, value.type, "Value should have the correct type")
    }

    @Test
    @DisplayName("Value should handle empty name")
    fun testValueEmptyName() {
        val testName = ""
        val testType = VoidType
        val value = MockValue(testName, testType)
        
        assertEquals(testName, value.name, "Value should handle empty name")
    }

    @Test
    @DisplayName("Values with same properties should be equal")
    fun testValueEquality() {
        val name = "value"
        val type = IntegerType.I32
        val value1 = MockValue(name, type)
        val value2 = MockValue(name, type)
        
        // Since we're using a data class for MockValue, equals should work
        assertEquals(value1, value2, "Values with same properties should be equal")
        assertEquals(value1.hashCode(), value2.hashCode(), "Values with same properties should have same hash code")
    }

    @Test
    @DisplayName("Values with different names should not be equal")
    fun testValueInequalityByName() {
        val type = IntegerType.I32
        val value1 = MockValue("value1", type)
        val value2 = MockValue("value2", type)
        
        assertNotEquals(value1, value2, "Values with different names should not be equal")
    }

    @Test
    @DisplayName("Values with different types should not be equal")
    fun testValueInequalityByType() {
        val name = "value"
        val value1 = MockValue(name, IntegerType.I32)
        val value2 = MockValue(name, IntegerType.I64)
        
        assertNotEquals(value1, value2, "Values with different types should not be equal")
    }

    @Test
    @DisplayName("Value toString should contain name and type information")
    fun testValueToString() {
        val name = "testValue"
        val type = IntegerType.I32
        val value = MockValue(name, type)
        
        val toString = value.toString()
        assertTrue(toString.contains(name), "toString should contain the value name")
        assertTrue(toString.contains(type.toString()), "toString should contain the type information")
    }
}