package space.norb.llvm.e2e.extracted.comparison_predicates

import space.norb.llvm.structure.Module
import space.norb.llvm.structure.Function
import space.norb.llvm.structure.BasicBlock
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.IntegerType
import space.norb.llvm.builder.IRBuilder
import space.norb.llvm.enums.IcmpPredicate

/**
 * Extracted test case for complex comparison predicates.
 * Originally from ComparisonPredicatesTest.testComplexComparisonPredicates()
 */
object ComplexComparisonPredicatesTest {
    
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
            paramTypes = listOf(IntegerType.I32, IntegerType.I32, IntegerType.I32),
            paramNames = listOf("arg0", "arg1", "arg2")
        )
        
        // Create the function
        val function = builder.createFunction("complex_comparisons", functionType)
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
        val arg2 = function.parameters[2]
        
        // Create multiple comparisons
        val cmp1 = builder.insertICmp(IcmpPredicate.SGT, arg0, arg1, "cmp1")
        val cmp2 = builder.insertICmp(IcmpPredicate.ULT, arg1, arg2, "cmp2")
        
        // Combine comparisons with AND
        val condition = builder.insertAnd(cmp1, cmp2, "condition")
        
        // Create then block
        val thenBlock = function.insertBasicBlock("then")
        builder.positionAtEnd(thenBlock)
        val thenResult = builder.insertAdd(arg0, arg2, "then_result")
        
        // Create else block
        val elseBlock = function.insertBasicBlock("else")
        builder.positionAtEnd(elseBlock)
        val elseResult = builder.insertSub(arg0, arg2, "else_result")
        
        // Create merge block
        val mergeBlock = function.insertBasicBlock("merge")
        
        // Add branches to then and else blocks
        builder.positionAtEnd(thenBlock)
        builder.insertBr(mergeBlock) // Branch to merge
        
        builder.positionAtEnd(elseBlock)
        builder.insertBr(mergeBlock) // Branch to merge
        
        // Build merge block
        builder.positionAtEnd(mergeBlock)
        val result = builder.insertPhi(IntegerType.I32, listOf(Pair(thenResult, thenBlock), Pair(elseResult, elseBlock)), "result")
        builder.insertRet(result)
        
        // Go back to entry block and add conditional branch
        builder.positionAtEnd(entryBlock)
        builder.insertCondBr(condition, thenBlock, elseBlock)
    }
}