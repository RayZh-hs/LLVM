package space.norb.llvm.e2e.extracted.binary_operations

import space.norb.llvm.structure.Module
import space.norb.llvm.structure.Function
import space.norb.llvm.structure.BasicBlock
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.IntegerType
import space.norb.llvm.builder.IRBuilder
import space.norb.llvm.values.constants.IntConstant

/**
 * Extracted test case for complex binary operations.
 * Originally from BinaryOperationsTest.testComplexBinaryOperations()
 */
object ComplexBinaryOperationsTest {
    
    /**
     * Builds the IR for this test case.
     *
     * @param module The module to add the IR to
     * @param builder The IR builder to use
     */
    fun buildIR(module: Module, builder: IRBuilder) {
        // Create function type: i32 (i32, i32, i32)
        val functionType = FunctionType(
            returnType = IntegerType.I32,
            paramTypes = listOf(IntegerType.I32, IntegerType.I32, IntegerType.I32)
        )
        
        // Create the function
        val function = builder.createFunction("complex_binary_ops", functionType)
        
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
        val arg2 = function.parameters[2]
        
        // Create a sequence of binary operations
        val temp1 = builder.insertMul(arg0, arg1, "temp1")  // temp1 = a * b
        val temp2 = builder.insertAdd(temp1, arg2, "temp2")  // temp2 = temp1 + c
        val temp3 = builder.insertAnd(temp2, IntConstant(255, IntegerType.I32), "temp3")  // temp3 = temp2 & 255
        val result = builder.insertXor(temp3, IntConstant(128, IntegerType.I32), "result")  // result = temp3 ^ 128
        
        // Return the result
        builder.insertRet(result)
    }
}