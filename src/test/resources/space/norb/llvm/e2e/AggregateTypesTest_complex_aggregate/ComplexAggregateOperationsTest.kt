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
 * Extracted test case for complex aggregate operations.
 * Originally from AggregateTypesTest.testComplexAggregateOperations()
 */
object ComplexAggregateOperationsTest {
    
    /**
     * Builds the IR for this test case.
     *
     * @param module The module to add the IR to
     * @param builder The IR builder to use
     */
    fun buildIR(module: Module, builder: IRBuilder) {
        // Create function type: i64 (ptr, i32, i32)
        val functionType = FunctionType(
            returnType = IntegerType.I64,
            paramTypes = listOf(PointerType, IntegerType.I32, IntegerType.I32)
        )
        
        // Create the function
        val function = builder.createFunction("complex_aggregate_ops", functionType)
        
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
        val arrayPtr = function.parameters[0]
        val outerIndex = function.parameters[1]
        val innerIndex = function.parameters[2]
        
        // Create inner struct type: { i32, i64 }
        val innerStructType = StructType(listOf(IntegerType.I32, IntegerType.I64))
        
        // Create array type: [10 x { i32, i64 }]
        val arrayType = ArrayType(10, innerStructType)
        
        // Get element pointer for array element's second field: gep [10 x { i32, i64 }], ptr, 0, outerIndex, innerIndex
        val gepIndices = listOf(
            IntConstant(0, IntegerType.I32), 
            outerIndex, 
            innerIndex
        )
        val fieldPtr = builder.buildGep(arrayType, arrayPtr, gepIndices, "field_ptr")
        
        // Load the field
        val field = builder.buildLoad(IntegerType.I64, fieldPtr, "field")
        
        // Add a constant to the field
        val result = builder.buildAdd(field, IntConstant(100, IntegerType.I64), "result")
        
        // Return the result
        builder.buildRet(result)
    }
}