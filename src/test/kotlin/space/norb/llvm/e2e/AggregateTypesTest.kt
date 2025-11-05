package space.norb.llvm.e2e

import space.norb.llvm.e2e.framework.IRTestFramework
import space.norb.llvm.structure.Module
import space.norb.llvm.structure.Function
import space.norb.llvm.structure.BasicBlock
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.ArrayType
import space.norb.llvm.types.StructType
import space.norb.llvm.types.IntegerType
import space.norb.llvm.types.PointerType
import space.norb.llvm.values.constants.IntConstant
import kotlin.test.Test

/**
 * End-to-end tests for aggregate types (arrays and structs) using the IRTestFramework.
 * Tests include array operations, struct operations, and GEP with aggregate types.
 */
class AggregateTypesTest {
    
    @Test
    fun testSimpleArray() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testSimpleArray",
            testClass = AggregateTypesTest::class.java,
            resourceName = "AggregateTypesTest_simple_array.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
                // Create function type: ptr ()
                val functionType = FunctionType(
                    returnType = PointerType,
                    paramTypes = emptyList()
                )
                
                // Create the function
                val function = builder.createFunction("simple_array", functionType)
                
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
                
                // Create an array type: [10 x i32]
                val arrayType = ArrayType(10, IntegerType.I32)
                
                // Allocate array
                val arrayPtr = builder.buildAlloca(arrayType, "array")
                
                // Return the array pointer
                builder.buildRet(arrayPtr)
            }
        )
    }
    
    @Test
    fun testArrayAccess() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testArrayAccess",
            testClass = AggregateTypesTest::class.java,
            resourceName = "AggregateTypesTest_array_access.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
                // Create function type: i32 (ptr, i32)
                val functionType = FunctionType(
                    returnType = IntegerType.I32,
                    paramTypes = listOf(PointerType, IntegerType.I32)
                )
                
                // Create the function
                val function = builder.createFunction("array_access", functionType)
                
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
                val index = function.parameters[1]
                
                // Create array type: [10 x i32]
                val arrayType = ArrayType(10, IntegerType.I32)
                
                // Get element pointer: gep [10 x i32], ptr, 0, index
                val gepIndices = listOf(IntConstant(0, IntegerType.I32), index)
                val elementPtr = builder.buildGep(arrayType, arrayPtr, gepIndices, "element_ptr")
                
                // Load the element
                val element = builder.buildLoad(IntegerType.I32, elementPtr, "element")
                
                // Return the element
                builder.buildRet(element)
            }
        )
    }
    
    @Test
    fun testSimpleStruct() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testSimpleStruct",
            testClass = AggregateTypesTest::class.java,
            resourceName = "AggregateTypesTest_simple_struct.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
                // Create function type: ptr ()
                val functionType = FunctionType(
                    returnType = PointerType,
                    paramTypes = emptyList()
                )
                
                // Create the function
                val function = builder.createFunction("simple_struct", functionType)
                
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
                
                // Create a struct type: { i32, i64, float }
                val structType = StructType(listOf(IntegerType.I32, IntegerType.I64, space.norb.llvm.types.FloatingPointType.FloatType))
                
                // Allocate struct
                val structPtr = builder.buildAlloca(structType, "struct")
                
                // Return the struct pointer
                builder.buildRet(structPtr)
            }
        )
    }
    
    @Test
    fun testStructAccess() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testStructAccess",
            testClass = AggregateTypesTest::class.java,
            resourceName = "AggregateTypesTest_struct_access.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
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
                val structType = StructType(listOf(IntegerType.I32, IntegerType.I64, space.norb.llvm.types.FloatingPointType.FloatType))
                
                // Get element pointer for the second field (index 1): gep { i32, i64, float }, ptr, 0, 1
                val gepIndices = listOf(IntConstant(0, IntegerType.I32), IntConstant(1, IntegerType.I32))
                val fieldPtr = builder.buildGep(structType, structPtr, gepIndices, "field_ptr")
                
                // Load the field
                val field = builder.buildLoad(IntegerType.I64, fieldPtr, "field")
                
                // Return the field
                builder.buildRet(field)
            }
        )
    }
    
    @Test
    fun testNestedStruct() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testNestedStruct",
            testClass = AggregateTypesTest::class.java,
            resourceName = "AggregateTypesTest_nested_struct.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
                // Create function type: i32 (ptr)
                val functionType = FunctionType(
                    returnType = IntegerType.I32,
                    paramTypes = listOf(PointerType)
                )
                
                // Create the function
                val function = builder.createFunction("nested_struct", functionType)
                
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
                
                // Create inner struct type: { i32, i64 }
                val innerStructType = StructType(listOf(IntegerType.I32, IntegerType.I64))
                
                // Create outer struct type: { float, { i32, i64 }, i8 }
                val outerStructType = StructType(listOf(
                    space.norb.llvm.types.FloatingPointType.FloatType,
                    innerStructType,
                    IntegerType.I8
                ))
                
                // Get element pointer for the nested struct's first field: gep { float, { i32, i64 }, i8 }, ptr, 0, 1, 0
                val gepIndices = listOf(
                    IntConstant(0, IntegerType.I32), 
                    IntConstant(1, IntegerType.I32), 
                    IntConstant(0, IntegerType.I32)
                )
                val fieldPtr = builder.buildGep(outerStructType, structPtr, gepIndices, "field_ptr")
                
                // Load the field
                val field = builder.buildLoad(IntegerType.I32, fieldPtr, "field")
                
                // Return the field
                builder.buildRet(field)
            }
        )
    }
    
    @Test
    fun testArrayOfStructs() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testArrayOfStructs",
            testClass = AggregateTypesTest::class.java,
            resourceName = "AggregateTypesTest_array_of_structs.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
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
                val fieldPtr = builder.buildGep(arrayType, arrayPtr, gepIndices, "field_ptr")
                
                // Load the field
                val field = builder.buildLoad(IntegerType.I32, fieldPtr, "field")
                
                // Return the field
                builder.buildRet(field)
            }
        )
    }
    
    @Test
    fun testStructInArray() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testStructInArray",
            testClass = AggregateTypesTest::class.java,
            resourceName = "AggregateTypesTest_struct_in_array.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
                // Create function type: ptr ()
                val functionType = FunctionType(
                    returnType = PointerType,
                    paramTypes = emptyList()
                )
                
                // Create the function
                val function = builder.createFunction("struct_in_array", functionType)
                
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
                
                // Create array type: [3 x i32]
                val arrayType = ArrayType(3, IntegerType.I32)
                
                // Create struct type containing an array: { i32, [3 x i32], i64 }
                val structType = StructType(listOf(IntegerType.I32, arrayType, IntegerType.I64))
                
                // Allocate struct
                val structPtr = builder.buildAlloca(structType, "struct")
                
                // Return the struct pointer
                builder.buildRet(structPtr)
            }
        )
    }
    
    @Test
    fun testComplexAggregateOperations() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testComplexAggregateOperations",
            testClass = AggregateTypesTest::class.java,
            resourceName = "AggregateTypesTest_complex_aggregate.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
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
        )
    }
}