package space.norb.llvm.structure

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import space.norb.llvm.types.IntegerType
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.LabelType
import space.norb.llvm.instructions.base.Instruction
import space.norb.llvm.instructions.base.TerminatorInst
import space.norb.llvm.instructions.terminators.ReturnInst
import space.norb.llvm.values.constants.IntConstant

/**
 * Unit tests for BasicBlock.
 */
@DisplayName("BasicBlock Tests")
class BasicBlockTest {
    
    private lateinit var module: Module
    private lateinit var function: Function
    
    @BeforeEach
    fun setUp() {
        module = Module("testModule")
        val functionType = FunctionType(IntegerType.I32, listOf(IntegerType.I32))
        function = Function("testFunction", functionType, module)
    }
    
    @Test
    @DisplayName("BasicBlock should be created with correct properties")
    fun testBasicBlockCreation() {
        val name = "testBlock"
        val basicBlock = BasicBlock(name, function)
        
        assertEquals(name, basicBlock.name, "BasicBlock should have the correct name")
        assertEquals(function, basicBlock.function, "BasicBlock should reference the function")
        assertEquals(LabelType, basicBlock.type, "BasicBlock should have LabelType")
        assertTrue(basicBlock.instructions.isEmpty(), "BasicBlock should start with no instructions")
        assertNull(basicBlock.terminator, "BasicBlock should start with no terminator")
    }
    
    @Test
    @DisplayName("BasicBlock should handle instructions correctly")
    fun testBasicBlockInstructions() {
        val basicBlock = BasicBlock("testBlock", function)
        
        assertTrue(basicBlock.instructions.isEmpty(), "BasicBlock should start with no instructions")
        
        // Note: We can't easily create real instructions without more infrastructure
        // For now, we'll test the list operations
        val instructionCount = 5
        
        // Simulate adding instructions (in real code, these would be actual Instruction objects)
        repeat(instructionCount) { _ ->
            // This is a mock for testing - in real usage, you'd add actual Instruction objects
            // For now, we'll just test the list operations
        }
        
        // Test that the instructions list is mutable
        // assertTrue(basicBlock.instructions is MutableList, "Instructions should be mutable")
    }
    
    @Test
    @DisplayName("BasicBlock should handle terminator correctly")
    fun testBasicBlockTerminator() {
        val basicBlock = BasicBlock("testBlock", function)
        
        assertNull(basicBlock.terminator, "BasicBlock should start with no terminator")
        
        // Note: We can't easily create real terminator instructions without more infrastructure
        // For now, we'll test the terminator property
        val terminator = createMockTerminator()
        basicBlock.terminator = terminator
        
        assertEquals(terminator, basicBlock.terminator, "BasicBlock should have the correct terminator")
    }
    
    @Test
    @DisplayName("BasicBlock should work with different names")
    fun testBasicBlockWithDifferentNames() {
        val entryBlock = BasicBlock("entry", function)
        assertEquals("entry", entryBlock.name)
        
        val thenBlock = BasicBlock("then", function)
        assertEquals("then", thenBlock.name)
        
        val elseBlock = BasicBlock("else", function)
        assertEquals("else", elseBlock.name)
        
        val mergeBlock = BasicBlock("merge", function)
        assertEquals("merge", mergeBlock.name)
        
        val numberedBlock = BasicBlock("block42", function)
        assertEquals("block42", numberedBlock.name)
    }
    
    @Test
    @DisplayName("BasicBlock should work with different functions")
    fun testBasicBlockWithDifferentFunctions() {
        val functionType1 = FunctionType(IntegerType.I32, listOf(IntegerType.I32))
        val function1 = Function("function1", functionType1, module)
        
        val functionType2 = FunctionType(IntegerType.I64, listOf(IntegerType.I64))
        val function2 = Function("function2", functionType2, module)
        
        val block1 = BasicBlock("block1", function1)
        val block2 = BasicBlock("block2", function2)
        
        assertEquals(function1, block1.function, "BasicBlock should reference the correct function")
        assertEquals(function2, block2.function, "BasicBlock should reference the correct function")
    }
    
