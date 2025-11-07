package space.norb.llvm.e2e.extracted.integer_widths

import space.norb.llvm.structure.Module
import space.norb.llvm.structure.Function
import space.norb.llvm.structure.BasicBlock
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.IntegerType
import space.norb.llvm.builder.IRBuilder
import space.norb.llvm.values.constants.IntConstant

/**
 * Extracted test case for i64 operations.
 * Originally from IntegerWidthsTest.testI64Operations()
 */
object I64OperationsTest {
    
    /**
     * Builds the IR for this test case.
     *
     * @param module The module to add the IR to
     * @param builder The IR builder to use
     */
    fun buildIR(module: Module, builder: IRBuilder) {
        // Create function type: i64 (i64, i64)
        val functionType = FunctionType(
            returnType = IntegerType.I64,
            paramTypes = listOf(IntegerType.I64, IntegerType.I64)
        )
        
        // Create the function
        val function = builder.createFunction("i64_ops", functionType)
        
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
        
        // Create operations with i64 values
        val temp1 = builder.insertAdd(arg0, arg1, "temp1")  // temp1 = a + b
        val temp2 = builder.insertSDiv(temp1, IntConstant(2L, IntegerType.I64), "temp2")  // temp2 = temp1 / 2
        val temp3 = builder.insertAnd(temp2, IntConstant(281474976710655L, IntegerType.I64), "temp3")  // temp3 = temp2 & mask
        val result = builder.insertXor(temp3, IntConstant(Long.MIN_VALUE, IntegerType.I64, isUnsigned = true), "result")  // result = temp3 ^ sign_bit
        
        // Return the result
        builder.insertRet(result)
    }
}