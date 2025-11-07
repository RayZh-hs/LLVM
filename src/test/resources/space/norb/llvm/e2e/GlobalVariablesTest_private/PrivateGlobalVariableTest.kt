package space.norb.llvm.e2e.extracted.global_variables

import space.norb.llvm.structure.Module
import space.norb.llvm.structure.Function
import space.norb.llvm.structure.BasicBlock
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.IntegerType
import space.norb.llvm.values.globals.GlobalVariable
import space.norb.llvm.values.constants.IntConstant
import space.norb.llvm.enums.LinkageType
import space.norb.llvm.builder.IRBuilder

/**
 * Extracted test case for private global variables.
 * Originally from GlobalVariablesTest.testPrivateGlobalVariable()
 */
object PrivateGlobalVariableTest {
    
    /**
     * Builds the IR for this test case.
     *
     * @param module The module to add the IR to
     * @param builder The IR builder to use
     */
    fun buildIR(module: Module, builder: IRBuilder) {
        // Create private global variable
        val globalVar = GlobalVariable.create(
            name = "private_global",
            module = module,
            initializer = IntConstant(100, IntegerType.I32),
            linkage = LinkageType.PRIVATE
        )
        
        // Add global variable to the module
        module.globalVariables.add(globalVar)
        
        // Create function type: i32 ()
        val functionType = FunctionType(
            returnType = IntegerType.I32,
            paramTypes = emptyList()
        )
        
        // Create the function
        val function = builder.createFunction("get_private_global", functionType)
        module.functions.add(function)
        
        // Create entry block
        val entryBlock = builder.createBasicBlock("entry", function)
        function.basicBlocks.add(entryBlock)
        
        if (function.entryBlock == null) {
            function.entryBlock = entryBlock
        }
        
        builder.positionAtEnd(entryBlock)
        
        // Load the global variable
        val loadedValue = builder.insertLoad(IntegerType.I32, globalVar, "loaded_value")
        
        // Return the loaded value
        builder.insertRet(loadedValue)
    }
}