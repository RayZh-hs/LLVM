package space.norb.llvm.e2e

import space.norb.llvm.e2e.framework.IRTestFramework
import space.norb.llvm.structure.Module
import space.norb.llvm.structure.Function
import space.norb.llvm.structure.BasicBlock
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.IntegerType
import kotlin.test.Test

/**
 * End-to-end tests for simple arithmetic operations using the IRTestFramework.
 */
class SimpleArithmeticTest {
    
    @Test
    fun testAddFunction() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testAddFunction",
            testClass = SimpleArithmeticTest::class.java,
            resourceName = "SimpleArithmeticTest_add.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
                // Create function type: i32 (i32, i32)
                val functionType = FunctionType(
                    returnType = IntegerType.I32,
                    paramTypes = listOf(IntegerType.I32, IntegerType.I32)
                )
                
                // Create the function
                val function = builder.createFunction("add", functionType)
                
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
                
                // Get function arguments
                val arg0 = function.parameters[0]
                val arg1 = function.parameters[1]
                
                // Create add instruction
                val result = builder.buildAdd(arg0, arg1, "result")
                
                // Return the result
                builder.buildRet(result)
            }
        )
    }
}