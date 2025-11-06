package space.norb.llvm.e2e.extracted.function_calls

import space.norb.llvm.structure.Module
import space.norb.llvm.structure.Function
import space.norb.llvm.structure.BasicBlock
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.IntegerType
import space.norb.llvm.types.PointerType
import space.norb.llvm.builder.BuilderUtils
import space.norb.llvm.builder.IRBuilder

/**
 * Extracted test case for simple indirect function calls.
 * Originally from FunctionCallsTest.testSimpleIndirectCall()
 */
object SimpleIndirectCallTest {
    
    /**
     * Builds the IR for this test case.
     *
     * @param module The module to add the IR to
     * @param builder The IR builder to use
     */
    fun buildIR(module: Module, builder: IRBuilder) {
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
}