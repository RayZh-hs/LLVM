package space.norb.llvm.e2e.extracted.cast_operations

import space.norb.llvm.structure.Module
import space.norb.llvm.structure.Function
import space.norb.llvm.structure.BasicBlock
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.IntegerType
import space.norb.llvm.builder.IRBuilder

/**
 * Extracted test case for trunc operation.
 * Originally from CastOperationsTest.testTruncFunction()
 */
object TruncTest {
    
    /**
     * Builds the IR for this test case.
     *
     * @param module The module to add the IR to
     * @param builder The IR builder to use
     */
    fun buildIR(module: Module, builder: IRBuilder) {
        // Create function type: i8 (i32)
        val functionType = FunctionType(
            returnType = IntegerType.I8,
            paramTypes = listOf(IntegerType.I32)
        )
        
        // Create the function
        val function = builder.createFunction("trunc", functionType)
        
        // Add the function to the module
        module.functions.add(function)
        
        // Create entry block
        val entryBlock = function.insertBasicBlock("entry")
        
        // Set the entry block if not already set
        if (function.entryBlock == null) {
            function.entryBlock = entryBlock
        }
        
        builder.positionAtEnd(entryBlock)
        
        // Get function argument
        val arg0 = function.parameters[0]
        
        // Create trunc instruction (i32 to i8)
        val result = builder.insertTrunc(arg0, IntegerType.I8, "result")
        
        // Return the result
        builder.insertRet(result)
    }
}