package space.norb.llvm.structure

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.BeforeEach
import space.norb.llvm.types.IntegerType
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.VoidType
import space.norb.llvm.enums.LinkageType

/**
 * Unit tests for Function.
 */
@DisplayName("Function Tests")
class FunctionTest {
    
    private lateinit var module: Module
    
    @BeforeEach
    fun setUp() {
        module = Module("testModule")
    }
    
    @Test
    @DisplayName("Function should be created with correct properties")
    fun testFunctionCreation() {
        val name = "testFunction"
        val functionType = FunctionType(IntegerType.I32, listOf(IntegerType.I32, IntegerType.I64))
        val function = Function(name, functionType, module)
        
        assertEquals(name, function.name, "Function should have the correct name")
        assertEquals(functionType, function.type, "Function should have the correct type")
        assertEquals(module, function.module, "Function should reference the module")
        assertEquals(IntegerType.I32, function.returnType, "Function should have the correct return type")
        assertEquals(2, function.parameters.size, "Function should have the correct number of parameters")
        assertNull(function.entryBlock, "Function should have no entry block initially")
        assertTrue(function.basicBlocks.isEmpty(), "Function should have no basic blocks initially")
    }
    
    @Test
    @DisplayName("Function should create correct arguments")
    fun testFunctionArguments() {
        val name = "testFunction"
        val functionType = FunctionType(IntegerType.I32, listOf(IntegerType.I32, IntegerType.I64, IntegerType.I8))
        val function = Function(name, functionType, module)
        
        assertEquals(3, function.parameters.size, "Function should have 3 parameters")
        
        val arg0 = function.parameters[0]
        assertEquals("arg0", arg0.name, "First argument should have name 'arg0'")
        assertEquals(IntegerType.I32, arg0.type, "First argument should have type I32")
        assertEquals(function, arg0.function, "First argument should reference the function")
        assertEquals(0, arg0.index, "First argument should have index 0")
        
        val arg1 = function.parameters[1]
        assertEquals("arg1", arg1.name, "Second argument should have name 'arg1'")
        assertEquals(IntegerType.I64, arg1.type, "Second argument should have type I64")
        assertEquals(function, arg1.function, "Second argument should reference the function")
        assertEquals(1, arg1.index, "Second argument should have index 1")
        
        val arg2 = function.parameters[2]
        assertEquals("arg2", arg2.name, "Third argument should have name 'arg2'")
        assertEquals(IntegerType.I8, arg2.type, "Third argument should have type I8")
        assertEquals(function, arg2.function, "Third argument should reference the function")
        assertEquals(2, arg2.index, "Third argument should have index 2")
    }
    
    @Test
    @DisplayName("Function should handle no parameters")
    fun testFunctionWithNoParameters() {
        val name = "testFunction"
        val functionType = FunctionType(IntegerType.I32, emptyList())
        val function = Function(name, functionType, module)
        
        assertEquals(0, function.parameters.size, "Function should have no parameters")
        assertTrue(function.parameters.isEmpty(), "Function parameters list should be empty")
    }
    
    @Test
    @DisplayName("Function should handle void return type")
    fun testFunctionWithVoidReturnType() {
        val name = "testFunction"
        val functionType = FunctionType(VoidType, listOf(IntegerType.I32))
        val function = Function(name, functionType, module)
        
        assertEquals(VoidType, function.returnType, "Function should have void return type")
    }
    
    @Test
    @DisplayName("Function should handle different parameter types")
    fun testFunctionWithDifferentParameterTypes() {
        val name = "testFunction"
        val functionType = FunctionType(IntegerType.I32, listOf(
            IntegerType.I1,   // Boolean
            IntegerType.I8,   // 8-bit integer
            IntegerType.I16,  // 16-bit integer
            IntegerType.I32,  // 32-bit integer
            IntegerType.I64   // 64-bit integer
        ))
        val function = Function(name, functionType, module)
        
        assertEquals(5, function.parameters.size, "Function should have 5 parameters")
        
        assertEquals(IntegerType.I1, function.parameters[0].type, "First parameter should be I1")
        assertEquals(IntegerType.I8, function.parameters[1].type, "Second parameter should be I8")
        assertEquals(IntegerType.I16, function.parameters[2].type, "Third parameter should be I16")
        assertEquals(IntegerType.I32, function.parameters[3].type, "Fourth parameter should be I32")
        assertEquals(IntegerType.I64, function.parameters[4].type, "Fifth parameter should be I64")
    }
    
