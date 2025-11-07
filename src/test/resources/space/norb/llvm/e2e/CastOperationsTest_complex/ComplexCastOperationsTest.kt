package space.norb.llvm.e2e.extracted.cast_operations

import space.norb.llvm.structure.Module
import space.norb.llvm.structure.Function
import space.norb.llvm.structure.BasicBlock
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.IntegerType
import space.norb.llvm.builder.IRBuilder

/**
 * Extracted test case for complex cast operations.
 * Originally from CastOperationsTest.testComplexCastOperations()
 */
object ComplexCastOperationsTest {
    
    /**
     * Builds the IR for this test case.
     *
     * @param module The module to add the IR to
     * @param builder The IR builder to use
     */
    fun buildIR(module: Module, builder: IRBuilder) {
        // Create function type: i64 (i8, i32)
        val functionType = FunctionType(
            returnType = IntegerType.I64,
            paramTypes = listOf(IntegerType.I8, IntegerType.I32)
        )
        
        // Create the function
        val function = builder.createFunction("complex_cast_ops", functionType)
        
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
        val arg1 = function.parameters[1]  // i32
        
        // Create a sequence of cast operations
        val temp1 = builder.insertSExt(arg0, IntegerType.I64, "temp1")  // temp1 = sext i8 to i64
        val temp2 = builder.insertZExt(arg1, IntegerType.I64, "temp2")  // temp2 = zext i32 to i64
        val temp3 = builder.insertAdd(temp1, temp2, "temp3")            // temp3 = temp1 + temp2
        val temp4 = builder.insertTrunc(temp3, IntegerType.I32, "temp4") // temp4 = trunc i64 to i32
        val result = builder.insertSExt(temp4, IntegerType.I64, "result") // result = sext i32 to i64
        
        // Return the result
        builder.insertRet(result)
    }
}