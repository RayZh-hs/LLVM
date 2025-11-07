package space.norb.llvm.e2e.extracted.control_flow

import space.norb.llvm.structure.Module
import space.norb.llvm.structure.Function
import space.norb.llvm.structure.BasicBlock
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.IntegerType
import space.norb.llvm.builder.IRBuilder
import space.norb.llvm.builder.BuilderUtils
import space.norb.llvm.enums.IcmpPredicate

/**
 * Extracted test case for if-else control flow.
 * Originally from ControlFlowTest.testIfElseFunction()
 */
object IfElseTest {
    
    /**
     * Builds the IR for this test case.
     *
     * @param module The module to add the IR to
     * @param builder The IR builder to use
     */
    fun buildIR(module: Module, builder: IRBuilder) {
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
        val condition = builder.insertICmp(IcmpPredicate.NE, arg0, zero, "condition")
        builder.insertCondBr(condition, thenBlock, elseBlock)
        
        // Then block
        builder.positionAtEnd(thenBlock)
        val thenResult = builder.insertAdd(arg1, arg2, "then_result")
        builder.insertBr(mergeBlock)
        
        // Else block
        builder.positionAtEnd(elseBlock)
        val elseResult = builder.insertSub(arg1, arg2, "else_result")
        builder.insertBr(mergeBlock)
        
        // Merge block
        builder.positionAtEnd(mergeBlock)
        val result = builder.insertPhi(IntegerType.I32, listOf(
            Pair(thenResult, thenBlock),
            Pair(elseResult, elseBlock)
        ), "result")
        builder.insertRet(result)
    }
}