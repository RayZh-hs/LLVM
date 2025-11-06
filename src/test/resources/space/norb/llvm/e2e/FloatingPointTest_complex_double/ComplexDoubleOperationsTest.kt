package space.norb.llvm.e2e.extracted.floating_point

import space.norb.llvm.structure.Module
import space.norb.llvm.structure.Function
import space.norb.llvm.structure.BasicBlock
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.FloatingPointType
import space.norb.llvm.values.constants.FloatConstant
import space.norb.llvm.builder.IRBuilder

/**
 * Extracted test case for complex double operations.
 * Originally from FloatingPointTest.testComplexDoubleOperations()
 */
object ComplexDoubleOperationsTest {
    
    /**
     * Builds the IR for this test case.
     *
     * @param module The module to add the IR to
     * @param builder The IR builder to use
     */
    fun buildIR(module: Module, builder: IRBuilder) {
        // Create function type: double (double, double, double)
        val functionType = FunctionType(
            returnType = FloatingPointType.DoubleType,
            paramTypes = listOf(FloatingPointType.DoubleType, FloatingPointType.DoubleType, FloatingPointType.DoubleType)
        )
        
        // Create the function
        val function = builder.createFunction("complex_double_ops", functionType)
        module.functions.add(function)
        
        // Create entry block
        val entryBlock = builder.createBasicBlock("entry", function)
        function.basicBlocks.add(entryBlock)
        
        if (function.entryBlock == null) {
            function.entryBlock = entryBlock
        }
        
        builder.positionAtEnd(entryBlock)
        
        // Get function arguments
        val arg0 = function.parameters[0]
        val arg1 = function.parameters[1]
        val arg2 = function.parameters[2]
        
        // Create complex double operations
        val temp1 = builder.buildMul(arg0, arg1, "temp1")  // temp1 = a * b
        val temp2 = builder.buildAdd(temp1, arg2, "temp2")  // temp2 = temp1 + c
        val const1 = FloatConstant(0.5, FloatingPointType.DoubleType)
        val result = builder.buildMul(temp2, const1, "result")  // result = temp2 * 0.5
        
        // Return the result
        builder.buildRet(result)
    }
}