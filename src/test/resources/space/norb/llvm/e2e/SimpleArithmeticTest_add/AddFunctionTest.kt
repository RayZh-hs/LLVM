package space.norb.llvm.e2e.extracted.simple_arithmetic

import space.norb.llvm.structure.Module
import space.norb.llvm.structure.Function
import space.norb.llvm.structure.BasicBlock
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.IntegerType
import space.norb.llvm.builder.IRBuilder

/**
 * Extracted test case for simple addition function.
 * Originally from SimpleArithmeticTest.testAddFunction()
 */
object AddFunctionTest {
    
    /**
     * Builds the IR for this test case.
     *
     * @param module The module to add the IR to
     * @param builder The IR builder to use
     */
    fun buildIR(module: Module, builder: IRBuilder) {
        // Create function type: i32 (i32, i32)
        val functionType = FunctionType(
            returnType = IntegerType.I32,
            paramTypes = listOf(IntegerType.I32, IntegerType.I32)
        )
        
        // Create the function
        val function = builder.createFunction("add", functionType)
        module.functions.add(function)
        
        // Create entry block
        val entryBlock = function.insertBasicBlock("entry")
        
        if (function.entryBlock == null) {
            function.entryBlock = entryBlock
        }
        
        builder.positionAtEnd(entryBlock)
        
        // Get function arguments
        val arg0 = function.parameters[0]
        val arg1 = function.parameters[1]
        
        // Create add instruction
        val result = builder.insertAdd(arg0, arg1, "result")
        
        // Return the result
        builder.insertRet(result)
    }
}