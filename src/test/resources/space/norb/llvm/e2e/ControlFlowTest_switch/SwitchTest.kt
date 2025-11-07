package space.norb.llvm.e2e.extracted.control_flow

import space.norb.llvm.structure.Module
import space.norb.llvm.structure.Function
import space.norb.llvm.structure.BasicBlock
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.IntegerType
import space.norb.llvm.builder.IRBuilder
import space.norb.llvm.builder.BuilderUtils

/**
 * Extracted test case for switch control flow.
 * Originally from ControlFlowTest.testSwitchFunction()
 */
object SwitchTest {
    
    /**
     * Builds the IR for this test case.
     *
     * @param module The module to add the IR to
     * @param builder The IR builder to use
     */
    fun buildIR(module: Module, builder: IRBuilder) {
        // Create function type: i32 (i32)
        val functionType = FunctionType(
            returnType = IntegerType.I32,
            paramTypes = listOf(IntegerType.I32)
        )
        
        // Create the function
        val function = builder.createFunction("switch_example", functionType)
        module.functions.add(function)
        
        // Create basic blocks
        val entryBlock = builder.createBasicBlock("entry", function)
        val case1Block = builder.createBasicBlock("case1", function)
        val case2Block = builder.createBasicBlock("case2", function)
        val defaultBlock = builder.createBasicBlock("default", function)
        val mergeBlock = builder.createBasicBlock("merge", function)
        
        function.basicBlocks.addAll(listOf(entryBlock, case1Block, case2Block, defaultBlock, mergeBlock))
        
        if (function.entryBlock == null) {
            function.entryBlock = entryBlock
        }
        
        // Entry block
        builder.positionAtEnd(entryBlock)
        val arg0 = function.parameters[0]
        
        // Create switch instruction
        builder.insertSwitch(arg0, defaultBlock, listOf(
            Pair(BuilderUtils.getIntConstant(1, IntegerType.I32), case1Block),
            Pair(BuilderUtils.getIntConstant(2, IntegerType.I32), case2Block)
        ))
        
        // Case 1 block
        builder.positionAtEnd(case1Block)
        val case1Result = BuilderUtils.getIntConstant(10, IntegerType.I32)
        builder.insertBr(mergeBlock)
        
        // Case 2 block
        builder.positionAtEnd(case2Block)
        val case2Result = BuilderUtils.getIntConstant(20, IntegerType.I32)
        builder.insertBr(mergeBlock)
        
        // Default block
        builder.positionAtEnd(defaultBlock)
        val defaultResult = BuilderUtils.getIntConstant(0, IntegerType.I32)
        builder.insertBr(mergeBlock)
        
        // Merge block
        builder.positionAtEnd(mergeBlock)
        val result = builder.insertPhi(IntegerType.I32, listOf(
            Pair(case1Result, case1Block),
            Pair(case2Result, case2Block),
            Pair(defaultResult, defaultBlock)
        ), "result")
        builder.insertRet(result)
    }
}