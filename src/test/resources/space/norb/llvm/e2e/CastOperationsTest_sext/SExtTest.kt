package space.norb.llvm.e2e.extracted.cast_operations

import space.norb.llvm.structure.Module
import space.norb.llvm.structure.Function
import space.norb.llvm.structure.BasicBlock
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.IntegerType
import space.norb.llvm.builder.IRBuilder

/**
 * Extracted test case for sext operation.
 * Originally from CastOperationsTest.testSExtFunction()
 */
object SExtTest {
    
    /**
     * Builds the IR for this test case.
     *
     * @param module The module to add the IR to
     * @param builder The IR builder to use
     */
    fun buildIR(module: Module, builder: IRBuilder) {
        // Create function type: i32 (i8)
        val functionType = FunctionType(
            returnType = IntegerType.I32,
            paramTypes = listOf(IntegerType.I8)
        )
        
        // Create the function
        val function = builder.createFunction("sext", functionType)
        
        // Add the function to the module
        module.functions.add(function)
        
        // Create entry block
        val entryBlock = function.insertBasicBlock("entry")
        
        // Set the entry block if not already set
        if (function.entryBlock == null) {
            function.entryBlock = entryBlock
        }
        
        builder.positionAtEnd(entryBlock)
        
        // Get function argument
        val arg0 = function.parameters[0]
        
        // Create sext instruction (i8 to i32)
        val result = builder.insertSExt(arg0, IntegerType.I32, "result")
        
        // Return the result
        builder.insertRet(result)
    }
}