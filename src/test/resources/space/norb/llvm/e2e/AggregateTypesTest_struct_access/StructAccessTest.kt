package space.norb.llvm.e2e.extracted.aggregate_types

import space.norb.llvm.structure.Module
import space.norb.llvm.structure.Function
import space.norb.llvm.structure.BasicBlock
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.StructType
import space.norb.llvm.types.IntegerType
import space.norb.llvm.types.PointerType
import space.norb.llvm.types.FloatingPointType
import space.norb.llvm.values.constants.IntConstant
import space.norb.llvm.builder.IRBuilder

/**
 * Extracted test case for struct access.
 * Originally from AggregateTypesTest.testStructAccess()
 */
object StructAccessTest {
    
    /**
     * Builds the IR for this test case.
     *
     * @param module The module to add the IR to
     * @param builder The IR builder to use
     */
    fun buildIR(module: Module, builder: IRBuilder) {
        // Create function type: i64 (ptr)
        val functionType = FunctionType(
            returnType = IntegerType.I64,
            paramTypes = listOf(PointerType)
        )
        
        // Create the function
        val function = builder.createFunction("struct_access", functionType)
        
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
        
        // Get function arguments
        val structPtr = function.parameters[0]
        
        // Create a struct type: { i32, i64, float }
        val structType = StructType(listOf(IntegerType.I32, IntegerType.I64, FloatingPointType.FloatType))
        
        // Get element pointer for the second field (index 1): gep { i32, i64, float }, ptr, 0, 1
        val gepIndices = listOf(IntConstant(0, IntegerType.I32), IntConstant(1, IntegerType.I32))
        val fieldPtr = builder.insertGep(structType, structPtr, gepIndices, "field_ptr")
        
        // Load the field
        val field = builder.insertLoad(IntegerType.I64, fieldPtr, "field")
        
        // Return the field
        builder.insertRet(field)
    }
}