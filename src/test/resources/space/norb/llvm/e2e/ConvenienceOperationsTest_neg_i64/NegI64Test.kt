package space.norb.llvm.e2e.extracted.convenience_operations

import space.norb.llvm.structure.Module
import space.norb.llvm.structure.Function
import space.norb.llvm.structure.BasicBlock
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.IntegerType
import space.norb.llvm.builder.IRBuilder

/**
 * Extracted test case for negation operation on i64.
 * Originally from ConvenienceOperationsTest.testNegOperationI64()
 */
object NegI64Test {
    
    /**
     * Builds the IR for this test case.
     *
     * @param module The module to add the IR to
     * @param builder The IR builder to use
     */
    fun buildIR(module: Module, builder: IRBuilder) {
        // Create function type: i64 (i64)
        val functionType = FunctionType(
            returnType = IntegerType.I64,
            paramTypes = listOf(IntegerType.I64)
        )
        
        // Create the function
        val function = builder.createFunction("neg_operation_i64", functionType)
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
        
        // Create negation operation
        val result = builder.buildNeg(arg0, "result")
        
        // Return the result
        builder.buildRet(result)
    }
}