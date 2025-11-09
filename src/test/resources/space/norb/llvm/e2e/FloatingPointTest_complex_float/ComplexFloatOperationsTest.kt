package space.norb.llvm.e2e.extracted.floating_point

import space.norb.llvm.structure.Module
import space.norb.llvm.structure.Function
import space.norb.llvm.structure.BasicBlock
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.FloatingPointType
import space.norb.llvm.values.constants.FloatConstant
import space.norb.llvm.builder.IRBuilder

/**
 * Extracted test case for complex float operations.
 * Originally from FloatingPointTest.testComplexFloatOperations()
 */
object ComplexFloatOperationsTest {
    
    /**
     * Builds the IR for this test case.
     *
     * @param module The module to add the IR to
     * @param builder The IR builder to use
     */
    fun buildIR(module: Module, builder: IRBuilder) {
        // Create function type: float (float, float, float)
        val functionType = FunctionType(
            returnType = FloatingPointType.FloatType,
            paramTypes = listOf(FloatingPointType.FloatType, FloatingPointType.FloatType, FloatingPointType.FloatType)
        )
        
        // Create the function
        val function = builder.createFunction("complex_float_ops", functionType)
        module.functions.add(function)
        
        // Create entry block
        val entryBlock = function.insertBasicBlock("entry")
        
        if (function.entryBlock == null) {
            function.entryBlock = entryBlock
        }
        
        builder.positionAtEnd(entryBlock)
        
        // Get function arguments
        val arg0 = function.parameters[0]
        val arg1 = function.parameters[1]
        val arg2 = function.parameters[2]
        
        // Create complex float operations
        val temp1 = builder.insertMul(arg0, arg1, "temp1")  // temp1 = a * b
        val temp2 = builder.insertAdd(temp1, arg2, "temp2")  // temp2 = temp1 + c
        val const1 = FloatConstant(0.5, FloatingPointType.FloatType)
        val result = builder.insertMul(temp2, const1, "result")  // result = temp2 * 0.5
        
        // Return the result
        builder.insertRet(result)
    }
}