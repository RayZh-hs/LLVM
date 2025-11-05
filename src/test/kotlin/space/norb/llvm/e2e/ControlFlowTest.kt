package space.norb.llvm.e2e

import space.norb.llvm.e2e.framework.IRTestFramework
import space.norb.llvm.structure.Module
import space.norb.llvm.structure.Function
import space.norb.llvm.structure.BasicBlock
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.IntegerType
import space.norb.llvm.builder.BuilderUtils
import space.norb.llvm.enums.IcmpPredicate
import kotlin.test.Test

/**
 * End-to-end tests for control flow constructs using the IRTestFramework.
 */
class ControlFlowTest {
    
    @Test
    fun testIfElseFunction() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testIfElseFunction",
            testClass = ControlFlowTest::class.java,
            resourceName = "ControlFlowTest_if_else.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
                // Create function type: i32 (i32, i32, i32)
                val functionType = FunctionType(
                    returnType = IntegerType.I32,
                    paramTypes = listOf(IntegerType.I32, IntegerType.I32, IntegerType.I32)
                )
                
                // Create the function
                val function = builder.createFunction("if_else", functionType)
                module.functions.add(function)
                
                // Create basic blocks
                val entryBlock = builder.createBasicBlock("entry", function)
                val thenBlock = builder.createBasicBlock("then", function)
                val elseBlock = builder.createBasicBlock("else", function)
                val mergeBlock = builder.createBasicBlock("merge", function)
                
                function.basicBlocks.addAll(listOf(entryBlock, thenBlock, elseBlock, mergeBlock))
                
                if (function.entryBlock == null) {
                    function.entryBlock = entryBlock
                }
                
                // Entry block
                builder.positionAtEnd(entryBlock)
                val arg0 = function.parameters[0]
                val arg1 = function.parameters[1]
                val arg2 = function.parameters[2]
                
                // Compare arg0 with 0
                val zero = BuilderUtils.getIntConstant(0, IntegerType.I32)
                val condition = builder.buildICmp(IcmpPredicate.NE, arg0, zero, "condition")
                builder.buildCondBr(condition, thenBlock, elseBlock)
                
                // Then block
                builder.positionAtEnd(thenBlock)
                val thenResult = builder.buildAdd(arg1, arg2, "then_result")
                builder.buildBr(mergeBlock)
                
                // Else block
                builder.positionAtEnd(elseBlock)
                val elseResult = builder.buildSub(arg1, arg2, "else_result")
                builder.buildBr(mergeBlock)
                
                // Merge block
                builder.positionAtEnd(mergeBlock)
                val result = builder.buildPhi(IntegerType.I32, listOf(
                    Pair(thenResult, thenBlock),
                    Pair(elseResult, elseBlock)
                ), "result")
                builder.buildRet(result)
            }
        )
    }
    
    @Test
    fun testSwitchFunction() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testSwitchFunction",
            testClass = ControlFlowTest::class.java,
            resourceName = "ControlFlowTest_switch.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
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
                val switchInst = builder.buildSwitch(arg0, defaultBlock, listOf(
                    Pair(BuilderUtils.getIntConstant(1, IntegerType.I32), case1Block),
                    Pair(BuilderUtils.getIntConstant(2, IntegerType.I32), case2Block)
                ))
                
                // Case 1 block
                builder.positionAtEnd(case1Block)
                val case1Result = BuilderUtils.getIntConstant(10, IntegerType.I32)
                builder.buildBr(mergeBlock)
                
                // Case 2 block
                builder.positionAtEnd(case2Block)
                val case2Result = BuilderUtils.getIntConstant(20, IntegerType.I32)
                builder.buildBr(mergeBlock)
                
                // Default block
                builder.positionAtEnd(defaultBlock)
                val defaultResult = BuilderUtils.getIntConstant(0, IntegerType.I32)
                builder.buildBr(mergeBlock)
                
                // Merge block
                builder.positionAtEnd(mergeBlock)
                val result = builder.buildPhi(IntegerType.I32, listOf(
                    Pair(case1Result, case1Block),
                    Pair(case2Result, case2Block),
                    Pair(defaultResult, defaultBlock)
                ), "result")
                builder.buildRet(result)
            }
        )
    }
}