package space.norb.llvm.e2e

import space.norb.llvm.e2e.framework.IRTestFramework
import space.norb.llvm.structure.Module
import space.norb.llvm.structure.Function
import space.norb.llvm.structure.BasicBlock
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.IntegerType
import space.norb.llvm.builder.BuilderUtils
import kotlin.test.Test

/**
 * Template test class for new end-to-end tests.
 * Copy this file and modify it to create your own test cases.
 */
class TemplateTest {
    
    @Test
    fun testYourFeature() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testYourFeature",
            testClass = TemplateTest::class.java,
            resourceName = "TemplateTest_feature.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
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
        )
    }
}