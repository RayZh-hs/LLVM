package space.norb.llvm.e2e.extracted.aggregate_types

import space.norb.llvm.structure.Module
import space.norb.llvm.structure.Function
import space.norb.llvm.structure.BasicBlock
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.ArrayType
import space.norb.llvm.types.IntegerType
import space.norb.llvm.types.PointerType
import space.norb.llvm.builder.IRBuilder

/**
 * Extracted test case for simple array.
 * Originally from AggregateTypesTest.testSimpleArray()
 */
object SimpleArrayTest {
    
    /**
     * Builds the IR for this test case.
     *
     * @param module The module to add the IR to
     * @param builder The IR builder to use
     */
    fun buildIR(module: Module, builder: IRBuilder) {
        // Create function type: ptr ()
        val functionType = FunctionType(
            returnType = PointerType,
            paramTypes = emptyList()
        )
        
        // Create the function
        val function = builder.createFunction("simple_array", functionType)
        
        // Add the function to the module
        module.functions.add(function)
        
        // Create entry block
        val entryBlock = function.insertBasicBlock("entry")
        
        // Set the entry block if not already set
        if (function.entryBlock == null) {
            function.entryBlock = entryBlock
        }
        
        builder.positionAtEnd(entryBlock)
        
        // Create an array type: [10 x i32]
        val arrayType = ArrayType(10, IntegerType.I32)
        
        // Allocate array
        val arrayPtr = builder.insertAlloca(arrayType, "array")
        
        // Return the array pointer
        builder.insertRet(arrayPtr)
    }
}