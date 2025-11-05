package space.norb.llvm.e2e

import space.norb.llvm.e2e.framework.IRTestFramework
import space.norb.llvm.structure.Module
import space.norb.llvm.structure.Function
import space.norb.llvm.structure.BasicBlock
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.IntegerType
import space.norb.llvm.types.VoidType
import space.norb.llvm.values.constants.IntConstant
import kotlin.test.Test

/**
 * End-to-end tests for advanced control flow constructs using the IRTestFramework.
 * Tests include unconditional branches and void returns.
 */
class AdvancedControlFlowTest {
    
    @Test
    fun testUnconditionalBranch() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testUnconditionalBranch",
            testClass = AdvancedControlFlowTest::class.java,
            resourceName = "AdvancedControlFlowTest_unconditional_branch.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
                // Create function type: i32 ()
                val functionType = FunctionType(
                    returnType = IntegerType.I32,
                    paramTypes = emptyList()
                )
                
                // Create the function
                val function = builder.createFunction("unconditional_branch", functionType)
                module.functions.add(function)
                
                // Create basic blocks
                val entryBlock = builder.createBasicBlock("entry", function)
                val targetBlock = builder.createBasicBlock("target", function)
                
                function.basicBlocks.addAll(listOf(entryBlock, targetBlock))
                
                if (function.entryBlock == null) {
                    function.entryBlock = entryBlock
                }
                
                // Entry block - create unconditional branch
                builder.positionAtEnd(entryBlock)
                builder.buildBr(targetBlock)
                
                // Target block - return a constant
                builder.positionAtEnd(targetBlock)
                val result = IntConstant(42, IntegerType.I32)
                builder.buildRet(result)
            }
        )
    }
    
    @Test
    fun testVoidReturn() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testVoidReturn",
            testClass = AdvancedControlFlowTest::class.java,
            resourceName = "AdvancedControlFlowTest_void_return.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
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
                builder.buildRetVoid()
            }
        )
    }
    
    @Test
    fun testMultipleUnconditionalBranches() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testMultipleUnconditionalBranches",
            testClass = AdvancedControlFlowTest::class.java,
            resourceName = "AdvancedControlFlowTest_multiple_branches.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
                // Create function type: i32 ()
                val functionType = FunctionType(
                    returnType = IntegerType.I32,
                    paramTypes = emptyList()
                )
                
                // Create the function
                val function = builder.createFunction("multiple_branches", functionType)
                module.functions.add(function)
                
                // Create basic blocks
                val entryBlock = builder.createBasicBlock("entry", function)
                val block1 = builder.createBasicBlock("block1", function)
                val block2 = builder.createBasicBlock("block2", function)
                val block3 = builder.createBasicBlock("block3", function)
                
                function.basicBlocks.addAll(listOf(entryBlock, block1, block2, block3))
                
                if (function.entryBlock == null) {
                    function.entryBlock = entryBlock
                }
                
                // Entry block - branch to block1
                builder.positionAtEnd(entryBlock)
                builder.buildBr(block1)
                
                // Block1 - branch to block2
                builder.positionAtEnd(block1)
                builder.buildBr(block2)
                
                // Block2 - branch to block3
                builder.positionAtEnd(block2)
                builder.buildBr(block3)
                
                // Block3 - return a constant
                builder.positionAtEnd(block3)
                val result = IntConstant(123, IntegerType.I32)
                builder.buildRet(result)
            }
        )
    }
    
    @Test
    fun testVoidFunctionWithBranches() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testVoidFunctionWithBranches",
            testClass = AdvancedControlFlowTest::class.java,
            resourceName = "AdvancedControlFlowTest_void_with_branches.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
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
                val condition = builder.buildICmp(space.norb.llvm.enums.IcmpPredicate.EQ, arg0, zero, "condition")
                builder.buildCondBr(condition, block1, block2)
                
                // Block1 - void return
                builder.positionAtEnd(block1)
                builder.buildRetVoid()
                
                // Block2 - void return
                builder.positionAtEnd(block2)
                builder.buildRetVoid()
            }
        )
    }
    
    @Test
    fun testComplexControlFlow() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testComplexControlFlow",
            testClass = AdvancedControlFlowTest::class.java,
            resourceName = "AdvancedControlFlowTest_complex.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
                // Create function type: i32 (i32, i32)
                val functionType = FunctionType(
                    returnType = IntegerType.I32,
                    paramTypes = listOf(IntegerType.I32, IntegerType.I32)
                )
                
                // Create the function
                val function = builder.createFunction("complex_control_flow", functionType)
                module.functions.add(function)
                
                // Create basic blocks
                val entryBlock = builder.createBasicBlock("entry", function)
                val computeBlock = builder.createBasicBlock("compute", function)
                val cleanupBlock = builder.createBasicBlock("cleanup", function)
                val exitBlock = builder.createBasicBlock("exit", function)
                
                function.basicBlocks.addAll(listOf(entryBlock, computeBlock, cleanupBlock, exitBlock))
                
                if (function.entryBlock == null) {
                    function.entryBlock = entryBlock
                }
                
                // Entry block - unconditional branch to compute
                builder.positionAtEnd(entryBlock)
                builder.buildBr(computeBlock)
                
                // Compute block - perform computation and branch to cleanup
                builder.positionAtEnd(computeBlock)
                val arg0 = function.parameters[0]
                val arg1 = function.parameters[1]
                val temp = builder.buildAdd(arg0, arg1, "temp")
                builder.buildBr(cleanupBlock)
                
                // Cleanup block - perform cleanup and branch to exit
                builder.positionAtEnd(cleanupBlock)
                val result = builder.buildMul(temp, IntConstant(2, IntegerType.I32), "result")
                builder.buildBr(exitBlock)
                
                // Exit block - return the result
                builder.positionAtEnd(exitBlock)
                builder.buildRet(result)
            }
        )
    }
}