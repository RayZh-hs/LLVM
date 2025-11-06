package space.norb.llvm.e2e.extracted.floating_point

import space.norb.llvm.structure.Module
import space.norb.llvm.structure.Function
import space.norb.llvm.structure.BasicBlock
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.FloatingPointType
import space.norb.llvm.values.constants.FloatConstant
import space.norb.llvm.builder.IRBuilder

/**
 * Extracted test case for float subtraction.
 * Originally from FloatingPointTest.testFloatSub()
 */
object FloatSubTest {
    
    /**
     * Builds the IR for this test case.
     *
     * @param module The module to add the IR to
     * @param builder The IR builder to use
     */
    fun buildIR(module: Module, builder: IRBuilder) {
        // Create function type: float (float, float)
        val functionType = FunctionType(
            returnType = FloatingPointType.FloatType,
            paramTypes = listOf(FloatingPointType.FloatType, FloatingPointType.FloatType)
        )
        
        // Create the function
        val function = builder.createFunction("float_sub", functionType)
        module.functions.add(function)
        
        // Create entry block
        val entryBlock = builder.createBasicBlock("entry", function)
        function.basicBlocks.add(entryBlock)
        
        if (function.entryBlock == null) {
            function.entryBlock = entryBlock
        }
        
        builder.positionAtEnd(entryBlock)
        
        // Get function arguments
        val a = function.parameters[0]
        val b = function.parameters[1]
        
        // Perform subtraction: a - b
        val result = builder.buildSub(a, b, "result")
        
        // Return the result
        builder.buildRet(result)
    }
}