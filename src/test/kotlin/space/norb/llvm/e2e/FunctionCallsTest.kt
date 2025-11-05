package space.norb.llvm.e2e

import space.norb.llvm.e2e.framework.IRTestFramework
import space.norb.llvm.structure.Module
import space.norb.llvm.structure.Function
import space.norb.llvm.structure.BasicBlock
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.IntegerType
import space.norb.llvm.types.PointerType
import space.norb.llvm.types.VoidType
import space.norb.llvm.builder.BuilderUtils
import kotlin.test.Test

/**
 * End-to-end tests for function calls using the IRTestFramework.
 */
class FunctionCallsTest {
    
    @Test
    fun testSimpleDirectCall() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testSimpleDirectCall",
            testClass = FunctionCallsTest::class.java,
            resourceName = "FunctionCallsTest_simple_direct.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
                // Create a helper function: i32 @add(i32 %a, i32 %b)
                val addFunctionType = FunctionType(
                    returnType = IntegerType.I32,
                    paramTypes = listOf(IntegerType.I32, IntegerType.I32)
                )
                val addFunction = builder.createFunction("add", addFunctionType)
                module.functions.add(addFunction)
                
                // Create basic block for add function
                val addBlock = builder.createBasicBlock("entry", addFunction)
                addFunction.basicBlocks.add(addBlock)
                addFunction.entryBlock = addBlock
                
                // Build add function
                builder.positionAtEnd(addBlock)
                val a = addFunction.parameters[0]
                val b = addFunction.parameters[1]
                val result = builder.buildAdd(a, b, "result")
                builder.buildRet(result)
                
                // Create main function: i32 @main()
                val mainFunctionType = FunctionType(
                    returnType = IntegerType.I32,
                    paramTypes = emptyList()
                )
                val mainFunction = builder.createFunction("main", mainFunctionType)
                module.functions.add(mainFunction)
                
                // Create basic block for main function
                val mainBlock = builder.createBasicBlock("entry", mainFunction)
                mainFunction.basicBlocks.add(mainBlock)
                mainFunction.entryBlock = mainBlock
                
