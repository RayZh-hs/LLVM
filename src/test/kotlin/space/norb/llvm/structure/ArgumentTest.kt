package space.norb.llvm.structure

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.BeforeEach
import space.norb.llvm.types.IntegerType
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.VoidType

/**
 * Unit tests for Argument.
 */
@DisplayName("Argument Tests")
class ArgumentTest {
    
    private lateinit var module: Module
    private lateinit var function: Function
    
    @BeforeEach
    fun setUp() {
        module = Module("testModule")
        val functionType = FunctionType(IntegerType.I32, listOf(IntegerType.I32, IntegerType.I64))
        function = Function("testFunction", functionType, module)
    }
    
    @Test
    @DisplayName("Argument should be created with correct properties")
    fun testArgumentCreation() {
        val name = "testArg"
        val type = IntegerType.I32
        val index = 0
        val argument = Argument(name, type, function, index)
        
        assertEquals(name, argument.name, "Argument should have the correct name")
        assertEquals(type, argument.type, "Argument should have the correct type")
        assertEquals(function, argument.function, "Argument should reference the function")
        assertEquals(index, argument.index, "Argument should have the correct index")
    }
    
    @Test
    @DisplayName("Argument should work with different types")
    fun testArgumentWithDifferentTypes() {
        val i1Arg = Argument("i1Arg", IntegerType.I1, function, 0)
        assertEquals(IntegerType.I1, i1Arg.type)
        
        val i8Arg = Argument("i8Arg", IntegerType.I8, function, 1)
        assertEquals(IntegerType.I8, i8Arg.type)
        
        val i16Arg = Argument("i16Arg", IntegerType.I16, function, 2)
        assertEquals(IntegerType.I16, i16Arg.type)
        
        val i32Arg = Argument("i32Arg", IntegerType.I32, function, 3)
        assertEquals(IntegerType.I32, i32Arg.type)
        
        val i64Arg = Argument("i64Arg", IntegerType.I64, function, 4)
        assertEquals(IntegerType.I64, i64Arg.type)
    }
    
    @Test
    @DisplayName("Argument should work with different indices")
    fun testArgumentWithDifferentIndices() {
        val arg0 = Argument("arg0", IntegerType.I32, function, 0)
        assertEquals(0, arg0.index)
        
        val arg1 = Argument("arg1", IntegerType.I32, function, 1)
        assertEquals(1, arg1.index)
        
        val arg5 = Argument("arg5", IntegerType.I32, function, 5)
        assertEquals(5, arg5.index)
        
        val arg10 = Argument("arg10", IntegerType.I32, function, 10)
        assertEquals(10, arg10.index)
    }
    
    @Test
    @DisplayName("Argument should work with different functions")
    fun testArgumentWithDifferentFunctions() {
        val functionType1 = FunctionType(IntegerType.I32, listOf(IntegerType.I32))
        val function1 = Function("function1", functionType1, module)
        
        val functionType2 = FunctionType(IntegerType.I64, listOf(IntegerType.I64))
        val function2 = Function("function2", functionType2, module)
        
        val arg1 = Argument("arg1", IntegerType.I32, function1, 0)
        val arg2 = Argument("arg2", IntegerType.I64, function2, 0)
        
        assertEquals(function1, arg1.function, "Argument should reference the correct function")
        assertEquals(function2, arg2.function, "Argument should reference the correct function")
    }
    
    @Test
    @DisplayName("Argument should implement Value interface correctly")
    fun testArgumentImplementsValue() {
        val name = "testArg"
        val type = IntegerType.I32
        val index = 0
        val argument = Argument(name, type, function, index)
        
        // Should be assignable to Value
        val value: space.norb.llvm.core.Value = argument
        assertEquals(name, value.name, "Argument as Value should have correct name")
        assertEquals(type, value.type, "Argument as Value should have correct type")
    }
    
    @Test
    @DisplayName("Argument equality should work correctly")
    fun testArgumentEquality() {
        val argument1 = Argument("sameName", IntegerType.I32, function, 0)
        val argument2 = Argument("sameName", IntegerType.I32, function, 0)
        val argument3 = Argument("differentName", IntegerType.I32, function, 0)
        
        assertEquals(argument1, argument2, "Arguments with same properties should be equal")
        
        assertNotEquals(argument1, argument3, "Arguments with different names should not be equal")
    }
    
    @Test
    @DisplayName("Argument equality should consider type")
    fun testArgumentEqualityByType() {
        val name = "testArg"
        val argument1 = Argument(name, IntegerType.I32, function, 0)
        val argument2 = Argument(name, IntegerType.I64, function, 0)
        
        assertNotEquals(argument1, argument2, "Arguments with different types should not be equal")
    }
    
    @Test
    @DisplayName("Argument equality should consider function")
    fun testArgumentEqualityByFunction() {
        val name = "testArg"
        val functionType = FunctionType(IntegerType.I32, listOf(IntegerType.I32))
        val function1 = Function("function1", functionType, module)
        val function2 = Function("function2", functionType, module)
        
        val argument1 = Argument(name, IntegerType.I32, function1, 0)
        val argument2 = Argument(name, IntegerType.I32, function2, 0)
        
        assertNotEquals(argument1, argument2, "Arguments in different functions should not be equal")
    }
    
