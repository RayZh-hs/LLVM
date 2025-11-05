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
 * End-to-end tests for integer width operations using the IRTestFramework.
 * Tests include i8, i16, i64, i128 operations.
 */
class IntegerWidthsTest {
    
    @Test
    fun testI8Operations() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testI8Operations",
            testClass = IntegerWidthsTest::class.java,
            resourceName = "IntegerWidthsTest_i8.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
                // Create function type: i8 (i8, i8)
                val functionType = FunctionType(
                    returnType = IntegerType.I8,
                    paramTypes = listOf(IntegerType.I8, IntegerType.I8)
                )
                
                // Create the function
                val function = builder.createFunction("i8_ops", functionType)
                
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
                
                // Create operations with i8 values
                val temp1 = builder.buildAdd(arg0, arg1, "temp1")  // temp1 = a + b
                val temp2 = builder.buildAnd(temp1, IntConstant(15, IntegerType.I8), "temp2")  // temp2 = temp1 & 15
                val result = builder.buildXor(temp2, IntConstant(255, IntegerType.I8), "result")  // result = temp2 ^ 255
                
                // Return the result
                builder.buildRet(result)
            }
        )
    }
    
    @Test
    fun testI16Operations() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testI16Operations",
            testClass = IntegerWidthsTest::class.java,
            resourceName = "IntegerWidthsTest_i16.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
                // Create function type: i16 (i16, i16)
                val functionType = FunctionType(
                    returnType = IntegerType.I16,
                    paramTypes = listOf(IntegerType.I16, IntegerType.I16)
                )
                
                // Create the function
                val function = builder.createFunction("i16_ops", functionType)
                
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
                
                // Create operations with i16 values
                val temp1 = builder.buildMul(arg0, arg1, "temp1")  // temp1 = a * b
                val temp2 = builder.buildOr(temp1, IntConstant(4095, IntegerType.I16), "temp2")  // temp2 = temp1 | 4095
                val result = builder.buildSub(temp2, IntConstant(1000, IntegerType.I16), "result")  // result = temp2 - 1000
                
                // Return the result
                builder.buildRet(result)
            }
        )
    }
    
    @Test
    fun testI64Operations() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testI64Operations",
            testClass = IntegerWidthsTest::class.java,
            resourceName = "IntegerWidthsTest_i64.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
                // Create function type: i64 (i64, i64)
                val functionType = FunctionType(
                    returnType = IntegerType.I64,
                    paramTypes = listOf(IntegerType.I64, IntegerType.I64)
                )
                
                // Create the function
                val function = builder.createFunction("i64_ops", functionType)
                
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
                
                // Create operations with i64 values
                val temp1 = builder.buildAdd(arg0, arg1, "temp1")  // temp1 = a + b
                val temp2 = builder.buildSDiv(temp1, IntConstant(2L, IntegerType.I64), "temp2")  // temp2 = temp1 / 2
                val temp3 = builder.buildAnd(temp2, IntConstant(281474976710655L, IntegerType.I64), "temp3")  // temp3 = temp2 & mask
                val result = builder.buildXor(temp3, IntConstant(0x4000000000000000L, IntegerType.I64), "result")  // result = temp3 ^ sign_bit
                
                // Return the result
                builder.buildRet(result)
            }
        )
    }
    
    @Test
    fun testI128Operations() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testI128Operations",
            testClass = IntegerWidthsTest::class.java,
            resourceName = "IntegerWidthsTest_i128.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
                // Create function type: i128 (i128, i128)
                val functionType = FunctionType(
                    returnType = IntegerType.I128,
                    paramTypes = listOf(IntegerType.I128, IntegerType.I128)
                )
                
                // Create the function
                val function = builder.createFunction("i128_ops", functionType)
                
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
                
                // Create operations with i128 values
                val temp1 = builder.buildMul(arg0, arg1, "temp1")  // temp1 = a * b
                val temp2 = builder.buildAdd(temp1, IntConstant(1L, IntegerType.I128), "temp2")  // temp2 = temp1 + 1
                val temp3 = builder.buildOr(temp2, IntConstant(-1L, IntegerType.I128), "temp3")  // temp3 = temp2 | -1 (all bits set)
                val result = builder.buildAnd(temp3, IntConstant(-1L, IntegerType.I128), "result")  // result = temp3 & mask
                
                // Return the result
                builder.buildRet(result)
            }
        )
    }
    
    @Test
    fun testMixedWidthOperations() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testMixedWidthOperations",
            testClass = IntegerWidthsTest::class.java,
            resourceName = "IntegerWidthsTest_mixed.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
                // Create function type: i64 (i8, i16, i32, i64)
                val functionType = FunctionType(
                    returnType = IntegerType.I64,
                    paramTypes = listOf(IntegerType.I8, IntegerType.I16, IntegerType.I32, IntegerType.I64)
                )
                
                // Create the function
                val function = builder.createFunction("mixed_width_ops", functionType)
                
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
                val arg0 = function.parameters[0]  // i8
                val arg1 = function.parameters[1]  // i16
                val arg2 = function.parameters[2]  // i32
                val arg3 = function.parameters[3]  // i64
                
                // Create operations with mixed width values
                val temp1 = builder.buildSExt(arg0, IntegerType.I64, "temp1")  // temp1 = sext i8 to i64
                val temp2 = builder.buildSExt(arg1, IntegerType.I64, "temp2")  // temp2 = sext i16 to i64
                val temp3 = builder.buildSExt(arg2, IntegerType.I64, "temp3")  // temp3 = sext i32 to i64
                val temp4 = builder.buildAdd(temp1, temp2, "temp4")  // temp4 = temp1 + temp2
                val temp5 = builder.buildAdd(temp4, temp3, "temp5")  // temp5 = temp4 + temp3
                val result = builder.buildAdd(temp5, arg3, "result")  // result = temp5 + arg3
                
                // Return the result
                builder.buildRet(result)
            }
        )
    }
    
    @Test
    fun testWidthCastingOperations() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testWidthCastingOperations",
            testClass = IntegerWidthsTest::class.java,
            resourceName = "IntegerWidthsTest_casting.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
                // Create function type: i128 (i64)
                val functionType = FunctionType(
                    returnType = IntegerType.I128,
                    paramTypes = listOf(IntegerType.I64)
                )
                
                // Create the function
                val function = builder.createFunction("width_casting_ops", functionType)
                
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
                val arg0 = function.parameters[0]  // i64
                
                // Create operations with width casting
                val temp1 = builder.buildZExt(arg0, IntegerType.I128, "temp1")  // temp1 = zext i64 to i128
                val temp2 = builder.buildMul(temp1, IntConstant(2L, IntegerType.I128), "temp2")  // temp2 = temp1 * 2
                val temp3 = builder.buildTrunc(temp2, IntegerType.I64, "temp3")  // temp3 = trunc i128 to i64
                val temp4 = builder.buildSExt(temp3, IntegerType.I128, "temp4")  // temp4 = sext i64 to i128
                val result = builder.buildAdd(temp4, IntConstant(1000000L, IntegerType.I128), "result")  // result = temp4 + 1000000
                
                // Return the result
                builder.buildRet(result)
            }
        )
    }
}