                // Build main function that calls add
                builder.positionAtEnd(mainBlock)
                val const5 = BuilderUtils.getIntConstant(5, IntegerType.I32)
                val const3 = BuilderUtils.getIntConstant(3, IntegerType.I32)
                val callResult = builder.buildCall(addFunction, listOf(const5, const3), "call_result")
                builder.buildRet(callResult)
            }
        )
    }
    
    @Test
    fun testComplexDirectCall() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testComplexDirectCall",
            testClass = FunctionCallsTest::class.java,
            resourceName = "FunctionCallsTest_complex_direct.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
                // Create a helper function: i32 @multiply(i32 %a, i32 %b)
                val multiplyFunctionType = FunctionType(
                    returnType = IntegerType.I32,
                    paramTypes = listOf(IntegerType.I32, IntegerType.I32)
                )
                val multiplyFunction = builder.createFunction("multiply", multiplyFunctionType)
                module.functions.add(multiplyFunction)
                
                // Create basic block for multiply function
                val multiplyBlock = builder.createBasicBlock("entry", multiplyFunction)
                multiplyFunction.basicBlocks.add(multiplyBlock)
                multiplyFunction.entryBlock = multiplyBlock
                
                // Build multiply function
                builder.positionAtEnd(multiplyBlock)
                val a = multiplyFunction.parameters[0]
                val b = multiplyFunction.parameters[1]
                val result = builder.buildMul(a, b, "result")
                builder.buildRet(result)
                
                // Create another helper function: i32 @calculate(i32 %x, i32 %y, i32 %z)
                val calculateFunctionType = FunctionType(
                    returnType = IntegerType.I32,
                    paramTypes = listOf(IntegerType.I32, IntegerType.I32, IntegerType.I32)
                )
                val calculateFunction = builder.createFunction("calculate", calculateFunctionType)
                module.functions.add(calculateFunction)
                
                // Create basic block for calculate function
                val calculateBlock = builder.createBasicBlock("entry", calculateFunction)
                calculateFunction.basicBlocks.add(calculateBlock)
                calculateFunction.entryBlock = calculateBlock
                
                // Build calculate function that calls multiply
                builder.positionAtEnd(calculateBlock)
                val x = calculateFunction.parameters[0]
                val y = calculateFunction.parameters[1]
                val z = calculateFunction.parameters[2]
                
                // Call multiply(x, y)
                val mulResult = builder.buildCall(multiplyFunction, listOf(x, y), "mul_result")
                // Add z to the result
                val finalResult = builder.buildAdd(mulResult, z, "final_result")
                builder.buildRet(finalResult)
                
                // Create main function: i32 @main()
                val mainFunctionType = FunctionType(
                    returnType = IntegerType.I32,
                    paramTypes = emptyList()
                )
                val mainFunction = builder.createFunction("main", mainFunctionType)
                module.functions.add(mainFunction)
                
                // Create basic block for main function
                val mainBlock = builder.createBasicBlock("entry", mainFunction)
                mainFunction.basicBlocks.add(mainBlock)
                mainFunction.entryBlock = mainBlock
                
                // Build main function that calls calculate
                builder.positionAtEnd(mainBlock)
                val const2 = BuilderUtils.getIntConstant(2, IntegerType.I32)
                val const3 = BuilderUtils.getIntConstant(3, IntegerType.I32)
                val const4 = BuilderUtils.getIntConstant(4, IntegerType.I32)
                val callResult = builder.buildCall(calculateFunction, listOf(const2, const3, const4), "call_result")
                builder.buildRet(callResult)
            }
        )
    }
    
    @Test
    fun testSimpleIndirectCall() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testSimpleIndirectCall",
            testClass = FunctionCallsTest::class.java,
            resourceName = "FunctionCallsTest_simple_indirect.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
                // Create a helper function: i32 @subtract(i32 %a, i32 %b)
                val subtractFunctionType = FunctionType(
                    returnType = IntegerType.I32,
                    paramTypes = listOf(IntegerType.I32, IntegerType.I32)
                )
                val subtractFunction = builder.createFunction("subtract", subtractFunctionType)
                module.functions.add(subtractFunction)
                
                // Create basic block for subtract function
                val subtractBlock = builder.createBasicBlock("entry", subtractFunction)
                subtractFunction.basicBlocks.add(subtractBlock)
                subtractFunction.entryBlock = subtractBlock
                
                // Build subtract function
                builder.positionAtEnd(subtractBlock)
                val a = subtractFunction.parameters[0]
                val b = subtractFunction.parameters[1]
                val result = builder.buildSub(a, b, "result")
                builder.buildRet(result)
                
                // Create main function: i32 @main()
                val mainFunctionType = FunctionType(
                    returnType = IntegerType.I32,
                    paramTypes = emptyList()
                )
                val mainFunction = builder.createFunction("main", mainFunctionType)
                module.functions.add(mainFunction)
                
                // Create basic block for main function
                val mainBlock = builder.createBasicBlock("entry", mainFunction)
                mainFunction.basicBlocks.add(mainBlock)
                mainFunction.entryBlock = mainBlock
                
                // Build main function that uses indirect call
                builder.positionAtEnd(mainBlock)
                
                // Create a function pointer to subtract
                val funcPtr = builder.buildBitcast(subtractFunction, PointerType, "func_ptr")
                
                // Call through function pointer
                val const10 = BuilderUtils.getIntConstant(10, IntegerType.I32)
                val const3 = BuilderUtils.getIntConstant(3, IntegerType.I32)
                val callResult = builder.buildIndirectCall(funcPtr, listOf(const10, const3), IntegerType.I32, "call_result")
                builder.buildRet(callResult)
            }
        )
    }
    
    @Test
    fun testComplexIndirectCall() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testComplexIndirectCall",
            testClass = FunctionCallsTest::class.java,
            resourceName = "FunctionCallsTest_complex_indirect.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
                // Create helper function 1: i32 @add(i32 %a, i32 %b)
                val addFunctionType = FunctionType(
                    returnType = IntegerType.I32,
                    paramTypes = listOf(IntegerType.I32, IntegerType.I32)
                )
                val addFunction = builder.createFunction("add", addFunctionType)
                module.functions.add(addFunction)
                
                // Create basic block for add function
                val addEntryBlock = builder.createBasicBlock("entry", addFunction)
                addFunction.basicBlocks.add(addEntryBlock)
                addFunction.entryBlock = addEntryBlock
                
                // Build add function
                builder.positionAtEnd(addEntryBlock)
                val addA = addFunction.parameters[0]
                val addB = addFunction.parameters[1]
                val addResult = builder.buildAdd(addA, addB, "result")
                builder.buildRet(addResult)
                
                // Create helper function 2: i32 @multiply(i32 %a, i32 %b)
                val multiplyFunctionType = FunctionType(
                    returnType = IntegerType.I32,
                    paramTypes = listOf(IntegerType.I32, IntegerType.I32)
                )
                val multiplyFunction = builder.createFunction("multiply", multiplyFunctionType)
                module.functions.add(multiplyFunction)
                
                // Create basic block for multiply function
                val multiplyBlock = builder.createBasicBlock("entry", multiplyFunction)
                multiplyFunction.basicBlocks.add(multiplyBlock)
                multiplyFunction.entryBlock = multiplyBlock
                
                // Build multiply function
                builder.positionAtEnd(multiplyBlock)
                val mulA = multiplyFunction.parameters[0]
                val mulB = multiplyFunction.parameters[1]
                val mulResult = builder.buildMul(mulA, mulB, "result")
                builder.buildRet(mulResult)
                
                // Create main function: i32 @main()
                val mainFunctionType = FunctionType(
                    returnType = IntegerType.I32,
                    paramTypes = emptyList()
                )
                val mainFunction = builder.createFunction("main", mainFunctionType)
                module.functions.add(mainFunction)
                
                // Create basic blocks for main function
                val entryBlock = builder.createBasicBlock("entry", mainFunction)
                val useAddBlock = builder.createBasicBlock("use_add", mainFunction)
                val mulBlock = builder.createBasicBlock("use_mul", mainFunction)
                val mergeBlock = builder.createBasicBlock("merge", mainFunction)
                
                mainFunction.basicBlocks.addAll(listOf(entryBlock, useAddBlock, mulBlock, mergeBlock))
                mainFunction.entryBlock = entryBlock
                
                // Build main function
                builder.positionAtEnd(entryBlock)
                val const1 = BuilderUtils.getIntConstant(1, IntegerType.I32)
                val zero = BuilderUtils.getIntConstant(0, IntegerType.I32)
                val condition = builder.buildICmp(space.norb.llvm.enums.IcmpPredicate.EQ, const1, zero, "condition")
                builder.buildCondBr(condition, mulBlock, useAddBlock)
                
                // Add block
                builder.positionAtEnd(useAddBlock)
                val addFuncPtr = builder.buildBitcast(addFunction, PointerType, "func_ptr")
                val const5 = BuilderUtils.getIntConstant(5, IntegerType.I32)
                val const3 = BuilderUtils.getIntConstant(3, IntegerType.I32)
                val addCallResult = builder.buildIndirectCall(addFuncPtr, listOf(const5, const3), IntegerType.I32, "call_result")
                builder.buildBr(mergeBlock)
                
                // Multiply block
                builder.positionAtEnd(mulBlock)
                val mulFuncPtr = builder.buildBitcast(multiplyFunction, PointerType, "func_ptr")
                val const4 = BuilderUtils.getIntConstant(4, IntegerType.I32)
                val const6 = BuilderUtils.getIntConstant(6, IntegerType.I32)
                val mulCallResult = builder.buildIndirectCall(mulFuncPtr, listOf(const4, const6), IntegerType.I32, "call_result")
                builder.buildBr(mergeBlock)
                
                // Merge block
                builder.positionAtEnd(mergeBlock)
                val finalResult = builder.buildPhi(IntegerType.I32, listOf(
                    Pair(addCallResult, useAddBlock),
                    Pair(mulCallResult, mulBlock)
                ), "result")
                builder.buildRet(finalResult)
            }
        )
    }
}