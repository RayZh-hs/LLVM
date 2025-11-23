package space.norb.llvm.instructions.binary

import org.junit.jupiter.api.Test
import space.norb.llvm.builder.IRBuilder
import space.norb.llvm.structure.Module
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.IntegerType
import space.norb.llvm.visitors.IRPrinter
import kotlin.test.assertEquals

class ModuloInstructionsTest {
    
    @Test
    fun testURemInstruction() {
        val module = Module("test")
        val builder = IRBuilder(module)
        
        // Create function type: i32 (i32, i32)
        val functionType = FunctionType(
            returnType = IntegerType.I32,
            paramTypes = listOf(IntegerType.I32, IntegerType.I32)
        )
        
        // Create the function
        val function = builder.createFunction("test_urem", functionType)
        module.functions.add(function)
        
        // Create entry block
        val entryBlock = function.insertBasicBlock("entry")
        function.entryBlock = entryBlock
        builder.positionAtEnd(entryBlock)
        
        // Get function arguments
        val arg0 = function.parameters[0]
        val arg1 = function.parameters[1]
        
        // Create urem instruction
        val result = builder.insertURem(arg0, arg1, "result")
        
        // Return the result
        builder.insertRet(result)
        
        // Print the IR
        val ir = IRPrinter().print(module)
        
        // Verify the IR contains the urem instruction
        assert(ir.contains("urem i32 %arg0, %arg1")) { "IR should contain urem instruction" }
        assert(ir.contains("define i32 @test_urem")) { "IR should contain function definition" }
    }
    
    @Test
    fun testSRemInstruction() {
        val module = Module("test")
        val builder = IRBuilder(module)
        
        // Create function type: i32 (i32, i32)
        val functionType = FunctionType(
            returnType = IntegerType.I32,
            paramTypes = listOf(IntegerType.I32, IntegerType.I32)
        )
        
        // Create the function
        val function = builder.createFunction("test_srem", functionType)
        module.functions.add(function)
        
        // Create entry block
        val entryBlock = function.insertBasicBlock("entry")
        function.entryBlock = entryBlock
        builder.positionAtEnd(entryBlock)
        
        // Get function arguments
        val arg0 = function.parameters[0]
        val arg1 = function.parameters[1]
        
        // Create srem instruction
        val result = builder.insertSRem(arg0, arg1, "result")
        
        // Return the result
        builder.insertRet(result)
        
        // Print the IR
        val ir = IRPrinter().print(module)
        
        // Verify the IR contains the srem instruction
        assert(ir.contains("srem i32 %arg0, %arg1")) { "IR should contain srem instruction" }
        assert(ir.contains("define i32 @test_srem")) { "IR should contain function definition" }
    }
    
    @Test
    fun testModuloInstructionProperties() {
        val module = Module("test")
        val builder = IRBuilder(module)
        
        // Create function type: i32 (i32, i32)
        val functionType = FunctionType(
            returnType = IntegerType.I32,
            paramTypes = listOf(IntegerType.I32, IntegerType.I32)
        )
        
        // Create the function
        val function = builder.createFunction("test_properties", functionType)
        module.functions.add(function)
        
        // Create entry block
        val entryBlock = function.insertBasicBlock("entry")
        function.entryBlock = entryBlock
        builder.positionAtEnd(entryBlock)
        
        // Get function arguments
        val arg0 = function.parameters[0]
        val arg1 = function.parameters[1]
        
        // Create modulo instructions
        val urem = builder.insertURem(arg0, arg1, "urem_result")
        val srem = builder.insertSRem(arg0, arg1, "srem_result")
        
        // Test properties
        assertEquals("urem", urem.getOpcodeName())
        assertEquals("srem", srem.getOpcodeName())
        assertEquals(false, urem.isCommutative())
        assertEquals(false, srem.isCommutative())
        assertEquals(false, urem.isAssociative())
        assertEquals(false, srem.isAssociative())
        
        // Test accessor methods
        assertEquals(arg0, urem.getDividend())
        assertEquals(arg1, urem.getDivisor())
        assertEquals(arg0, urem.getLeftOperand())
        assertEquals(arg1, urem.getRightOperand())
        
        assertEquals(arg0, srem.getDividend())
        assertEquals(arg1, srem.getDivisor())
        assertEquals(arg0, srem.getLeftOperand())
        assertEquals(arg1, srem.getRightOperand())
    }
}