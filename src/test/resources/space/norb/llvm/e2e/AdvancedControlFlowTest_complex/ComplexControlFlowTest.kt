package space.norb.llvm.e2e.extracted.advanced_control_flow

import space.norb.llvm.structure.Module
import space.norb.llvm.structure.Function
import space.norb.llvm.structure.BasicBlock
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.IntegerType
import space.norb.llvm.values.constants.IntConstant
import space.norb.llvm.builder.IRBuilder

/**
 * Extracted test case for complex control flow.
 * Originally from AdvancedControlFlowTest.testComplexControlFlow()
 */
object ComplexControlFlowTest {
    
    /**
     * Builds the IR for this test case.
     *
     * @param module The module to add the IR to
     * @param builder The IR builder to use
     */
    fun buildIR(module: Module, builder: IRBuilder) {
        // Create function type: i32 (i32, i32)
        val functionType = FunctionType(
            returnType = IntegerType.I32,
            paramTypes = listOf(IntegerType.I32, IntegerType.I32)
        )
        
        // Create the function
        val function = builder.createFunction("complex_control_flow", functionType)
        module.functions.add(function)
        
        // Create basic blocks
        val entryBlock = function.insertBasicBlock("entry")
        val computeBlock = function.insertBasicBlock("compute")
        val cleanupBlock = function.insertBasicBlock("cleanup")
        val exitBlock = function.insertBasicBlock("exit")
        
        // Note: insertBasicBlock() automatically adds blocks to function.basicBlocks
        // and sets entryBlock if it's the first block
        
        // Entry block - unconditional branch to compute
        builder.positionAtEnd(entryBlock)
        builder.insertBr(computeBlock)
        
        // Compute block - perform computation and branch to cleanup
        builder.positionAtEnd(computeBlock)
        val arg0 = function.parameters[0]
        val arg1 = function.parameters[1]
        val temp = builder.insertAdd(arg0, arg1, "temp")
        builder.insertBr(cleanupBlock)
        
        // Cleanup block - perform cleanup and branch to exit
        builder.positionAtEnd(cleanupBlock)
        val result = builder.insertMul(temp, IntConstant(2, IntegerType.I32), "result")
        builder.insertBr(exitBlock)
        
        // Exit block - return the result
        builder.positionAtEnd(exitBlock)
        builder.insertRet(result)
    }
}