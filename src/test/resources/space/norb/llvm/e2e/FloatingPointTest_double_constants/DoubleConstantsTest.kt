package space.norb.llvm.e2e.extracted.floating_point

import space.norb.llvm.structure.Module
import space.norb.llvm.structure.Function
import space.norb.llvm.structure.BasicBlock
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.FloatingPointType
import space.norb.llvm.values.constants.FloatConstant
import space.norb.llvm.builder.IRBuilder

/**
 * Extracted test case for double constants.
 * Originally from FloatingPointTest.testDoubleConstants()
 */
object DoubleConstantsTest {
    
    /**
     * Builds the IR for this test case.
     *
     * @param module The module to add the IR to
     * @param builder The IR builder to use
     */
    fun buildIR(module: Module, builder: IRBuilder) {
        // Create function type: double ()
        val functionType = FunctionType(
            returnType = FloatingPointType.DoubleType,
            paramTypes = emptyList()
        )
        
        // Create the function
        val function = builder.createFunction("double_constants", functionType)
        module.functions.add(function)
        
        // Create entry block
        val entryBlock = builder.createBasicBlock("entry", function)
        function.basicBlocks.add(entryBlock)
        
        if (function.entryBlock == null) {
            function.entryBlock = entryBlock
        }
        
        builder.positionAtEnd(entryBlock)
        
        // Create double constants and operations
        val const1 = FloatConstant(3.14159265359, FloatingPointType.DoubleType)
        val const2 = FloatConstant(2.71828182846, FloatingPointType.DoubleType)
        val temp1 = builder.insertAdd(const1, const2, "temp1")
        val const3 = FloatConstant(1.0, FloatingPointType.DoubleType)
        val result = builder.insertMul(temp1, const3, "result")
        
        // Return the result
        builder.insertRet(result)
    }
}