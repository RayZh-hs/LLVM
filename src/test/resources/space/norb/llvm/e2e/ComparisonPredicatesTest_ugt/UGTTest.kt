package space.norb.llvm.e2e.extracted.comparison_predicates

import space.norb.llvm.structure.Module
import space.norb.llvm.structure.Function
import space.norb.llvm.structure.BasicBlock
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.IntegerType
import space.norb.llvm.builder.IRBuilder
import space.norb.llvm.enums.IcmpPredicate

/**
 * Extracted test case for UGT comparison predicate.
 * Originally from ComparisonPredicatesTest.testUGTPredicate()
 */
object UGTTest {
    
    /**
     * Builds the IR for this test case.
     *
     * @param module The module to add the IR to
     * @param builder The IR builder to use
     */
    fun buildIR(module: Module, builder: IRBuilder) {
        // Create function type: i1 (i32, i32)
        val functionType = FunctionType(
            returnType = IntegerType.I1,
            paramTypes = listOf(IntegerType.I32, IntegerType.I32)
        )
        
        // Create the function
        val function = builder.createFunction("ugt_test", functionType)
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
        
        // Create UGT comparison
        val result = builder.insertICmp(IcmpPredicate.UGT, arg0, arg1, "result")
        
        // Return the result
        builder.insertRet(result)
    }
}