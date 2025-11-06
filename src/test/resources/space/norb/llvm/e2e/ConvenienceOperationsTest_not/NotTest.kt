package space.norb.llvm.e2e.extracted.convenience_operations

import space.norb.llvm.structure.Module
import space.norb.llvm.structure.Function
import space.norb.llvm.structure.BasicBlock
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.IntegerType
import space.norb.llvm.builder.IRBuilder

/**
 * Extracted test case for NOT operation.
 * Originally from ConvenienceOperationsTest.testNotOperation()
 */
object NotTest {
    
    /**
     * Builds the IR for this test case.
     *
     * @param module The module to add the IR to
     * @param builder The IR builder to use
     */
    fun buildIR(module: Module, builder: IRBuilder) {
        // Create function type: i32 (i32)
        val functionType = FunctionType(
            returnType = IntegerType.I32,
            paramTypes = listOf(IntegerType.I32)
        )
        
        // Create the function
        val function = builder.createFunction("not_operation", functionType)
        module.functions.add(function)
        
        // Create entry block
        val entryBlock = builder.createBasicBlock("entry", function)
        function.basicBlocks.add(entryBlock)
        
        if (function.entryBlock == null) {
            function.entryBlock = entryBlock
        }
        
        builder.positionAtEnd(entryBlock)
        
        // Get function argument
        val arg0 = function.parameters[0]
        
        // Create NOT operation
        val result = builder.buildNot(arg0, "result")
        
        // Return the result
        builder.buildRet(result)
    }
}