    @Test
    @DisplayName("Argument equality should consider index")
    fun testArgumentEqualityByIndex() {
        val name = "testArg"
        val argument1 = Argument(name, IntegerType.I32, function, 0)
        val argument2 = Argument(name, IntegerType.I32, function, 1)
        
        assertNotEquals(argument1, argument2, "Arguments with different indices should not be equal")
    }
    
    @Test
    @DisplayName("Argument toString should contain name and type information")
    fun testArgumentToString() {
        val name = "testArg"
        val type = IntegerType.I32
        val argument = Argument(name, type, function, 0)
        
        val toString = argument.toString()
        assertTrue(toString.contains(name), "toString should contain the argument name")
        assertTrue(toString.contains(type.toString()), "toString should contain the type")
    }
    
    @Test
    @DisplayName("Argument should handle empty name")
    fun testArgumentEmptyName() {
        val name = ""
        val argument = Argument(name, IntegerType.I32, function, 0)
        
        assertEquals(name, argument.name, "Argument should handle empty name")
    }
    
    @Test
    @DisplayName("Argument should handle special characters in name")
    fun testArgumentSpecialCharactersInName() {
        val name = "test-arg_123"
        val argument = Argument(name, IntegerType.I32, function, 0)
        
        assertEquals(name, argument.name, "Argument should handle special characters in name")
    }
    
    @Test
    @DisplayName("Argument should handle negative index")
    fun testArgumentNegativeIndex() {
        val name = "testArg"
        val index = -1
        val argument = Argument(name, IntegerType.I32, function, index)
        
        assertEquals(index, argument.index, "Argument should handle negative index")
    }
    
    @Test
    @DisplayName("Argument should handle large index")
    fun testArgumentLargeIndex() {
        val name = "testArg"
        val index = Int.MAX_VALUE
        val argument = Argument(name, IntegerType.I32, function, index)
        
        assertEquals(index, argument.index, "Argument should handle large index")
    }
    
    @Test
    @DisplayName("Argument should maintain type information")
    fun testArgumentTypeInformation() {
        val argument = Argument("testArg", IntegerType.I32, function, 0)
        
        assertTrue(argument.type.isIntegerType(), "Argument type should be an integer type")
        assertFalse(argument.type.isFloatingPointType(), "Argument type should not be a floating-point type")
        assertFalse(argument.type.isPointerType(), "Argument type should not be a pointer type")
        assertFalse(argument.type.isFunctionType(), "Argument type should not be a function type")
    }
    
    @Test
    @DisplayName("Argument should work with function parameters")
    fun testArgumentWithFunctionParameters() {
        val functionType = FunctionType(IntegerType.I32, listOf(
            IntegerType.I1,   // Parameter 0
            IntegerType.I8,   // Parameter 1
            IntegerType.I16,  // Parameter 2
            IntegerType.I32,  // Parameter 3
            IntegerType.I64   // Parameter 4
        ))
        val function = Function("testFunction", functionType, module)
        
        // Create arguments matching the function parameters
        val arg0 = Argument("arg0", IntegerType.I1, function, 0)
        val arg1 = Argument("arg1", IntegerType.I8, function, 1)
        val arg2 = Argument("arg2", IntegerType.I16, function, 2)
        val arg3 = Argument("arg3", IntegerType.I32, function, 3)
        val arg4 = Argument("arg4", IntegerType.I64, function, 4)
        
        assertEquals(IntegerType.I1, arg0.type, "Argument 0 should match parameter type")
        assertEquals(IntegerType.I8, arg1.type, "Argument 1 should match parameter type")
        assertEquals(IntegerType.I16, arg2.type, "Argument 2 should match parameter type")
        assertEquals(IntegerType.I32, arg3.type, "Argument 3 should match parameter type")
        assertEquals(IntegerType.I64, arg4.type, "Argument 4 should match parameter type")
        
        assertEquals(0, arg0.index, "Argument 0 should have correct index")
        assertEquals(1, arg1.index, "Argument 1 should have correct index")
        assertEquals(2, arg2.index, "Argument 2 should have correct index")
        assertEquals(3, arg3.index, "Argument 3 should have correct index")
        assertEquals(4, arg4.index, "Argument 4 should have correct index")
    }
    
    @Test
    @DisplayName("Argument should work with void return type function")
    fun testArgumentWithVoidReturnTypeFunction() {
        val functionType = FunctionType(VoidType, listOf(IntegerType.I32))
        val voidFunction = Function("voidFunction", functionType, module)
        
        val argument = Argument("arg", IntegerType.I32, voidFunction, 0)
        
        assertEquals(voidFunction, argument.function, "Argument should reference the void function")
        assertEquals(IntegerType.I32, argument.type, "Argument should have correct type")
        assertEquals(0, argument.index, "Argument should have correct index")
    }
    
    @Test
    @DisplayName("Argument should work with no parameter function")
    fun testArgumentWithNoParameterFunction() {
        val functionType = FunctionType(IntegerType.I32, emptyList())
        val noParamFunction = Function("noParamFunction", functionType, module)
        
        // Even though the function has no parameters, we can still create arguments
        // This might be useful for testing or error cases
        val argument = Argument("arg", IntegerType.I32, noParamFunction, 0)
        
        assertEquals(noParamFunction, argument.function, "Argument should reference the no-parameter function")
        assertEquals(IntegerType.I32, argument.type, "Argument should have correct type")
        assertEquals(0, argument.index, "Argument should have correct index")
    }
}