package space.norb.llvm.core

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import space.norb.llvm.types.IntegerType
import space.norb.llvm.types.VoidType
import space.norb.llvm.structure.Module
import space.norb.llvm.structure.Function
import space.norb.llvm.structure.BasicBlock
import space.norb.llvm.structure.Argument
import space.norb.llvm.values.globals.GlobalVariable
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.PointerType
import space.norb.llvm.instructions.memory.AllocaInst

/**
 * Unit tests for the Value interface.
 */
@DisplayName("Value Interface Tests")
class ValueTest {

    /**
     * Mock implementation of Value for testing purposes.
     */
    private data class MockValue(
        override val name: String,
        override val type: Type
    ) : Value {
        override fun <T> accept(visitor: space.norb.llvm.visitors.IRVisitor<T>): T {
            throw UnsupportedOperationException("accept() not implemented for MockValue - this is a test mock")
        }
        
        override fun toString(): String = "MockValue(name='$name', type=$type)"
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

    @Test
    @DisplayName("Function getParent should return module")
    fun testFunctionGetParent() {
        val module = Module("testModule")
        val functionType = FunctionType(IntegerType.I32, listOf(IntegerType.I32))
        val function = Function("testFunction", functionType, module)
        
        assertEquals(module, function.getParent(), "Function getParent should return the containing module")
    }

    @Test
    @DisplayName("BasicBlock getParent should return function")
    fun testBasicBlockGetParent() {
        val module = Module("testModule")
        val functionType = FunctionType(IntegerType.I32, listOf(IntegerType.I32))
        val function = Function("testFunction", functionType, module)
        val basicBlock = BasicBlock("entry", function)
        
        assertEquals(function, basicBlock.getParent(), "BasicBlock getParent should return the containing function")
    }

    @Test
    @DisplayName("Argument getParent should return function")
    fun testArgumentGetParent() {
        val module = Module("testModule")
        val functionType = FunctionType(IntegerType.I32, listOf(IntegerType.I32))
        val function = Function("testFunction", functionType, module)
        val argument = function.parameters.first()
        
        assertEquals(function, argument.getParent(), "Argument getParent should return the containing function")
    }

    @Test
    @DisplayName("GlobalVariable getParent should return module")
    fun testGlobalVariableGetParent() {
        val module = Module("testModule")
        val globalVariable = GlobalVariable.create("testGlobal", module)
        
        assertEquals(module, globalVariable.getParent(), "GlobalVariable getParent should return the containing module")
    }

    @Test
    @DisplayName("Instruction getParent should return basic block")
    fun testInstructionGetParent() {
        val module = Module("testModule")
        val functionType = FunctionType(IntegerType.I32, listOf())
        val function = Function("testFunction", functionType, module)
        val basicBlock = BasicBlock("entry", function)
        val instruction = AllocaInst("alloca", IntegerType.I32)
        instruction.parent = basicBlock
        
        assertEquals(basicBlock, instruction.getParent(), "Instruction getParent should return the containing basic block")
    }

    @Test
    @DisplayName("MockValue getParent should return null")
    fun testMockValueGetParent() {
        val mockValue = MockValue("test", IntegerType.I32)
        
        assertNull(mockValue.getParent(), "MockValue getParent should return null by default")
    }
}