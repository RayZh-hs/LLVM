package space.norb.llvm.e2e.extracted.memory

import space.norb.llvm.structure.Module
import space.norb.llvm.structure.Function
import space.norb.llvm.structure.BasicBlock
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.IntegerType
import space.norb.llvm.types.PointerType
import space.norb.llvm.builder.IRBuilder
import space.norb.llvm.builder.BuilderUtils

/**
 * Extracted test case for getelementptr operations.
 * Originally from MemoryTest.testGetElementPtr()
 */
object GetElementPtrTest {
    
    /**
     * Builds the IR for this test case.
     *
     * @param module The module to add the IR to
     * @param builder The IR builder to use
     */
    fun buildIR(module: Module, builder: IRBuilder) {
        // Create function type: i32 (i32*)
        val functionType = FunctionType(
            returnType = IntegerType.I32,
            paramTypes = listOf(PointerType)
        )
        
        // Create the function
        val function = builder.createFunction("getelementptr_example", functionType)
        module.functions.add(function)
        
        // Create entry block
        val entryBlock = builder.createBasicBlock("entry", function)
        function.basicBlocks.add(entryBlock)
        
        if (function.entryBlock == null) {
            function.entryBlock = entryBlock
        }
        
        builder.positionAtEnd(entryBlock)
        
        // Get function argument (pointer to i32)
        val arg0 = function.parameters[0]
        
        // Create indices for GEP (get element at index 2)
        val index = BuilderUtils.getIntConstant(2, IntegerType.I32)
        
        // Get pointer to element at index 2
        val gep = builder.insertGep(IntegerType.I32, arg0, listOf(index), "ptr_to_elem")
        
        // Load the value at that location
        val loaded = builder.insertLoad(IntegerType.I32, gep, "loaded")
        
        // Return the loaded value
        builder.insertRet(loaded)
    }
}