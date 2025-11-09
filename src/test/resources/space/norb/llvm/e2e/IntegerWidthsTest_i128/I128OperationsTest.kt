package space.norb.llvm.e2e.extracted.integer_widths

import space.norb.llvm.structure.Module
import space.norb.llvm.structure.Function
import space.norb.llvm.structure.BasicBlock
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.IntegerType
import space.norb.llvm.values.constants.IntConstant
import space.norb.llvm.builder.IRBuilder

/**
 * Extracted test case for i128 operations.
 * Originally from IntegerWidthsTest.testI128Operations()
 */
object I128OperationsTest {
    
    /**
     * Builds the IR for this test case.
     *
     * @param module The module to add the IR to
     * @param builder The IR builder to use
     */
    fun buildIR(module: Module, builder: IRBuilder) {
        // Create function type: i128 (i128, i128)
        val functionType = FunctionType(
            returnType = IntegerType.I128,
            paramTypes = listOf(IntegerType.I128, IntegerType.I128)
        )
        
        // Create the function
        val function = builder.createFunction("i128_ops", functionType)
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
        
        // Create operations with i128 values
        val temp1 = builder.insertMul(arg0, arg1, "temp1")  // temp1 = a * b
        val temp2 = builder.insertAdd(temp1, IntConstant(1L, IntegerType.I128), "temp2")  // temp2 = temp1 + 1
        val temp3 = builder.insertOr(temp2, IntConstant(-1L, IntegerType.I128), "temp3")  // temp3 = temp2 | -1 (all bits set)
        val result = builder.insertAnd(temp3, IntConstant(-1L, IntegerType.I128), "result")  // result = temp3 & mask
        
        // Return the result
        builder.insertRet(result)
    }
}