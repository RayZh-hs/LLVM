package space.norb.llvm.e2e.extracted.template

import space.norb.llvm.structure.Module
import space.norb.llvm.structure.Function
import space.norb.llvm.structure.BasicBlock
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.IntegerType
import space.norb.llvm.builder.BuilderUtils
import space.norb.llvm.builder.IRBuilder

/**
 * Extracted test case for template feature.
 * Originally from TemplateTest.testYourFeature()
 */
object TemplateFeatureTest {
    
    /**
     * Builds the IR for this test case.
     *
     * @param module The module to add the IR to
     * @param builder The IR builder to use
     */
    fun buildIR(module: Module, builder: IRBuilder) {
        // Create function type: adjust return type and parameters as needed
        val functionType = FunctionType(
            returnType = IntegerType.I32,
            paramTypes = listOf(IntegerType.I32, IntegerType.I32)
        )
        
        // Create the function
        val function = builder.createFunction("your_function", functionType)
        module.functions.add(function)
        
        // Create entry block
        val entryBlock = builder.createBasicBlock("entry", function)
        function.basicBlocks.add(entryBlock)
        
        if (function.entryBlock == null) {
            function.entryBlock = entryBlock
        }
        
        builder.positionAtEnd(entryBlock)
        
        // Get function arguments
        val arg0 = function.parameters[0]
        val arg1 = function.parameters[1]
        
        // Build your IR here
        // Example: add two numbers
        val result = builder.buildAdd(arg0, arg1, "result")
        
        // Return the result
        builder.buildRet(result)
    }
}