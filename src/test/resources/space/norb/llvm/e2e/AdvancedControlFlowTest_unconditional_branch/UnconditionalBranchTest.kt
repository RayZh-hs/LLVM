package space.norb.llvm.e2e.extracted.advanced_control_flow

import space.norb.llvm.structure.Module
import space.norb.llvm.structure.Function
import space.norb.llvm.structure.BasicBlock
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.IntegerType
import space.norb.llvm.values.constants.IntConstant
import space.norb.llvm.builder.IRBuilder

/**
 * Extracted test case for unconditional branch.
 * Originally from AdvancedControlFlowTest.testUnconditionalBranch()
 */
object UnconditionalBranchTest {
    
    /**
     * Builds the IR for this test case.
     *
     * @param module The module to add the IR to
     * @param builder The IR builder to use
     */
    fun buildIR(module: Module, builder: IRBuilder) {
        // Create function type: i32 ()
        val functionType = FunctionType(
            returnType = IntegerType.I32,
            paramTypes = emptyList()
        )
        
        // Create the function
        val function = builder.createFunction("unconditional_branch", functionType)
        module.functions.add(function)
        
        // Create basic blocks
        val entryBlock = builder.createBasicBlock("entry", function)
        val targetBlock = builder.createBasicBlock("target", function)
        
        function.basicBlocks.addAll(listOf(entryBlock, targetBlock))
        
        if (function.entryBlock == null) {
            function.entryBlock = entryBlock
        }
        
        // Entry block - create unconditional branch
        builder.positionAtEnd(entryBlock)
        builder.insertBr(targetBlock)
        
        // Target block - return a constant
        builder.positionAtEnd(targetBlock)
        val result = IntConstant(42, IntegerType.I32)
        builder.insertRet(result)
    }
}