    @Test
    @DisplayName("Function should handle basic blocks correctly")
    fun testFunctionBasicBlocks() {
        val name = "testFunction"
        val functionType = FunctionType(IntegerType.I32, emptyList())
        val function = Function(name, functionType, module)
        
        assertTrue(function.basicBlocks.isEmpty(), "Function should start with no basic blocks")
        assertNull(function.entryBlock, "Function should have no entry block initially")
        
        val entryBlock = BasicBlock("entry", function)
        function.basicBlocks.add(entryBlock)
        function.entryBlock = entryBlock
        
        assertEquals(1, function.basicBlocks.size, "Function should have one basic block")
        assertEquals(entryBlock, function.basicBlocks[0], "Function should contain the added basic block")
        assertEquals(entryBlock, function.entryBlock, "Function should have the correct entry block")
        assertEquals(function, entryBlock.function, "Basic block should reference the function")
    }
    
    @Test
    @DisplayName("Function should handle multiple basic blocks correctly")
    fun testFunctionMultipleBasicBlocks() {
        val name = "testFunction"
        val functionType = FunctionType(IntegerType.I32, emptyList())
        val function = Function(name, functionType, module)
        
        val entryBlock = BasicBlock("entry", function)
        val thenBlock = BasicBlock("then", function)
        val elseBlock = BasicBlock("else", function)
        val mergeBlock = BasicBlock("merge", function)
        
        function.basicBlocks.addAll(listOf(entryBlock, thenBlock, elseBlock, mergeBlock))
        function.entryBlock = entryBlock
        
        assertEquals(4, function.basicBlocks.size, "Function should have four basic blocks")
        assertEquals(entryBlock, function.entryBlock, "Function should have the correct entry block")
        assertTrue(function.basicBlocks.contains(entryBlock), "Function should contain entry block")
        assertTrue(function.basicBlocks.contains(thenBlock), "Function should contain then block")
        assertTrue(function.basicBlocks.contains(elseBlock), "Function should contain else block")
        assertTrue(function.basicBlocks.contains(mergeBlock), "Function should contain merge block")
    }
    
    @Test
    @DisplayName("Function should implement Value interface correctly")
    fun testFunctionImplementsValue() {
        val name = "testFunction"
        val functionType = FunctionType(IntegerType.I32, listOf(IntegerType.I32))
        val function = Function(name, functionType, module)
        
        // Should be assignable to Value
        val value: space.norb.llvm.core.Value = function
        assertEquals(name, value.name, "Function as Value should have correct name")
        assertEquals(functionType, value.type, "Function as Value should have correct type")
    }
    
    @Test
    @DisplayName("Function equality should work correctly")
    fun testFunctionEquality() {
        val functionType = FunctionType(IntegerType.I32, listOf(IntegerType.I32))
        
        val function1 = Function("sameName", functionType, module)
        val function2 = Function("sameName", functionType, module)
        val function3 = Function("differentName", functionType, module)
        
        assertEquals(function1, function2, "Functions with same properties should be equal")
        assertEquals(function1.hashCode(), function2.hashCode(), "Equal functions should have same hash code")
        
        assertNotEquals(function1, function3, "Functions with different names should not be equal")
    }
    
    @Test
    @DisplayName("Function equality should consider type")
    fun testFunctionEqualityByType() {
        val name = "testFunction"
        val functionType1 = FunctionType(IntegerType.I32, listOf(IntegerType.I32))
        val functionType2 = FunctionType(IntegerType.I64, listOf(IntegerType.I32))
        
        val function1 = Function(name, functionType1, module)
        val function2 = Function(name, functionType2, module)
        
        assertNotEquals(function1, function2, "Functions with different types should not be equal")
    }
    
    @Test
    @DisplayName("Function equality should consider module")
    fun testFunctionEqualityByModule() {
        val name = "testFunction"
        val functionType = FunctionType(IntegerType.I32, listOf(IntegerType.I32))
        
        val module1 = Module("module1")
        val module2 = Module("module2")
        
        val function1 = Function(name, functionType, module1)
        val function2 = Function(name, functionType, module2)
        
        assertNotEquals(function1, function2, "Functions in different modules should not be equal")
    }
    
    @Test
    @DisplayName("Function toString should contain name and type information")
    fun testFunctionToString() {
        val name = "testFunction"
        val functionType = FunctionType(IntegerType.I32, listOf(IntegerType.I32))
        val function = Function(name, functionType, module)
        
        val toString = function.toString()
        assertTrue(toString.contains(name), "toString should contain the function name")
        assertTrue(toString.contains(functionType.toString()), "toString should contain the function type")
    }
    
    @Test
    @DisplayName("Function should handle empty name")
    fun testFunctionEmptyName() {
        val name = ""
        val functionType = FunctionType(IntegerType.I32, listOf(IntegerType.I32))
        val function = Function(name, functionType, module)
        
        assertEquals(name, function.name, "Function should handle empty name")
    }
    
