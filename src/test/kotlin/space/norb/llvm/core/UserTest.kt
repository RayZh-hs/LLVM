package space.norb.llvm.core

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertThrows
import space.norb.llvm.types.IntegerType

/**
 * Unit tests for the User abstract class.
 */
@DisplayName("User Abstract Class Tests")
class UserTest {

    /**
     * Mock implementation of User for testing purposes.
     */
    private data class MockUser(
        private val userName: String,
        private val userType: Type,
        private val userOperands: List<Value>
    ) : User(userName, userType, userOperands) {
        override fun <T> accept(visitor: space.norb.llvm.visitors.IRVisitor<T>): T {
            TODO("Not implemented for mock")
        }
        
        override fun toString(): String = "MockUser(name='$userName', type=$userType, operands=${getOperandsList()})"
    }

    /**
     * Mock implementation of Value for testing purposes.
     */
    private data class MockValue(
        override val name: String,
        override val type: Type
    ) : Value {
        override fun <T> accept(visitor: space.norb.llvm.visitors.IRVisitor<T>): T {
            TODO("Not implemented for mock")
        }
        
        override fun toString(): String = "MockValue(name='$name', type=$type)"
    }

    @Test
    @DisplayName("User should have correct name and type")
    fun testUserNameAndType() {
        val name = "testUser"
        val type = IntegerType.I32
        val operands = emptyList<Value>()
        val user = MockUser(name, type, operands)
        
        assertEquals(name, user.name, "User should have the correct name")
        assertEquals(type, user.type, "User should have the correct type")
    }

    @Test
    @DisplayName("User should have correct operands")
    fun testUserOperands() {
        val name = "testUser"
        val type = IntegerType.I32
        val operand1 = MockValue("operand1", IntegerType.I32)
        val operand2 = MockValue("operand2", IntegerType.I64)
        val operands = listOf(operand1, operand2)
        val user = MockUser(name, type, operands)
        
        assertEquals(operands, user.getOperandsList(), "User should have the correct operands")
        assertEquals(2, user.getNumOperands(), "User should have correct operand count")
    }

    @Test
    @DisplayName("User should handle empty operands")
    fun testUserEmptyOperands() {
        val name = "testUser"
        val type = IntegerType.I32
        val operands = emptyList<Value>()
        val user = MockUser(name, type, operands)
        
        assertTrue(user.getOperandsList().isEmpty(), "User should handle empty operands")
        assertEquals(0, user.getNumOperands(), "User should have zero operand count")
    }

    @Test
    @DisplayName("getOperand should return correct operand")
    fun testGetOperand() {
        val name = "testUser"
        val type = IntegerType.I32
        val operand1 = MockValue("operand1", IntegerType.I32)
        val operand2 = MockValue("operand2", IntegerType.I64)
        val operand3 = MockValue("operand3", IntegerType.I16)
        val operands = listOf(operand1, operand2, operand3)
        val user = MockUser(name, type, operands)
        
        assertEquals(operand1, user.getOperand(0), "getOperand should return first operand")
        assertEquals(operand2, user.getOperand(1), "getOperand should return second operand")
        assertEquals(operand3, user.getOperand(2), "getOperand should return third operand")
    }

    @Test
    @DisplayName("getOperand should throw IndexOutOfBoundsException for invalid index")
    fun testGetOperandInvalidIndex() {
        val name = "testUser"
        val type = IntegerType.I32
        val operand1 = MockValue("operand1", IntegerType.I32)
        val operand2 = MockValue("operand2", IntegerType.I64)
        val operands = listOf(operand1, operand2)
        val user = MockUser(name, type, operands)
        
        assertThrows<IndexOutOfBoundsException> { user.getOperand(-1) }
        assertThrows<IndexOutOfBoundsException> { user.getOperand(2) }
        assertThrows<IndexOutOfBoundsException> { user.getOperand(10) }
    }

    @Test
    @DisplayName("operandCount should return correct count")
    fun testOperandCount() {
        val name = "testUser"
        val type = IntegerType.I32
        
        // Test with 0 operands
        val user0 = MockUser(name, type, emptyList())
        assertEquals(0, user0.getNumOperands(), "getNumOperands should be 0 for empty operands")
        
        // Test with 1 operand
        val operand1 = MockValue("operand1", IntegerType.I32)
        val user1 = MockUser(name, type, listOf(operand1))
        assertEquals(1, user1.getNumOperands(), "getNumOperands should be 1 for single operand")
        
        // Test with multiple operands
        val operand2 = MockValue("operand2", IntegerType.I64)
        val operand3 = MockValue("operand3", IntegerType.I16)
        val user3 = MockUser(name, type, listOf(operand1, operand2, operand3))
        assertEquals(3, user3.getNumOperands(), "getNumOperands should be 3 for three operands")
    }

