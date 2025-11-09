package space.norb.llvm.e2e.extracted.global_variables

import space.norb.llvm.structure.Module
import space.norb.llvm.structure.Function
import space.norb.llvm.structure.BasicBlock
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.PointerType
import space.norb.llvm.types.ArrayType
import space.norb.llvm.types.IntegerType
import space.norb.llvm.values.globals.GlobalVariable
import space.norb.llvm.enums.LinkageType
import space.norb.llvm.builder.IRBuilder

/**
 * Extracted test case for global arrays.
 * Originally from GlobalVariablesTest.testGlobalArray()
 */
object GlobalArrayTest {
    
    /**
     * Builds the IR for this test case.
     *
     * @param module The module to add the IR to
     * @param builder The IR builder to use
     */
    fun buildIR(module: Module, builder: IRBuilder) {
        // Create array type: [5 x i32]
        val arrayType = ArrayType(5, IntegerType.I32)
        
        // Create global array with zero initializer
        val globalArray = GlobalVariable.createWithElementType(
            name = "global_array",
            elementType = arrayType,
            module = module,
            initializer = null, // Will use zeroinitializer
            linkage = LinkageType.EXTERNAL
        )
        
        // Add global variable to the module
        module.globalVariables.add(globalArray)
        
        // Create function type: ptr ()
        val functionType = FunctionType(
            returnType = PointerType,
            paramTypes = emptyList()
        )
        
        // Create the function
        val function = builder.createFunction("get_global_array", functionType)
        module.functions.add(function)
        
        // Create entry block
        val entryBlock = function.insertBasicBlock("entry")
        
        if (function.entryBlock == null) {
            function.entryBlock = entryBlock
        }
        
        builder.positionAtEnd(entryBlock)
        
        // Return the global array pointer
        builder.insertRet(globalArray)
    }
}