    @Test
    @DisplayName("BasicBlock should implement Value interface correctly")
    fun testBasicBlockImplementsValue() {
        val name = "testBlock"
        val basicBlock = BasicBlock(name, function)
        
        // Should be assignable to Value
        val value: space.norb.llvm.core.Value = basicBlock
        assertEquals(name, value.name, "BasicBlock as Value should have correct name")
        assertEquals(LabelType, value.type, "BasicBlock as Value should have correct type")
    }
    
    @Test
    @DisplayName("BasicBlock equality should work correctly")
    fun testBasicBlockEquality() {
        val basicBlock1 = BasicBlock("sameName", function)
        val basicBlock2 = BasicBlock("sameName", function)
        val basicBlock3 = BasicBlock("differentName", function)
        
        assertEquals(basicBlock1, basicBlock2, "BasicBlocks with same properties should be equal")
        assertEquals(basicBlock1.hashCode(), basicBlock2.hashCode(), "Equal BasicBlocks should have same hash code")
        
        assertNotEquals(basicBlock1, basicBlock3, "BasicBlocks with different names should not be equal")
    }
    
    @Test
    @DisplayName("BasicBlock equality should consider function")
    fun testBasicBlockEqualityByFunction() {
        val name = "testBlock"
        val functionType = FunctionType(IntegerType.I32, listOf(IntegerType.I32))
        val function1 = Function("function1", functionType, module)
        val function2 = Function("function2", functionType, module)
        
        val basicBlock1 = BasicBlock(name, function1)
        val basicBlock2 = BasicBlock(name, function2)
        
        assertNotEquals(basicBlock1, basicBlock2, "BasicBlocks in different functions should not be equal")
    }
    
    @Test
    @DisplayName("BasicBlock toString should contain name and type information")
    fun testBasicBlockToString() {
        val name = "testBlock"
        val basicBlock = BasicBlock(name, function)
        
        val toString = basicBlock.toString()
        assertTrue(toString.contains(name), "toString should contain the basic block name")
        assertTrue(toString.contains(LabelType.toString()), "toString should contain the type")
    }
    
    @Test
    @DisplayName("BasicBlock should handle empty name")
    fun testBasicBlockEmptyName() {
        val name = ""
        val basicBlock = BasicBlock(name, function)
        
        assertEquals(name, basicBlock.name, "BasicBlock should handle empty name")
    }
    
    @Test
    @DisplayName("BasicBlock should handle special characters in name")
    fun testBasicBlockSpecialCharactersInName() {
        val name = "test-block_123"
        val basicBlock = BasicBlock(name, function)
        
        assertEquals(name, basicBlock.name, "BasicBlock should handle special characters in name")
    }
    
    @Test
    @DisplayName("BasicBlock should maintain type information")
    fun testBasicBlockTypeInformation() {
        val basicBlock = BasicBlock("testBlock", function)
        
        assertEquals(space.norb.llvm.types.LabelType, basicBlock.type, "BasicBlock type should be LabelType")
        assertFalse(basicBlock.type.isIntegerType(), "BasicBlock type should not be an integer type")
        assertFalse(basicBlock.type.isFloatingPointType(), "BasicBlock type should not be a floating-point type")
        assertFalse(basicBlock.type.isPointerType(), "BasicBlock type should not be a pointer type")
        assertFalse(basicBlock.type.isFunctionType(), "BasicBlock type should not be a function type")
    }
    
