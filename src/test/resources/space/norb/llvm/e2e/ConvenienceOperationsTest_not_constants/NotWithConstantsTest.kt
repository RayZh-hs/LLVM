package space.norb.llvm.e2e.extracted.convenience_operations

import space.norb.llvm.structure.Module
import space.norb.llvm.structure.Function
import space.norb.llvm.structure.BasicBlock
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.IntegerType
import space.norb.llvm.values.constants.IntConstant
import space.norb.llvm.builder.IRBuilder

/**
 * Extracted test case for NOT operation with constants.
 * Originally from ConvenienceOperationsTest.testNotWithConstants()
 */
object NotWithConstantsTest {
    
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
        val function = builder.createFunction("not_with_constants", functionType)
        module.functions.add(function)
        
        // Create entry block
        val entryBlock = builder.createBasicBlock("entry", function)
        function.basicBlocks.add(entryBlock)
        
        if (function.entryBlock == null) {
            function.entryBlock = entryBlock
        }
        
        builder.positionAtEnd(entryBlock)
        
        // Create constants
        val const1 = IntConstant(0x12345678, IntegerType.I32)
        val const2 = IntConstant(0x87654321, IntegerType.I32)
        
        // Create NOT operations with constants
        val not1 = builder.insertNot(const1, "not1")
        val not2 = builder.insertNot(const2, "not2")
        
        // Combine the results
        val result = builder.insertXor(not1, not2, "result")
        
        // Return the result
        builder.insertRet(result)
    }
}