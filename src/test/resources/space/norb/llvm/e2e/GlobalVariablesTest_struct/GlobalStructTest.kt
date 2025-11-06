package space.norb.llvm.e2e.extracted.global_variables

import space.norb.llvm.structure.Module
import space.norb.llvm.structure.Function
import space.norb.llvm.structure.BasicBlock
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.PointerType
import space.norb.llvm.types.StructType
import space.norb.llvm.types.IntegerType
import space.norb.llvm.values.globals.GlobalVariable
import space.norb.llvm.enums.LinkageType
import space.norb.llvm.builder.IRBuilder

/**
 * Extracted test case for global structs.
 * Originally from GlobalVariablesTest.testGlobalStruct()
 */
object GlobalStructTest {
    
    /**
     * Builds the IR for this test case.
     *
     * @param module The module to add the IR to
     * @param builder The IR builder to use
     */
    fun buildIR(module: Module, builder: IRBuilder) {
        // Create struct type: { i32, i64 }
        val structType = StructType(listOf(IntegerType.I32, IntegerType.I64))
        
        // Create global struct
        val globalStruct = GlobalVariable.createWithElementType(
            name = "global_struct",
            elementType = structType,
            module = module,
            initializer = null, // Will use zeroinitializer
            linkage = LinkageType.PRIVATE
        )
        
        // Add global variable to the module
        module.globalVariables.add(globalStruct)
        
        // Create function type: ptr ()
        val functionType = FunctionType(
            returnType = PointerType,
            paramTypes = emptyList()
        )
        
        // Create the function
        val function = builder.createFunction("get_global_struct", functionType)
        module.functions.add(function)
        
        // Create entry block
        val entryBlock = builder.createBasicBlock("entry", function)
        function.basicBlocks.add(entryBlock)
        
        if (function.entryBlock == null) {
            function.entryBlock = entryBlock
        }
        
        builder.positionAtEnd(entryBlock)
        
        // Return the global struct pointer
        builder.buildRet(globalStruct)
    }
}