package space.norb.llvm.e2e.extracted.named_struct_emission

import space.norb.llvm.structure.Module
import space.norb.llvm.types.IntegerType
import space.norb.llvm.types.FunctionType
import space.norb.llvm.values.constants.IntConstant
import space.norb.llvm.builder.IRBuilder

/**
 * Test case for multiple structs emission order.
 * Tests that multiple structs are emitted in alphabetical order.
 */
object MultipleStructsEmissionOrderTest {
    
    /**
     * Builds the IR for this test case.
     *
     * @param module The module to add the IR to
     * @param builder The IR builder to use
     */
    fun buildIR(module: Module, builder: IRBuilder) {
        // Register structs in non-alphabetical order
        module.registerNamedStructType("ZStruct", listOf(IntegerType.I64))
        module.registerNamedStructType("AStruct", listOf(IntegerType.I32))
        module.registerNamedStructType("MStruct", listOf(IntegerType.I16))
        
        // Get the registered struct types
        val zStruct = module.getNamedStructType("ZStruct")!!
        val aStruct = module.getNamedStructType("AStruct")!!
        val mStruct = module.getNamedStructType("MStruct")!!
        
        // Create a function that uses all struct types
        val functionType = FunctionType(IntegerType.I32, listOf(aStruct, mStruct, zStruct))
        val function = builder.createFunction("processAllStructs", functionType)
        module.functions.add(function)  // Add function to module
        
        // Create function body
        val entryBlock = function.insertBasicBlock("entry")
        builder.positionAtEnd(entryBlock)
        
        // Return a constant value
        val returnValue = IntConstant(42, IntegerType.I32)
        builder.insertRet(returnValue)
    }
}