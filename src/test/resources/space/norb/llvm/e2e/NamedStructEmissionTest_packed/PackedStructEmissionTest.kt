package space.norb.llvm.e2e.extracted.named_struct_emission

import space.norb.llvm.structure.Module
import space.norb.llvm.types.IntegerType
import space.norb.llvm.types.FunctionType
import space.norb.llvm.builder.IRBuilder

/**
 * Test case for packed struct emission.
 * Tests that packed struct definitions are emitted with correct syntax.
 */
object PackedStructEmissionTest {
    
    /**
     * Builds the IR for this test case.
     *
     * @param module The module to add the IR to
     * @param builder The IR builder to use
     */
    fun buildIR(module: Module, builder: IRBuilder) {
        // Register a packed struct type
        val packedStruct = module.registerNamedStructType("PackedData", listOf(IntegerType.I8, IntegerType.I32, IntegerType.I8), true)
        
        // Create a function that uses the packed struct
        val functionType = FunctionType(packedStruct, listOf(packedStruct))
        val function = builder.createFunction("processPackedData", functionType)
        module.functions.add(function)  // Add function to module
        
        // Create function body
        val entryBlock = function.insertBasicBlock("entry")
        builder.positionAtEnd(entryBlock)
        
        // Get function parameter
        val packedParam = function.parameters[0]
        
        // Return the parameter directly
        builder.insertRet(packedParam)
    }
}