    @Test
    @DisplayName("operands list should iterate correctly")
    fun testOperandsIteration() {
        val name = "testUser"
        val type = IntegerType.I32
        val operand1 = MockValue("operand1", IntegerType.I32)
        val operand2 = MockValue("operand2", IntegerType.I64)
        val operand3 = MockValue("operand3", IntegerType.I16)
        val operands = listOf(operand1, operand2, operand3)
        val user = MockUser(name, type, operands)
        
        val userOperands = user.getOperandsList()
        assertEquals(3, userOperands.size, "Should have 3 operands")
        assertEquals(operand1, userOperands[0], "First operand should be operand1")
        assertEquals(operand2, userOperands[1], "Second operand should be operand2")
        assertEquals(operand3, userOperands[2], "Third operand should be operand3")
    }

    @Test
    @DisplayName("operands list should work with empty operands")
    fun testOperandsEmpty() {
        val name = "testUser"
        val type = IntegerType.I32
        val user = MockUser(name, type, emptyList())
        
        val userOperands = user.getOperandsList()
        assertTrue(userOperands.isEmpty(), "Operands list should be empty")
        assertEquals(0, userOperands.size, "Operands list size should be 0")
    }

    @Test
    @DisplayName("User should correctly implement Value interface")
    fun testUserImplementsValue() {
        val name = "testUser"
        val type = IntegerType.I32
        val operand1 = MockValue("operand1", IntegerType.I32)
        val operands = listOf(operand1)
        val user = MockUser(name, type, operands)
        
        // User should be assignable to Value
        val value: Value = user
        assertEquals(name, value.name, "User as Value should have correct name")
        assertEquals(type, value.type, "User as Value should have correct type")
    }

    @Test
    @DisplayName("Users with same properties should be equal")
    fun testUserEquality() {
        val name = "user"
        val type = IntegerType.I32
        val operand1 = MockValue("operand1", IntegerType.I32)
        val operand2 = MockValue("operand2", IntegerType.I64)
        val operands = listOf(operand1, operand2)
        
        val user1 = MockUser(name, type, operands)
        val user2 = MockUser(name, type, operands)
        
        // Since we're using a data class for MockUser, equals should work
        assertEquals(user1, user2, "Users with same properties should be equal")
        assertEquals(user1.hashCode(), user2.hashCode(), "Users with same properties should have same hash code")
    }

    @Test
    @DisplayName("Users with different names should not be equal")
    fun testUserInequalityByName() {
        val type = IntegerType.I32
        val operand1 = MockValue("operand1", IntegerType.I32)
        val operands = listOf(operand1)
        
        val user1 = MockUser("user1", type, operands)
        val user2 = MockUser("user2", type, operands)
        
        assertNotEquals(user1, user2, "Users with different names should not be equal")
    }

    @Test
    @DisplayName("Users with different types should not be equal")
    fun testUserInequalityByType() {
        val name = "user"
        val operand1 = MockValue("operand1", IntegerType.I32)
        val operands = listOf(operand1)
        
        val user1 = MockUser(name, IntegerType.I32, operands)
        val user2 = MockUser(name, IntegerType.I64, operands)
        
        assertNotEquals(user1, user2, "Users with different types should not be equal")
    }

    @Test
    @DisplayName("Users with different operands should not be equal")
    fun testUserInequalityByOperands() {
        val name = "user"
        val type = IntegerType.I32
        val operand1 = MockValue("operand1", IntegerType.I32)
        val operand2 = MockValue("operand2", IntegerType.I64)
        
        val user1 = MockUser(name, type, listOf(operand1))
        val user2 = MockUser(name, type, listOf(operand2))
        
        assertNotEquals(user1, user2, "Users with different operands should not be equal")
    }

    @Test
    @DisplayName("User toString should contain name and type information")
    fun testUserToString() {
        val name = "testUser"
        val type = IntegerType.I32
        val operand1 = MockValue("operand1", IntegerType.I32)
        val operands = listOf(operand1)
        val user = MockUser(name, type, operands)
        
        val toString = user.toString()
        assertTrue(toString.contains(name), "toString should contain the user name")
        assertTrue(toString.contains(type.toString()), "toString should contain the type information")
    }
}