package space.norb.llvm.e2e.extracted.named_struct_emission

import space.norb.llvm.structure.Module
import space.norb.llvm.types.IntegerType
import space.norb.llvm.types.FunctionType
import space.norb.llvm.values.globals.GlobalVariable
import space.norb.llvm.builder.IRBuilder

/**
 * Test case for named struct emission.
 * Tests that named struct definitions are emitted correctly in the IR
 * and appear before their usage in globals and functions.
 */
object NamedStructEmissionTest {
    
    /**
     * Builds the IR for this test case.
     *
     * @param module The module to add the IR to
     * @param builder The IR builder to use
     */
    fun buildIR(module: Module, builder: IRBuilder) {
        // Register a named struct type
        val pointStruct = module.registerNamedStructType("Point", listOf(IntegerType.I32, IntegerType.I32))
        
        // Register an opaque struct and complete it
        val opaqueStruct = module.registerOpaqueStructType("OpaquePoint")
        val completedOpaque = module.completeOpaqueStructType("OpaquePoint", listOf(IntegerType.I64, IntegerType.I64))
        
        // Create a global variable using the struct
        val globalPoint = GlobalVariable.createWithElementType("globalPoint", pointStruct, module, linkage = space.norb.llvm.enums.LinkageType.EXTERNAL)
        module.globalVariables.add(globalPoint)
        
        // Create a function that uses the struct types
        val functionType = FunctionType(pointStruct, listOf(pointStruct, opaqueStruct))
        val function = builder.createFunction("processPoints", functionType)
        module.functions.add(function)  // Add function to module
        
        // Create function body
        val entryBlock = builder.createBasicBlock("entry", function)
        function.basicBlocks.add(entryBlock)
        function.entryBlock = entryBlock
        builder.positionAtEnd(entryBlock)
        
        // Get function parameters
        val pointParam = function.parameters[0]
        val opaquePointParam = function.parameters[1]
        
        // Create some basic operations using the structs
        val alloca = builder.insertAlloca(pointStruct, "localPoint")
        builder.insertStore(pointParam, alloca)
        
        val loadedPoint = builder.insertLoad(pointStruct, alloca, "loadedPoint")
        builder.insertRet(loadedPoint)
    }
}