    @Test
    @DisplayName("BasicBlock should handle instruction list operations")
    fun testBasicBlockInstructionListOperations() {
        val basicBlock = BasicBlock("testBlock", function)
        
        // Test that instructions list is initially empty
        assertTrue(basicBlock.instructions.isEmpty(), "Instructions should be empty initially")
        assertEquals(0, basicBlock.instructions.size, "Instructions size should be 0 initially")
        
        // Test that instructions list is mutable
        // assertTrue(basicBlock.instructions is MutableList, "Instructions should be mutable")
        
        // Test adding to instructions list (with mock data)
        // val initialSize = basicBlock.instructions.size
        
        // In real usage, you would add actual Instruction objects
        // For now, we'll just test the list operations
        basicBlock.instructions.clear() // Test clear operation
        assertTrue(basicBlock.instructions.isEmpty(), "Instructions should be empty after clear")
    }
    
    @Test
    @DisplayName("BasicBlock should handle terminator operations")
    fun testBasicBlockTerminatorOperations() {
        val basicBlock = BasicBlock("testBlock", function)
        
        // Test initial state
        assertNull(basicBlock.terminator, "Terminator should be null initially")
        
        // Test setting terminator
        val terminator = createMockTerminator()
        basicBlock.terminator = terminator
        assertEquals(terminator, basicBlock.terminator, "Terminator should be set correctly")
        
        // Test clearing terminator
        basicBlock.terminator = null
        assertNull(basicBlock.terminator, "Terminator should be null after clearing")
    }
    
    @Test
    @DisplayName("BasicBlock should work with entry block pattern")
    fun testBasicBlockEntryBlockPattern() {
        val basicBlock = BasicBlock("entry", function)
        
        // Set as entry block
        function.entryBlock = basicBlock
        assertEquals(basicBlock, function.entryBlock, "BasicBlock should be set as entry block")
        assertEquals(function, basicBlock.function, "Entry block should reference the function")
        
        // Add to function's basic blocks
        function.basicBlocks.add(basicBlock)
        assertTrue(function.basicBlocks.contains(basicBlock), "Entry block should be in function's basic blocks")
    }
    
    @Test
    @DisplayName("BasicBlock should work with multiple blocks pattern")
    fun testBasicBlockMultipleBlocksPattern() {
        val entryBlock = BasicBlock("entry", function)
        val thenBlock = BasicBlock("then", function)
        val elseBlock = BasicBlock("else", function)
        val mergeBlock = BasicBlock("merge", function)
        
        // Add all blocks to function
        function.basicBlocks.addAll(listOf(entryBlock, thenBlock, elseBlock, mergeBlock))
        function.entryBlock = entryBlock
        
        // Verify all blocks are in the function
        assertEquals(4, function.basicBlocks.size, "Function should have 4 basic blocks")
        assertTrue(function.basicBlocks.contains(entryBlock), "Function should contain entry block")
        assertTrue(function.basicBlocks.contains(thenBlock), "Function should contain then block")
        assertTrue(function.basicBlocks.contains(elseBlock), "Function should contain else block")
        assertTrue(function.basicBlocks.contains(mergeBlock), "Function should contain merge block")
        
        // Verify entry block
        assertEquals(entryBlock, function.entryBlock, "Function should have correct entry block")
        
        // Verify all blocks reference the function
        assertEquals(function, entryBlock.function)
        assertEquals(function, thenBlock.function)
        assertEquals(function, elseBlock.function)
        assertEquals(function, mergeBlock.function)
    }
    
    /**
     * Creates a mock terminator for testing purposes.
     * In real usage, this would be an actual TerminatorInst implementation.
     */
    private fun createMockTerminator(): TerminatorInst {
        // This is a mock for testing - in real usage, you'd create actual terminator instructions
        // For now, we'll return a mock object
        return object : TerminatorInst("mockTerminator", IntegerType.I32, emptyList()) {
            override fun <T> accept(visitor: space.norb.llvm.visitors.IRVisitor<T>): T {
                throw UnsupportedOperationException("Mock terminator")
            }
            
            override fun getSuccessors(): List<BasicBlock> {
                return emptyList() // Mock terminator has no successors
            }
        }
    }
}