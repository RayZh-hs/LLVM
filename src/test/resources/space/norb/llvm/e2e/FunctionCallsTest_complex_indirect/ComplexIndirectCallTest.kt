package space.norb.llvm.e2e.extracted.function_calls

import space.norb.llvm.structure.Module
import space.norb.llvm.structure.Function
import space.norb.llvm.structure.BasicBlock
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.IntegerType
import space.norb.llvm.types.PointerType
import space.norb.llvm.builder.BuilderUtils
import space.norb.llvm.builder.IRBuilder
import space.norb.llvm.enums.IcmpPredicate

/**
 * Extracted test case for complex indirect function calls.
 * Originally from FunctionCallsTest.testComplexIndirectCall()
 */
object ComplexIndirectCallTest {
    
    /**
     * Builds the IR for this test case.
     *
     * @param module The module to add the IR to
     * @param builder The IR builder to use
     */
    fun buildIR(module: Module, builder: IRBuilder) {
        // Create helper function 1: i32 @add(i32 %a, i32 %b)
        val addFunctionType = FunctionType(
            returnType = IntegerType.I32,
            paramTypes = listOf(IntegerType.I32, IntegerType.I32),
            paramNames = listOf("a", "b")
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
        val addResult = builder.insertAdd(addA, addB, "result")
        builder.insertRet(addResult)
        
        // Create helper function 2: i32 @multiply(i32 %a, i32 %b)
        val multiplyFunctionType = FunctionType(
            returnType = IntegerType.I32,
            paramTypes = listOf(IntegerType.I32, IntegerType.I32),
            paramNames = listOf("a", "b")
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
        val mulResult = builder.insertMul(mulA, mulB, "result")
        builder.insertRet(mulResult)
        
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
        val condition = builder.insertICmp(IcmpPredicate.EQ, const1, zero, "condition")
        builder.insertCondBr(condition, mulBlock, useAddBlock)
        
        // Add block
        builder.positionAtEnd(useAddBlock)
        val addFuncPtr = builder.insertBitcast(addFunction, PointerType, "func_ptr")
        val const5 = BuilderUtils.getIntConstant(5, IntegerType.I32)
        val const3 = BuilderUtils.getIntConstant(3, IntegerType.I32)
        val addCallResult = builder.insertIndirectCall(addFuncPtr, listOf(const5, const3), IntegerType.I32, "call_result")
        builder.insertBr(mergeBlock)
        
        // Multiply block
        builder.positionAtEnd(mulBlock)
        val mulFuncPtr = builder.insertBitcast(multiplyFunction, PointerType, "func_ptr1")
        val const4 = BuilderUtils.getIntConstant(4, IntegerType.I32)
        val const6 = BuilderUtils.getIntConstant(6, IntegerType.I32)
        val mulCallResult = builder.insertIndirectCall(mulFuncPtr, listOf(const4, const6), IntegerType.I32, "call_result2")
        builder.insertBr(mergeBlock)
        
        // Merge block
        builder.positionAtEnd(mergeBlock)
        val finalResult = builder.insertPhi(IntegerType.I32, listOf(
            Pair(addCallResult, useAddBlock),
            Pair(mulCallResult, mulBlock)
        ), "result")
        builder.insertRet(finalResult)
    }
}