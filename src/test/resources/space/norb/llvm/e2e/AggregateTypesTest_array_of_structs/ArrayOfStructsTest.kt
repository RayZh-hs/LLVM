package space.norb.llvm.e2e.extracted.aggregate_types

import space.norb.llvm.structure.Module
import space.norb.llvm.structure.Function
import space.norb.llvm.structure.BasicBlock
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.ArrayType
import space.norb.llvm.types.StructType
import space.norb.llvm.types.IntegerType
import space.norb.llvm.types.PointerType
import space.norb.llvm.values.constants.IntConstant
import space.norb.llvm.builder.IRBuilder

/**
 * Extracted test case for array of structs.
 * Originally from AggregateTypesTest.testArrayOfStructs()
 */
object ArrayOfStructsTest {
    
    /**
     * Builds the IR for this test case.
     *
     * @param module The module to add the IR to
     * @param builder The IR builder to use
     */
    fun buildIR(module: Module, builder: IRBuilder) {
        // Create function type: i32 (ptr, i32)
        val functionType = FunctionType(
            returnType = IntegerType.I32,
            paramTypes = listOf(PointerType, IntegerType.I32)
        )
        
        // Create the function
        val function = builder.createFunction("array_of_structs", functionType)
        
        // Add the function to the module
        module.functions.add(function)
        
        // Create entry block
        val entryBlock = function.insertBasicBlock("entry")
        
        // Set the entry block if not already set
        if (function.entryBlock == null) {
            function.entryBlock = entryBlock
        }
        
        builder.positionAtEnd(entryBlock)
        
        // Get function arguments
        val arrayPtr = function.parameters[0]
        val index = function.parameters[1]
        
        // Create struct type: { i32, i64 }
        val structType = StructType(listOf(IntegerType.I32, IntegerType.I64))
        
        // Create array type: [5 x { i32, i64 }]
        val arrayType = ArrayType(5, structType)
        
        // Get element pointer for array element's first field: gep [5 x { i32, i64 }], ptr, 0, index, 0
        val gepIndices = listOf(
            IntConstant(0, IntegerType.I32), 
            index, 
            IntConstant(0, IntegerType.I32)
        )
        val fieldPtr = builder.insertGep(arrayType, arrayPtr, gepIndices, "field_ptr")
        
        // Load the field
        val field = builder.insertLoad(IntegerType.I32, fieldPtr, "field")
        
        // Return the field
        builder.insertRet(field)
    }
}