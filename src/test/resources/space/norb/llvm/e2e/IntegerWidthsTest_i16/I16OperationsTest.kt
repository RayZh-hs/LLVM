package space.norb.llvm.e2e.extracted.integer_widths

import space.norb.llvm.structure.Module
import space.norb.llvm.structure.Function
import space.norb.llvm.structure.BasicBlock
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.IntegerType
import space.norb.llvm.builder.IRBuilder
import space.norb.llvm.values.constants.IntConstant

/**
 * Extracted test case for i16 operations.
 * Originally from IntegerWidthsTest.testI16Operations()
 */
object I16OperationsTest {
    
    /**
     * Builds the IR for this test case.
     *
     * @param module The module to add the IR to
     * @param builder The IR builder to use
     */
    fun buildIR(module: Module, builder: IRBuilder) {
        // Create function type: i16 (i16, i16)
        val functionType = FunctionType(
            returnType = IntegerType.I16,
            paramTypes = listOf(IntegerType.I16, IntegerType.I16)
        )
        
        // Create the function
        val function = builder.createFunction("i16_ops", functionType)
        
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
        
        // Create operations with i16 values
        val temp1 = builder.insertMul(arg0, arg1, "temp1")  // temp1 = a * b
        val temp2 = builder.insertOr(temp1, IntConstant(4095, IntegerType.I16), "temp2")  // temp2 = temp1 | 4095
        val result = builder.insertSub(temp2, IntConstant(1000, IntegerType.I16), "result")  // result = temp2 - 1000
        
        // Return the result
        builder.insertRet(result)
    }
}