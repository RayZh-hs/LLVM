package space.norb.llvm.e2e

import space.norb.llvm.e2e.framework.IRTestFramework
import space.norb.llvm.structure.Module
import space.norb.llvm.structure.Function
import space.norb.llvm.structure.BasicBlock
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.FloatingPointType
import space.norb.llvm.values.constants.FloatConstant
import kotlin.test.Test

/**
 * End-to-end tests for floating-point operations using the IRTestFramework.
 * Tests include float and double operations.
 */
class FloatingPointTest {
    
    @Test
    fun testFloatAdd() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testFloatAdd",
            testClass = FloatingPointTest::class.java,
            resourceName = "FloatingPointTest_float_add.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
                // Create function type: float (float, float)
                val functionType = FunctionType(
                    returnType = FloatingPointType.FloatType,
                    paramTypes = listOf(FloatingPointType.FloatType, FloatingPointType.FloatType)
                )
                
                // Create the function
                val function = builder.createFunction("float_add", functionType)
                
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
                val arg0 = function.parameters[0]
                val arg1 = function.parameters[1]
                
                // Create float addition
                val result = builder.buildAdd(arg0, arg1, "result")
                
                // Return the result
                builder.buildRet(result)
            }
        )
    }
    
    @Test
    fun testFloatMul() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testFloatMul",
            testClass = FloatingPointTest::class.java,
            resourceName = "FloatingPointTest_float_mul.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
                // Create function type: float (float, float)
                val functionType = FunctionType(
                    returnType = FloatingPointType.FloatType,
                    paramTypes = listOf(FloatingPointType.FloatType, FloatingPointType.FloatType)
                )
                
                // Create the function
                val function = builder.createFunction("float_mul", functionType)
                
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
                val arg0 = function.parameters[0]
                val arg1 = function.parameters[1]
                
                // Create float multiplication
                val result = builder.buildMul(arg0, arg1, "result")
                
                // Return the result
                builder.buildRet(result)
            }
        )
    }
    
    @Test
    fun testFloatSub() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testFloatSub",
            testClass = FloatingPointTest::class.java,
            resourceName = "FloatingPointTest_float_sub.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
                // Create function type: float (float, float)
                val functionType = FunctionType(
                    returnType = FloatingPointType.FloatType,
                    paramTypes = listOf(FloatingPointType.FloatType, FloatingPointType.FloatType)
                )
                
                // Create the function
                val function = builder.createFunction("float_sub", functionType)
                
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
                val arg0 = function.parameters[0]
                val arg1 = function.parameters[1]
                
                // Create float subtraction
                val result = builder.buildSub(arg0, arg1, "result")
                
                // Return the result
                builder.buildRet(result)
            }
        )
    }
    
    @Test
    fun testDoubleAdd() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testDoubleAdd",
            testClass = FloatingPointTest::class.java,
            resourceName = "FloatingPointTest_double_add.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
                // Create function type: double (double, double)
                val functionType = FunctionType(
                    returnType = FloatingPointType.DoubleType,
                    paramTypes = listOf(FloatingPointType.DoubleType, FloatingPointType.DoubleType)
                )
                
                // Create the function
                val function = builder.createFunction("double_add", functionType)
                
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
                val arg0 = function.parameters[0]
                val arg1 = function.parameters[1]
                
                // Create double addition
                val result = builder.buildAdd(arg0, arg1, "result")
                
                // Return the result
                builder.buildRet(result)
            }
        )
    }
    
    @Test
    fun testDoubleMul() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testDoubleMul",
            testClass = FloatingPointTest::class.java,
            resourceName = "FloatingPointTest_double_mul.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
                // Create function type: double (double, double)
                val functionType = FunctionType(
                    returnType = FloatingPointType.DoubleType,
                    paramTypes = listOf(FloatingPointType.DoubleType, FloatingPointType.DoubleType)
                )
                
                // Create the function
                val function = builder.createFunction("double_mul", functionType)
                
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
                val arg0 = function.parameters[0]
                val arg1 = function.parameters[1]
                
                // Create double multiplication
                val result = builder.buildMul(arg0, arg1, "result")
                
                // Return the result
                builder.buildRet(result)
            }
        )
    }
    
    @Test
    fun testFloatConstants() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testFloatConstants",
            testClass = FloatingPointTest::class.java,
            resourceName = "FloatingPointTest_float_constants.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
                // Create function type: float ()
                val functionType = FunctionType(
                    returnType = FloatingPointType.FloatType,
                    paramTypes = emptyList()
                )
                
                // Create the function
                val function = builder.createFunction("float_constants", functionType)
                
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
                
                // Create float constants and operations
                val const1 = FloatConstant(3.14, FloatingPointType.FloatType)
                val const2 = FloatConstant(2.71, FloatingPointType.FloatType)
                val temp1 = builder.buildAdd(const1, const2, "temp1")
                val const3 = FloatConstant(1.0, FloatingPointType.FloatType)
                val result = builder.buildMul(temp1, const3, "result")
                
                // Return the result
                builder.buildRet(result)
            }
        )
    }
    
    @Test
    fun testDoubleConstants() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testDoubleConstants",
            testClass = FloatingPointTest::class.java,
            resourceName = "FloatingPointTest_double_constants.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
                // Create function type: double ()
                val functionType = FunctionType(
                    returnType = FloatingPointType.DoubleType,
                    paramTypes = emptyList()
                )
                
                // Create the function
                val function = builder.createFunction("double_constants", functionType)
                
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
                
                // Create double constants and operations
                val const1 = FloatConstant(3.14159265359, FloatingPointType.DoubleType)
                val const2 = FloatConstant(2.71828182846, FloatingPointType.DoubleType)
                val temp1 = builder.buildAdd(const1, const2, "temp1")
                val const3 = FloatConstant(1.0, FloatingPointType.DoubleType)
                val result = builder.buildMul(temp1, const3, "result")
                
                // Return the result
                builder.buildRet(result)
            }
        )
    }
    
    @Test
    fun testComplexFloatOperations() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testComplexFloatOperations",
            testClass = FloatingPointTest::class.java,
            resourceName = "FloatingPointTest_complex_float.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
                // Create function type: float (float, float, float)
                val functionType = FunctionType(
                    returnType = FloatingPointType.FloatType,
                    paramTypes = listOf(FloatingPointType.FloatType, FloatingPointType.FloatType, FloatingPointType.FloatType)
                )
                
                // Create the function
                val function = builder.createFunction("complex_float_ops", functionType)
                
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
                val arg0 = function.parameters[0]
                val arg1 = function.parameters[1]
                val arg2 = function.parameters[2]
                
                // Create complex float operations
                val temp1 = builder.buildMul(arg0, arg1, "temp1")  // temp1 = a * b
                val temp2 = builder.buildAdd(temp1, arg2, "temp2")  // temp2 = temp1 + c
                val const1 = FloatConstant(0.5, FloatingPointType.FloatType)
                val result = builder.buildMul(temp2, const1, "result")  // result = temp2 * 0.5
                
                // Return the result
                builder.buildRet(result)
            }
        )
    }
    
    @Test
    fun testComplexDoubleOperations() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testComplexDoubleOperations",
            testClass = FloatingPointTest::class.java,
            resourceName = "FloatingPointTest_complex_double.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
                // Create function type: double (double, double, double)
                val functionType = FunctionType(
                    returnType = FloatingPointType.DoubleType,
                    paramTypes = listOf(FloatingPointType.DoubleType, FloatingPointType.DoubleType, FloatingPointType.DoubleType)
                )
                
                // Create the function
                val function = builder.createFunction("complex_double_ops", functionType)
                
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
                val arg0 = function.parameters[0]
                val arg1 = function.parameters[1]
                val arg2 = function.parameters[2]
                
                // Create complex double operations
                val temp1 = builder.buildMul(arg0, arg1, "temp1")  // temp1 = a * b
                val temp2 = builder.buildAdd(temp1, arg2, "temp2")  // temp2 = temp1 + c
                val const1 = FloatConstant(0.5, FloatingPointType.DoubleType)
                val result = builder.buildMul(temp2, const1, "result")  // result = temp2 * 0.5
                
                // Return the result
                builder.buildRet(result)
            }
        )
    }
}