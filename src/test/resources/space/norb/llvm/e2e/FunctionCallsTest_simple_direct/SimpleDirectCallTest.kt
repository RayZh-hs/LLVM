package space.norb.llvm.e2e.extracted.function_calls

import space.norb.llvm.structure.Module
import space.norb.llvm.structure.Function
import space.norb.llvm.structure.BasicBlock
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.IntegerType
import space.norb.llvm.builder.BuilderUtils
import space.norb.llvm.builder.IRBuilder

/**
 * Extracted test case for simple direct function calls.
 * Originally from FunctionCallsTest.testSimpleDirectCall()
 */
object SimpleDirectCallTest {
    
    /**
     * Builds the IR for this test case.
     *
     * @param module The module to add the IR to
     * @param builder The IR builder to use
     */
    fun buildIR(module: Module, builder: IRBuilder) {
        // Create a helper function: i32 @add(i32 %a, i32 %b)
        val addFunctionType = FunctionType(
            returnType = IntegerType.I32,
            paramTypes = listOf(IntegerType.I32, IntegerType.I32),
            paramNames = listOf("a", "b")
        )
        val addFunction = builder.createFunction("add", addFunctionType)
        module.functions.add(addFunction)
        
        // Create basic block for add function
        val addBlock = addFunction.insertBasicBlock("entry")
        
        // Build add function
        builder.positionAtEnd(addBlock)
        val a = addFunction.parameters[0]
        val b = addFunction.parameters[1]
        val result = builder.insertAdd(a, b, "result")
        builder.insertRet(result)
        
        // Create main function: i32 @main()
        val mainFunctionType = FunctionType(
            returnType = IntegerType.I32,
            paramTypes = emptyList()
        )
        val mainFunction = builder.createFunction("main", mainFunctionType)
        module.functions.add(mainFunction)
        
        // Create basic block for main function
        val mainBlock = mainFunction.insertBasicBlock("entry")
        
        // Build main function that calls add
        builder.positionAtEnd(mainBlock)
        val const5 = BuilderUtils.getIntConstant(5, IntegerType.I32)
        val const3 = BuilderUtils.getIntConstant(3, IntegerType.I32)
        val callResult = builder.insertCall(addFunction, listOf(const5, const3), "call_result")
        builder.insertRet(callResult)
    }
}