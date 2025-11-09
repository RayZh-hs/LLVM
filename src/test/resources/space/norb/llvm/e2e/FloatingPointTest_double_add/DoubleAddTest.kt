package space.norb.llvm.e2e.extracted.floating_point

import space.norb.llvm.structure.Module
import space.norb.llvm.structure.Function
import space.norb.llvm.structure.BasicBlock
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.FloatingPointType
import space.norb.llvm.values.constants.FloatConstant
import space.norb.llvm.builder.IRBuilder

/**
 * Extracted test case for double addition.
 * Originally from FloatingPointTest.testDoubleAdd()
 */
object DoubleAddTest {
    
    /**
     * Builds the IR for this test case.
     *
     * @param module The module to add the IR to
     * @param builder The IR builder to use
     */
    fun buildIR(module: Module, builder: IRBuilder) {
        // Create function type: double (double, double)
        val functionType = FunctionType(
            returnType = FloatingPointType.DoubleType,
            paramTypes = listOf(FloatingPointType.DoubleType, FloatingPointType.DoubleType)
        )
        
        // Create the function
        val function = builder.createFunction("double_add", functionType)
        module.functions.add(function)
        
        // Create entry block
        val entryBlock = function.insertBasicBlock("entry")
        
        if (function.entryBlock == null) {
            function.entryBlock = entryBlock
        }
        
        builder.positionAtEnd(entryBlock)
        
        // Get function arguments
        val a = function.parameters[0]
        val b = function.parameters[1]
        
        // Perform addition: a + b
        val result = builder.insertAdd(a, b, "result")
        
        // Return the result
        builder.insertRet(result)
    }
}