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
 * End-to-end tests for convenience operations using the IRTestFramework.
 * Tests include NOT and negation operations.
 */
class ConvenienceOperationsTest {
    
    @Test
    fun testNotOperation() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testNotOperation",
            testClass = ConvenienceOperationsTest::class.java,
            resourceName = "ConvenienceOperationsTest_not.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
                // Create function type: i32 (i32)
                val functionType = FunctionType(
                    returnType = IntegerType.I32,
                    paramTypes = listOf(IntegerType.I32)
                )
                
                // Create the function
                val function = builder.createFunction("not_operation", functionType)
                module.functions.add(function)
                
                // Create entry block
                val entryBlock = builder.createBasicBlock("entry", function)
                function.basicBlocks.add(entryBlock)
                
                if (function.entryBlock == null) {
                    function.entryBlock = entryBlock
                }
                
                builder.positionAtEnd(entryBlock)
                
                // Get function argument
                val arg0 = function.parameters[0]
                
                // Create NOT operation
                val result = builder.buildNot(arg0, "result")
                
                // Return the result
                builder.buildRet(result)
            }
        )
    }
    
    @Test
    fun testNegOperation() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testNegOperation",
            testClass = ConvenienceOperationsTest::class.java,
            resourceName = "ConvenienceOperationsTest_neg.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
                // Create function type: i32 (i32)
                val functionType = FunctionType(
                    returnType = IntegerType.I32,
                    paramTypes = listOf(IntegerType.I32)
                )
                
                // Create the function
                val function = builder.createFunction("neg_operation", functionType)
                module.functions.add(function)
                
                // Create entry block
                val entryBlock = builder.createBasicBlock("entry", function)
                function.basicBlocks.add(entryBlock)
                
                if (function.entryBlock == null) {
                    function.entryBlock = entryBlock
                }
                
                builder.positionAtEnd(entryBlock)
                
                // Get function argument
                val arg0 = function.parameters[0]
                
                // Create negation operation
                val result = builder.buildNeg(arg0, "result")
                
                // Return the result
                builder.buildRet(result)
            }
        )
    }
    
    @Test
    fun testNotOperationI8() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testNotOperationI8",
            testClass = ConvenienceOperationsTest::class.java,
            resourceName = "ConvenienceOperationsTest_not_i8.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
                // Create function type: i8 (i8)
                val functionType = FunctionType(
                    returnType = IntegerType.I8,
                    paramTypes = listOf(IntegerType.I8)
                )
                
                // Create the function
                val function = builder.createFunction("not_operation_i8", functionType)
                module.functions.add(function)
                
                // Create entry block
                val entryBlock = builder.createBasicBlock("entry", function)
                function.basicBlocks.add(entryBlock)
                
                if (function.entryBlock == null) {
                    function.entryBlock = entryBlock
                }
                
                builder.positionAtEnd(entryBlock)
                
                // Get function argument
                val arg0 = function.parameters[0]
                
                // Create NOT operation
                val result = builder.buildNot(arg0, "result")
                
                // Return the result
                builder.buildRet(result)
            }
        )
    }
    
    @Test
    fun testNegOperationI64() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testNegOperationI64",
            testClass = ConvenienceOperationsTest::class.java,
            resourceName = "ConvenienceOperationsTest_neg_i64.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
                // Create function type: i64 (i64)
                val functionType = FunctionType(
                    returnType = IntegerType.I64,
                    paramTypes = listOf(IntegerType.I64)
                )
                
                // Create the function
                val function = builder.createFunction("neg_operation_i64", functionType)
                module.functions.add(function)
                
                // Create entry block
                val entryBlock = builder.createBasicBlock("entry", function)
                function.basicBlocks.add(entryBlock)
                
                if (function.entryBlock == null) {
                    function.entryBlock = entryBlock
                }
                
                builder.positionAtEnd(entryBlock)
                
                // Get function argument
                val arg0 = function.parameters[0]
                
                // Create negation operation
                val result = builder.buildNeg(arg0, "result")
                
                // Return the result
                builder.buildRet(result)
            }
        )
    }
    
    @Test
    fun testComplexConvenienceOperations() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testComplexConvenienceOperations",
            testClass = ConvenienceOperationsTest::class.java,
            resourceName = "ConvenienceOperationsTest_complex.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
                // Create function type: i32 (i32, i32)
                val functionType = FunctionType(
                    returnType = IntegerType.I32,
                    paramTypes = listOf(IntegerType.I32, IntegerType.I32)
                )
                
                // Create the function
                val function = builder.createFunction("complex_convenience_ops", functionType)
                module.functions.add(function)
                
                // Create entry block
                val entryBlock = builder.createBasicBlock("entry", function)
                function.basicBlocks.add(entryBlock)
                
                if (function.entryBlock == null) {
                    function.entryBlock = entryBlock
                }
                
                builder.positionAtEnd(entryBlock)
                
                // Get function arguments
                val arg0 = function.parameters[0]
                val arg1 = function.parameters[1]
                
                // Create a sequence of convenience operations
                val notArg0 = builder.buildNot(arg0, "not_arg0")  // not_arg0 = ~arg0
                val negArg1 = builder.buildNeg(arg1, "neg_arg1")  // neg_arg1 = -arg1
                val temp = builder.buildAnd(notArg0, negArg1, "temp")  // temp = not_arg0 & neg_arg1
                val result = builder.buildNot(temp, "result")  // result = ~temp
                
                // Return the result
                builder.buildRet(result)
            }
        )
    }
    
    @Test
    fun testNotWithConstants() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testNotWithConstants",
            testClass = ConvenienceOperationsTest::class.java,
            resourceName = "ConvenienceOperationsTest_not_constants.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
                // Create function type: i32 ()
                val functionType = FunctionType(
                    returnType = IntegerType.I32,
                    paramTypes = emptyList()
                )
                
                // Create the function
                val function = builder.createFunction("not_with_constants", functionType)
                module.functions.add(function)
                
                // Create entry block
                val entryBlock = builder.createBasicBlock("entry", function)
                function.basicBlocks.add(entryBlock)
                
                if (function.entryBlock == null) {
                    function.entryBlock = entryBlock
                }
                
                builder.positionAtEnd(entryBlock)
                
                // Create constants
                val const1 = IntConstant(0x12345678, IntegerType.I32)
                val const2 = IntConstant(0x87654321, IntegerType.I32)
                
                // Create NOT operations with constants
                val not1 = builder.buildNot(const1, "not1")
                val not2 = builder.buildNot(const2, "not2")
                
                // Combine the results
                val result = builder.buildXor(not1, not2, "result")
                
                // Return the result
                builder.buildRet(result)
            }
        )
    }
    
    @Test
    fun testNegWithConstants() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testNegWithConstants",
            testClass = ConvenienceOperationsTest::class.java,
            resourceName = "ConvenienceOperationsTest_neg_constants.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
                // Create function type: i32 ()
                val functionType = FunctionType(
                    returnType = IntegerType.I32,
                    paramTypes = emptyList()
                )
                
                // Create the function
                val function = builder.createFunction("neg_with_constants", functionType)
                module.functions.add(function)
                
                // Create entry block
                val entryBlock = builder.createBasicBlock("entry", function)
                function.basicBlocks.add(entryBlock)
                
                if (function.entryBlock == null) {
                    function.entryBlock = entryBlock
                }
                
                builder.positionAtEnd(entryBlock)
                
                // Create constants
                val const1 = IntConstant(42, IntegerType.I32)
                val const2 = IntConstant(-17, IntegerType.I32)
                
                // Create negation operations with constants
                val neg1 = builder.buildNeg(const1, "neg1")
                val neg2 = builder.buildNeg(const2, "neg2")
                
                // Combine the results
                val result = builder.buildAdd(neg1, neg2, "result")
                
                // Return the result
                builder.buildRet(result)
            }
        )
    }
}