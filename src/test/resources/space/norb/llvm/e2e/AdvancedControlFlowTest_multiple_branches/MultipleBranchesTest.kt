package space.norb.llvm.e2e.extracted.advanced_control_flow

import space.norb.llvm.structure.Module
import space.norb.llvm.structure.Function
import space.norb.llvm.structure.BasicBlock
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.IntegerType
import space.norb.llvm.values.constants.IntConstant
import space.norb.llvm.builder.IRBuilder

/**
 * Extracted test case for multiple unconditional branches.
 * Originally from AdvancedControlFlowTest.testMultipleUnconditionalBranches()
 */
object MultipleBranchesTest {
    
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
        val function = builder.createFunction("multiple_branches", functionType)
        module.functions.add(function)
        
        // Create basic blocks
        val entryBlock = builder.createBasicBlock("entry", function)
        val block1 = builder.createBasicBlock("block1", function)
        val block2 = builder.createBasicBlock("block2", function)
        val block3 = builder.createBasicBlock("block3", function)
        
        function.basicBlocks.addAll(listOf(entryBlock, block1, block2, block3))
        
        if (function.entryBlock == null) {
            function.entryBlock = entryBlock
        }
        
        // Entry block - branch to block1
        builder.positionAtEnd(entryBlock)
        builder.insertBr(block1)
        
        // Block1 - branch to block2
        builder.positionAtEnd(block1)
        builder.insertBr(block2)
        
        // Block2 - branch to block3
        builder.positionAtEnd(block2)
        builder.insertBr(block3)
        
        // Block3 - return a constant
        builder.positionAtEnd(block3)
        val result = IntConstant(123, IntegerType.I32)
        builder.insertRet(result)
    }
}