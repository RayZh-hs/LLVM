package space.norb.llvm.e2e

import space.norb.llvm.e2e.framework.IRTestFramework
import space.norb.llvm.structure.Module
import space.norb.llvm.structure.Function
import space.norb.llvm.structure.BasicBlock
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.IntegerType
import space.norb.llvm.values.constants.IntConstant
import kotlin.test.Test

/**
 * End-to-end tests for binary operations using the IRTestFramework.
 * Tests include sub, mul, sdiv, and, or, xor operations.
 */
class BinaryOperationsTest {
    
    @Test
    fun testSubFunction() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testSubFunction",
            testClass = BinaryOperationsTest::class.java,
            resourceName = "BinaryOperationsTest_sub.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
                // Create function type: i32 (i32, i32)
                val functionType = FunctionType(
                    returnType = IntegerType.I32,
                    paramTypes = listOf(IntegerType.I32, IntegerType.I32)
                )
                
                // Create the function
                val function = builder.createFunction("sub", functionType)
                
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
                
                // Create sub instruction
                val result = builder.buildSub(arg0, arg1, "result")
                
                // Return the result
                builder.buildRet(result)
            }
        )
    }
    
    @Test
    fun testMulFunction() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testMulFunction",
            testClass = BinaryOperationsTest::class.java,
            resourceName = "BinaryOperationsTest_mul.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
                // Create function type: i32 (i32, i32)
                val functionType = FunctionType(
                    returnType = IntegerType.I32,
                    paramTypes = listOf(IntegerType.I32, IntegerType.I32)
                )
                
                // Create the function
                val function = builder.createFunction("mul", functionType)
                
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
                
                // Create mul instruction
                val result = builder.buildMul(arg0, arg1, "result")
                
                // Return the result
                builder.buildRet(result)
            }
        )
    }
    
    @Test
    fun testSDivFunction() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testSDivFunction",
            testClass = BinaryOperationsTest::class.java,
            resourceName = "BinaryOperationsTest_sdiv.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
                // Create function type: i32 (i32, i32)
                val functionType = FunctionType(
                    returnType = IntegerType.I32,
                    paramTypes = listOf(IntegerType.I32, IntegerType.I32)
                )
                
                // Create the function
                val function = builder.createFunction("sdiv", functionType)
                
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
                
                // Create sdiv instruction
                val result = builder.buildSDiv(arg0, arg1, "result")
                
                // Return the result
                builder.buildRet(result)
            }
        )
    }
    
    @Test
    fun testAndFunction() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testAndFunction",
            testClass = BinaryOperationsTest::class.java,
            resourceName = "BinaryOperationsTest_and.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
                // Create function type: i32 (i32, i32)
                val functionType = FunctionType(
                    returnType = IntegerType.I32,
                    paramTypes = listOf(IntegerType.I32, IntegerType.I32)
                )
                
                // Create the function
                val function = builder.createFunction("and", functionType)
                
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
                
                // Create and instruction
                val result = builder.buildAnd(arg0, arg1, "result")
                
                // Return the result
                builder.buildRet(result)
            }
        )
    }
    
    @Test
    fun testOrFunction() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testOrFunction",
            testClass = BinaryOperationsTest::class.java,
            resourceName = "BinaryOperationsTest_or.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
                // Create function type: i32 (i32, i32)
                val functionType = FunctionType(
                    returnType = IntegerType.I32,
                    paramTypes = listOf(IntegerType.I32, IntegerType.I32)
                )
                
                // Create the function
                val function = builder.createFunction("or", functionType)
                
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
                
                // Create or instruction
                val result = builder.buildOr(arg0, arg1, "result")
                
                // Return the result
                builder.buildRet(result)
            }
        )
    }
    
    @Test
    fun testXorFunction() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testXorFunction",
            testClass = BinaryOperationsTest::class.java,
            resourceName = "BinaryOperationsTest_xor.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
                // Create function type: i32 (i32, i32)
                val functionType = FunctionType(
                    returnType = IntegerType.I32,
                    paramTypes = listOf(IntegerType.I32, IntegerType.I32)
                )
                
                // Create the function
                val function = builder.createFunction("xor", functionType)
                
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
                
                // Create xor instruction
                val result = builder.buildXor(arg0, arg1, "result")
                
                // Return the result
                builder.buildRet(result)
            }
        )
    }
    
    @Test
    fun testComplexBinaryOperations() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testComplexBinaryOperations",
            testClass = BinaryOperationsTest::class.java,
            resourceName = "BinaryOperationsTest_complex.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
                // Create function type: i32 (i32, i32, i32)
                val functionType = FunctionType(
                    returnType = IntegerType.I32,
                    paramTypes = listOf(IntegerType.I32, IntegerType.I32, IntegerType.I32)
                )
                
                // Create the function
                val function = builder.createFunction("complex_binary_ops", functionType)
                
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
                
                // Create a sequence of binary operations
                val temp1 = builder.buildMul(arg0, arg1, "temp1")  // temp1 = a * b
                val temp2 = builder.buildAdd(temp1, arg2, "temp2")  // temp2 = temp1 + c
                val temp3 = builder.buildAnd(temp2, IntConstant(255, IntegerType.I32), "temp3")  // temp3 = temp2 & 255
                val result = builder.buildXor(temp3, IntConstant(128, IntegerType.I32), "result")  // result = temp3 ^ 128
                
                // Return the result
                builder.buildRet(result)
            }
        )
    }
}