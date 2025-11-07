package space.norb.llvm.e2e.extracted.advanced_control_flow

import space.norb.llvm.structure.Module
import space.norb.llvm.structure.Function
import space.norb.llvm.structure.BasicBlock
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.IntegerType
import space.norb.llvm.types.VoidType
import space.norb.llvm.values.constants.IntConstant
import space.norb.llvm.builder.IRBuilder
import space.norb.llvm.enums.IcmpPredicate

/**
 * Extracted test case for void function with branches.
 * Originally from AdvancedControlFlowTest.testVoidFunctionWithBranches()
 */
object VoidWithBranchesTest {
    
    /**
     * Builds the IR for this test case.
     *
     * @param module The module to add the IR to
     * @param builder The IR builder to use
     */
    fun buildIR(module: Module, builder: IRBuilder) {
        // Create function type: void (i32)
        val functionType = FunctionType(
            returnType = VoidType,
            paramTypes = listOf(IntegerType.I32)
        )
        
        // Create the function
        val function = builder.createFunction("void_with_branches", functionType)
        module.functions.add(function)
        
        // Create basic blocks
        val entryBlock = builder.createBasicBlock("entry", function)
        val block1 = builder.createBasicBlock("block1", function)
        val block2 = builder.createBasicBlock("block2", function)
        
        function.basicBlocks.addAll(listOf(entryBlock, block1, block2))
        
        if (function.entryBlock == null) {
            function.entryBlock = entryBlock
        }
        
        // Entry block - compare argument and branch
        builder.positionAtEnd(entryBlock)
        val arg0 = function.parameters[0]
        val zero = IntConstant(0, IntegerType.I32)
        val condition = builder.insertICmp(IcmpPredicate.EQ, arg0, zero, "condition")
        builder.insertCondBr(condition, block1, block2)
        
        // Block1 - void return
        builder.positionAtEnd(block1)
        builder.insertRetVoid()
        
        // Block2 - void return
        builder.positionAtEnd(block2)
        builder.insertRetVoid()
    }
}