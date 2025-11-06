package space.norb.llvm.e2e.extracted.aggregate_types

import space.norb.llvm.structure.Module
import space.norb.llvm.structure.Function
import space.norb.llvm.structure.BasicBlock
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.StructType
import space.norb.llvm.types.IntegerType
import space.norb.llvm.types.PointerType
import space.norb.llvm.types.FloatingPointType
import space.norb.llvm.builder.IRBuilder

/**
 * Extracted test case for simple struct.
 * Originally from AggregateTypesTest.testSimpleStruct()
 */
object SimpleStructTest {
    
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
        val function = builder.createFunction("simple_struct", functionType)
        
        // Add the function to the module
        module.functions.add(function)
        
        // Create entry block
        val entryBlock = builder.createBasicBlock("entry", function)
        
        // Add the basic block to the function
        function.basicBlocks.add(entryBlock)
        
        // Set the entry block if not already set
        if (function.entryBlock == null) {
            function.entryBlock = entryBlock
        }
        
        builder.positionAtEnd(entryBlock)
        
        // Create a struct type: { i32, i64, float }
        val structType = StructType(listOf(IntegerType.I32, IntegerType.I64, FloatingPointType.FloatType))
        
        // Allocate struct
        val structPtr = builder.buildAlloca(structType, "struct")
        
        // Return the struct pointer
        builder.buildRet(structPtr)
    }
}