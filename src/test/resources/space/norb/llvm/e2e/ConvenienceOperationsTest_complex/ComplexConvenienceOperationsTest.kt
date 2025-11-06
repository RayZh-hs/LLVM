package space.norb.llvm.e2e.extracted.convenience_operations

import space.norb.llvm.structure.Module
import space.norb.llvm.structure.Function
import space.norb.llvm.structure.BasicBlock
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.IntegerType
import space.norb.llvm.builder.IRBuilder

/**
 * Extracted test case for complex convenience operations.
 * Originally from ConvenienceOperationsTest.testComplexConvenienceOperations()
 */
object ComplexConvenienceOperationsTest {
    
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
        val function = builder.createFunction("complex_convenience_ops", functionType)
        module.functions.add(function)
        
        // Create entry block
        val entryBlock = builder.createBasicBlock("entry", function)
        function.basicBlocks.add(entryBlock)
        
        if (function.entryBlock == null) {
            function.entryBlock = entryBlock
        }
        
        builder.positionAtEnd(entryBlock)
        
        // Get function arguments
        val arg0 = function.parameters[0]
        val arg1 = function.parameters[1]
        
        // Create a sequence of convenience operations
        val notArg0 = builder.buildNot(arg0, "not_arg0")  // not_arg0 = ~arg0
        val negArg1 = builder.buildNeg(arg1, "neg_arg1")  // neg_arg1 = -arg1
        val temp = builder.buildAnd(notArg0, negArg1, "temp")  // temp = not_arg0 & neg_arg1
        val result = builder.buildNot(temp, "result")  // result = ~temp
        
        // Return the result
        builder.buildRet(result)
    }
}