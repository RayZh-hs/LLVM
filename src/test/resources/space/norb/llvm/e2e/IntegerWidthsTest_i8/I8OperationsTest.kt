package space.norb.llvm.e2e.extracted.integer_widths

import space.norb.llvm.structure.Module
import space.norb.llvm.structure.Function
import space.norb.llvm.structure.BasicBlock
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.IntegerType
import space.norb.llvm.builder.IRBuilder
import space.norb.llvm.values.constants.IntConstant

/**
 * Extracted test case for i8 operations.
 * Originally from IntegerWidthsTest.testI8Operations()
 */
object I8OperationsTest {
    
    /**
     * Builds the IR for this test case.
     *
     * @param module The module to add the IR to
     * @param builder The IR builder to use
     */
    fun buildIR(module: Module, builder: IRBuilder) {
        // Create function type: i8 (i8, i8)
        val functionType = FunctionType(
            returnType = IntegerType.I8,
            paramTypes = listOf(IntegerType.I8, IntegerType.I8)
        )
        
        // Create the function
        val function = builder.createFunction("i8_ops", functionType)
        
        // Add the function to the module
        module.functions.add(function)
        
        // Create entry block
        val entryBlock = builder.createBasicBlock("entry", function)
        
        // Add the basic block to the function
        function.basicBlocks.add(entryBlock)
        
        // Set the entry block if not already set
        if (function.entryBlock == null) {
            function.entryBlock = entryBlock
        }
        
        builder.positionAtEnd(entryBlock)
        
        // Get function arguments
        val arg0 = function.parameters[0]
        val arg1 = function.parameters[1]
        
        // Create operations with i8 values
        val temp1 = builder.buildAdd(arg0, arg1, "temp1")  // temp1 = a + b
        val temp2 = builder.buildAnd(temp1, IntConstant(15, IntegerType.I8), "temp2")  // temp2 = temp1 & 15
        val result = builder.buildXor(temp2, IntConstant(255, IntegerType.I8), "result")  // result = temp2 ^ 255
        
        // Return the result
        builder.buildRet(result)
    }
}