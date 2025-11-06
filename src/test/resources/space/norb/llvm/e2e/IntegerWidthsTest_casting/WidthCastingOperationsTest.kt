package space.norb.llvm.e2e.extracted.integer_widths

import space.norb.llvm.structure.Module
import space.norb.llvm.structure.Function
import space.norb.llvm.structure.BasicBlock
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.IntegerType
import space.norb.llvm.values.constants.IntConstant
import space.norb.llvm.builder.IRBuilder

/**
 * Extracted test case for width casting operations.
 * Originally from IntegerWidthsTest.testWidthCastingOperations()
 */
object WidthCastingOperationsTest {
    
    /**
     * Builds the IR for this test case.
     *
     * @param module The module to add the IR to
     * @param builder The IR builder to use
     */
    fun buildIR(module: Module, builder: IRBuilder) {
        // Create function type: i128 (i64)
        val functionType = FunctionType(
            returnType = IntegerType.I128,
            paramTypes = listOf(IntegerType.I64)
        )
        
        // Create the function
        val function = builder.createFunction("width_casting_ops", functionType)
        module.functions.add(function)
        
        // Create entry block
        val entryBlock = builder.createBasicBlock("entry", function)
        function.basicBlocks.add(entryBlock)
        
        if (function.entryBlock == null) {
            function.entryBlock = entryBlock
        }
        
        builder.positionAtEnd(entryBlock)
        
        // Get function argument
        val arg0 = function.parameters[0]  // i64
        
        // Create operations with width casting
        val temp1 = builder.buildZExt(arg0, IntegerType.I128, "temp1")  // temp1 = zext i64 to i128
        val temp2 = builder.buildMul(temp1, IntConstant(2L, IntegerType.I128), "temp2")  // temp2 = temp1 * 2
        val temp3 = builder.buildTrunc(temp2, IntegerType.I64, "temp3")  // temp3 = trunc i128 to i64
        val temp4 = builder.buildSExt(temp3, IntegerType.I128, "temp4")  // temp4 = sext i64 to i128
        val result = builder.buildAdd(temp4, IntConstant(1000000L, IntegerType.I128), "result")  // result = temp4 + 1000000
        
        // Return the result
        builder.buildRet(result)
    }
}