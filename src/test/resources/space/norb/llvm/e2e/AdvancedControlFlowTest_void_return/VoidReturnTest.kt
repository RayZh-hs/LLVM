package space.norb.llvm.e2e.extracted.advanced_control_flow

import space.norb.llvm.structure.Module
import space.norb.llvm.structure.Function
import space.norb.llvm.structure.BasicBlock
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.VoidType
import space.norb.llvm.builder.IRBuilder

/**
 * Extracted test case for void return.
 * Originally from AdvancedControlFlowTest.testVoidReturn()
 */
object VoidReturnTest {
    
    /**
     * Builds the IR for this test case.
     *
     * @param module The module to add the IR to
     * @param builder The IR builder to use
     */
    fun buildIR(module: Module, builder: IRBuilder) {
        // Create function type: void ()
        val functionType = FunctionType(
            returnType = VoidType,
            paramTypes = emptyList()
        )
        
        // Create the function
        val function = builder.createFunction("void_return", functionType)
        module.functions.add(function)
        
        // Create entry block
        val entryBlock = builder.createBasicBlock("entry", function)
        function.basicBlocks.add(entryBlock)
        
        if (function.entryBlock == null) {
            function.entryBlock = entryBlock
        }
        
        // Entry block - create void return
        builder.positionAtEnd(entryBlock)
        builder.insertRetVoid()
    }
}