    @Test
    @DisplayName("Function should handle special characters in name")
    fun testFunctionSpecialCharactersInName() {
        val name = "test-function_123"
        val functionType = FunctionType(IntegerType.I32, listOf(IntegerType.I32))
        val function = Function(name, functionType, module)
        
        assertEquals(name, function.name, "Function should handle special characters in name")
    }
    
    @Test
    @DisplayName("Function should handle removal of basic blocks")
    fun testFunctionBasicBlockRemoval() {
        val name = "testFunction"
        val functionType = FunctionType(IntegerType.I32, emptyList())
        val function = Function(name, functionType, module)
        
        val entryBlock = BasicBlock("entry", function)
        val thenBlock = BasicBlock("then", function)
        
        function.basicBlocks.addAll(listOf(entryBlock, thenBlock))
        function.entryBlock = entryBlock
        
        assertEquals(2, function.basicBlocks.size, "Function should have two basic blocks")
        
        function.basicBlocks.remove(thenBlock)
        assertEquals(1, function.basicBlocks.size, "Function should have one basic block after removal")
        assertEquals(entryBlock, function.basicBlocks[0], "Function should still contain entry block")
        
        function.basicBlocks.remove(entryBlock)
        function.entryBlock = null
        assertTrue(function.basicBlocks.isEmpty(), "Function should have no basic blocks after removing entry")
        assertNull(function.entryBlock, "Function should have no entry block after removal")
    }
    
    @Test
    @DisplayName("Function should handle clearing of basic blocks")
    fun testFunctionClearBasicBlocks() {
        val name = "testFunction"
        val functionType = FunctionType(IntegerType.I32, emptyList())
        val function = Function(name, functionType, module)
        
        val entryBlock = BasicBlock("entry", function)
        val thenBlock = BasicBlock("then", function)
        val elseBlock = BasicBlock("else", function)
        
        function.basicBlocks.addAll(listOf(entryBlock, thenBlock, elseBlock))
        function.entryBlock = entryBlock
        
        assertEquals(3, function.basicBlocks.size, "Function should have three basic blocks")
        
        function.basicBlocks.clear()
        function.entryBlock = null
        
        assertTrue(function.basicBlocks.isEmpty(), "Basic blocks should be cleared")
        assertNull(function.entryBlock, "Entry block should be cleared")
    }
    
    @Test
    @DisplayName("Function should maintain type information")
    fun testFunctionTypeInformation() {
        val name = "testFunction"
        val functionType = FunctionType(IntegerType.I32, listOf(IntegerType.I32))
        val function = Function(name, functionType, module)
        
        assertTrue(function.type.isFunctionType(), "Function type should be a function type")
        assertFalse(function.type.isIntegerType(), "Function type should not be an integer type")
        assertFalse(function.type.isFloatingPointType(), "Function type should not be a floating-point type")
        assertFalse(function.type.isPointerType(), "Function type should not be a pointer type")
    }
    
    @Test
    @DisplayName("Function should have default EXTERNAL linkage")
    fun testFunctionDefaultLinkage() {
        val name = "testFunction"
        val functionType = FunctionType(IntegerType.I32, listOf(IntegerType.I32))
        val function = Function(name, functionType, module)
        
        assertEquals(LinkageType.EXTERNAL, function.linkage, "Function should have EXTERNAL linkage by default")
    }
    
    @Test
    @DisplayName("Function should accept linkage parameter")
    fun testFunctionWithLinkage() {
        val name = "testFunction"
        val functionType = FunctionType(IntegerType.I32, listOf(IntegerType.I32))
        
        val externalFunction = Function(name + "_external", functionType, module, LinkageType.EXTERNAL)
        val internalFunction = Function(name + "_internal", functionType, module, LinkageType.INTERNAL)
        val privateFunction = Function(name + "_private", functionType, module, LinkageType.PRIVATE)
        
        assertEquals(LinkageType.EXTERNAL, externalFunction.linkage, "Function should have EXTERNAL linkage")
        assertEquals(LinkageType.INTERNAL, internalFunction.linkage, "Function should have INTERNAL linkage")
        assertEquals(LinkageType.PRIVATE, privateFunction.linkage, "Function should have PRIVATE linkage")
    }
    
    @Test
    @DisplayName("Function equality should consider linkage")
    fun testFunctionEqualityByLinkage() {
        val name = "testFunction"
        val functionType = FunctionType(IntegerType.I32, listOf(IntegerType.I32))
        
        val function1 = Function(name, functionType, module, LinkageType.EXTERNAL)
        val function2 = Function(name, functionType, module, LinkageType.EXTERNAL)
        val function3 = Function(name, functionType, module, LinkageType.INTERNAL)
        
        assertEquals(function1, function2, "Functions with same linkage should be equal")
        assertNotEquals(function1, function3, "Functions with different linkage should not be equal")
    }
}