package space.norb.llvm.e2e.extracted.cast_operations

import space.norb.llvm.structure.Module
import space.norb.llvm.structure.Function
import space.norb.llvm.structure.BasicBlock
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.PointerType
import space.norb.llvm.builder.IRBuilder

/**
 * Extracted test case for bitcast operation.
 * Originally from CastOperationsTest.testBitcastFunction()
 */
object BitcastTest {
    
    /**
     * Builds the IR for this test case.
     *
     * @param module The module to add the IR to
     * @param builder The IR builder to use
     */
    fun buildIR(module: Module, builder: IRBuilder) {
        // Create function type: ptr (ptr)
        val functionType = FunctionType(
            returnType = PointerType,
            paramTypes = listOf(PointerType)
        )
        
        // Create the function
        val function = builder.createFunction("bitcast", functionType)
        
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
        
        // Get function argument
        val arg0 = function.parameters[0]
        
        // Create bitcast instruction (ptr to ptr)
        val result = builder.insertBitcast(arg0, PointerType, "result")
        
        // Return the result
        builder.insertRet(result)
    }
}