package space.norb.llvm.e2e.extracted.integer_widths

import space.norb.llvm.structure.Module
import space.norb.llvm.structure.Function
import space.norb.llvm.structure.BasicBlock
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.IntegerType
import space.norb.llvm.builder.IRBuilder

/**
 * Extracted test case for mixed width operations.
 * Originally from IntegerWidthsTest.testMixedWidthOperations()
 */
object MixedWidthOperationsTest {
    
    /**
     * Builds the IR for this test case.
     *
     * @param module The module to add the IR to
     * @param builder The IR builder to use
     */
    fun buildIR(module: Module, builder: IRBuilder) {
        // Create function type: i64 (i8, i16, i32, i64)
        val functionType = FunctionType(
            returnType = IntegerType.I64,
            paramTypes = listOf(IntegerType.I8, IntegerType.I16, IntegerType.I32, IntegerType.I64)
        )
        
        // Create the function
        val function = builder.createFunction("mixed_width_ops", functionType)
        
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
        val arg0 = function.parameters[0]  // i8
        val arg1 = function.parameters[1]  // i16
        val arg2 = function.parameters[2]  // i32
        val arg3 = function.parameters[3]  // i64
        
        // Create operations with mixed width values
        val temp1 = builder.insertSExt(arg0, IntegerType.I64, "temp1")  // temp1 = sext i8 to i64
        val temp2 = builder.insertSExt(arg1, IntegerType.I64, "temp2")  // temp2 = sext i16 to i64
        val temp3 = builder.insertSExt(arg2, IntegerType.I64, "temp3")  // temp3 = sext i32 to i64
        val temp4 = builder.insertAdd(temp1, temp2, "temp4")  // temp4 = temp1 + temp2
        val temp5 = builder.insertAdd(temp4, temp3, "temp5")  // temp5 = temp4 + temp3
        val result = builder.insertAdd(temp5, arg3, "result")  // result = temp5 + arg3
        
        // Return the result
        builder.insertRet(result)
    }
}