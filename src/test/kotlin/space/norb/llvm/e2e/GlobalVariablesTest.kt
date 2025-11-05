package space.norb.llvm.e2e

import space.norb.llvm.e2e.framework.IRTestFramework
import space.norb.llvm.structure.Module
import space.norb.llvm.structure.Function
import space.norb.llvm.structure.BasicBlock
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.IntegerType
import space.norb.llvm.types.PointerType
import space.norb.llvm.types.ArrayType
import space.norb.llvm.types.StructType
import space.norb.llvm.values.globals.GlobalVariable
import space.norb.llvm.values.constants.IntConstant
import space.norb.llvm.enums.LinkageType
import kotlin.test.Test

/**
 * End-to-end tests for global variables with different linkage types using the IRTestFramework.
 * Tests include external, private, internal, and other linkage types.
 */
class GlobalVariablesTest {
    
    @Test
    fun testExternalGlobalVariable() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testExternalGlobalVariable",
            testClass = GlobalVariablesTest::class.java,
            resourceName = "GlobalVariablesTest_external.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
                // Create external global variable
                val globalVar = GlobalVariable.create(
                    name = "external_global",
                    module = module,
                    initializer = IntConstant(42, IntegerType.I32),
                    linkage = LinkageType.EXTERNAL
                )
                
                // Add global variable to the module
                module.globalVariables.add(globalVar)
                
                // Create function type: i32 ()
                val functionType = FunctionType(
                    returnType = IntegerType.I32,
                    paramTypes = emptyList()
                )
                
                // Create the function
                val function = builder.createFunction("get_external_global", functionType)
                
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
                
                // Load the global variable
                val loadedValue = builder.buildLoad(IntegerType.I32, globalVar, "loaded_value")
                
                // Return the loaded value
                builder.buildRet(loadedValue)
            }
        )
    }
    
    @Test
    fun testPrivateGlobalVariable() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testPrivateGlobalVariable",
            testClass = GlobalVariablesTest::class.java,
            resourceName = "GlobalVariablesTest_private.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
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
                
                // Load the global variable
                val loadedValue = builder.buildLoad(IntegerType.I32, globalVar, "loaded_value")
                
                // Return the loaded value
                builder.buildRet(loadedValue)
            }
        )
    }
    
    @Test
    fun testInternalGlobalVariable() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testInternalGlobalVariable",
            testClass = GlobalVariablesTest::class.java,
            resourceName = "GlobalVariablesTest_internal.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
                // Create internal global variable
                val globalVar = GlobalVariable.create(
                    name = "internal_global",
                    module = module,
                    initializer = IntConstant(200, IntegerType.I32),
                    linkage = LinkageType.INTERNAL
                )
                
                // Add global variable to the module
                module.globalVariables.add(globalVar)
                
                // Create function type: i32 ()
                val functionType = FunctionType(
                    returnType = IntegerType.I32,
                    paramTypes = emptyList()
                )
                
                // Create the function
                val function = builder.createFunction("get_internal_global", functionType)
                
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
                
                // Load the global variable
                val loadedValue = builder.buildLoad(IntegerType.I32, globalVar, "loaded_value")
                
                // Return the loaded value
                builder.buildRet(loadedValue)
            }
        )
    }
    
    @Test
    fun testGlobalArray() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testGlobalArray",
            testClass = GlobalVariablesTest::class.java,
            resourceName = "GlobalVariablesTest_array.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
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
                
                // Return the global array pointer
                builder.buildRet(globalArray)
            }
        )
    }
    
    @Test
    fun testGlobalStruct() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testGlobalStruct",
            testClass = GlobalVariablesTest::class.java,
            resourceName = "GlobalVariablesTest_struct.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
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
                
                // Return the global struct pointer
                builder.buildRet(globalStruct)
            }
        )
    }
    
    @Test
    fun testConstantGlobal() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testConstantGlobal",
            testClass = GlobalVariablesTest::class.java,
            resourceName = "GlobalVariablesTest_constant.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
                // Create constant global variable
                val constantGlobal = GlobalVariable.create(
                    name = "constant_global",
                    module = module,
                    initializer = IntConstant(12345, IntegerType.I32),
                    isConstantValue = true,
                    linkage = LinkageType.EXTERNAL
                )
                
                // Add global variable to the module
                module.globalVariables.add(constantGlobal)
                
                // Create function type: i32 ()
                val functionType = FunctionType(
                    returnType = IntegerType.I32,
                    paramTypes = emptyList()
                )
                
                // Create the function
                val function = builder.createFunction("get_constant_global", functionType)
                
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
                
                // Load the constant global variable
                val loadedValue = builder.buildLoad(IntegerType.I32, constantGlobal, "loaded_value")
                
                // Return the loaded value
                builder.buildRet(loadedValue)
            }
        )
    }
    
    @Test
    fun testWeakGlobalVariable() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testWeakGlobalVariable",
            testClass = GlobalVariablesTest::class.java,
            resourceName = "GlobalVariablesTest_weak.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
                // Create weak global variable
                val weakGlobal = GlobalVariable.create(
                    name = "weak_global",
                    module = module,
                    initializer = IntConstant(999, IntegerType.I32),
                    linkage = LinkageType.WEAK
                )
                
                // Add global variable to the module
                module.globalVariables.add(weakGlobal)
                
                // Create function type: i32 ()
                val functionType = FunctionType(
                    returnType = IntegerType.I32,
                    paramTypes = emptyList()
                )
                
                // Create the function
                val function = builder.createFunction("get_weak_global", functionType)
                
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
                
                // Load the weak global variable
                val loadedValue = builder.buildLoad(IntegerType.I32, weakGlobal, "loaded_value")
                
                // Return the loaded value
                builder.buildRet(loadedValue)
            }
        )
    }
    
    @Test
    fun testComplexGlobalOperations() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testComplexGlobalOperations",
            testClass = GlobalVariablesTest::class.java,
            resourceName = "GlobalVariablesTest_complex.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
                // Create multiple global variables with different linkage types
                val global1 = GlobalVariable.create(
                    name = "global_counter",
                    module = module,
                    initializer = IntConstant(0, IntegerType.I32),
                    linkage = LinkageType.INTERNAL
                )
                
                val global2 = GlobalVariable.create(
                    name = "global_multiplier",
                    module = module,
                    initializer = IntConstant(10, IntegerType.I32),
                    isConstantValue = true,
                    linkage = LinkageType.PRIVATE
                )
                
                // Add global variables to the module
                module.globalVariables.add(global1)
                module.globalVariables.add(global2)
                
                // Create function type: i32 (i32)
                val functionType = FunctionType(
                    returnType = IntegerType.I32,
                    paramTypes = listOf(IntegerType.I32)
                )
                
                // Create the function
                val function = builder.createFunction("complex_global_ops", functionType)
                
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
                
                // Get function argument
                val arg = function.parameters[0]
                
                // Load global variables
                val counter = builder.buildLoad(IntegerType.I32, global1, "counter")
                val multiplier = builder.buildLoad(IntegerType.I32, global2, "multiplier")
                
                // Perform operations
                val temp1 = builder.buildAdd(counter, arg, "temp1")
                val result = builder.buildMul(temp1, multiplier, "result")
                
                // Store the new counter value
                builder.buildStore(temp1, global1)
                
                // Return the result
                builder.buildRet(result)
            }
        )
    }
}