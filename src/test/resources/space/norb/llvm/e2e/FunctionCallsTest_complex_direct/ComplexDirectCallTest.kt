package space.norb.llvm.e2e.extracted.function_calls

import space.norb.llvm.structure.Module
import space.norb.llvm.structure.Function
import space.norb.llvm.structure.BasicBlock
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.IntegerType
import space.norb.llvm.builder.BuilderUtils
import space.norb.llvm.builder.IRBuilder

/**
 * Extracted test case for complex direct function calls.
 * Originally from FunctionCallsTest.testComplexDirectCall()
 */
object ComplexDirectCallTest {
    
    /**
     * Builds the IR for this test case.
     *
     * @param module The module to add the IR to
     * @param builder The IR builder to use
     */
    fun buildIR(module: Module, builder: IRBuilder) {
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
}