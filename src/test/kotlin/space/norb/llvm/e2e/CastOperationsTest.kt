package space.norb.llvm.e2e

import space.norb.llvm.e2e.framework.IRTestFramework
import space.norb.llvm.structure.Module
import space.norb.llvm.structure.Function
import space.norb.llvm.structure.BasicBlock
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.IntegerType
import space.norb.llvm.types.PointerType
import space.norb.llvm.values.constants.IntConstant
import kotlin.test.Test

/**
 * End-to-end tests for cast operations using the IRTestFramework.
 * Tests include bitcast, sext, zext, trunc operations.
 */
class CastOperationsTest {
    
    @Test
    fun testBitcastFunction() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testBitcastFunction",
            testClass = CastOperationsTest::class.java,
            resourceName = "CastOperationsTest_bitcast.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
                // Create function type: ptr (ptr)
                val functionType = FunctionType(
                    returnType = PointerType,
                    paramTypes = listOf(PointerType)
                )
                
                // Create the function
                val function = builder.createFunction("bitcast", functionType)
                
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
                val arg0 = function.parameters[0]
                
                // Create bitcast instruction (ptr to ptr)
                val result = builder.buildBitcast(arg0, PointerType, "result")
                
                // Return the result
                builder.buildRet(result)
            }
        )
    }
    
    @Test
    fun testSExtFunction() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testSExtFunction",
            testClass = CastOperationsTest::class.java,
            resourceName = "CastOperationsTest_sext.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
                // Create function type: i32 (i8)
                val functionType = FunctionType(
                    returnType = IntegerType.I32,
                    paramTypes = listOf(IntegerType.I8)
                )
                
                // Create the function
                val function = builder.createFunction("sext", functionType)
                
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
                val arg0 = function.parameters[0]
                
                // Create sext instruction (i8 to i32)
                val result = builder.buildSExt(arg0, IntegerType.I32, "result")
                
                // Return the result
                builder.buildRet(result)
            }
        )
    }
    
    @Test
    fun testZExtFunction() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testZExtFunction",
            testClass = CastOperationsTest::class.java,
            resourceName = "CastOperationsTest_zext.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
                // Create function type: i32 (i8)
                val functionType = FunctionType(
                    returnType = IntegerType.I32,
                    paramTypes = listOf(IntegerType.I8)
                )
                
                // Create the function
                val function = builder.createFunction("zext", functionType)
                
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
                val arg0 = function.parameters[0]
                
                // Create zext instruction (i8 to i32)
                val result = builder.buildZExt(arg0, IntegerType.I32, "result")
                
                // Return the result
                builder.buildRet(result)
            }
        )
    }
    
    @Test
    fun testTruncFunction() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testTruncFunction",
            testClass = CastOperationsTest::class.java,
            resourceName = "CastOperationsTest_trunc.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
                // Create function type: i8 (i32)
                val functionType = FunctionType(
                    returnType = IntegerType.I8,
                    paramTypes = listOf(IntegerType.I32)
                )
                
                // Create the function
                val function = builder.createFunction("trunc", functionType)
                
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
                val arg0 = function.parameters[0]
                
                // Create trunc instruction (i32 to i8)
                val result = builder.buildTrunc(arg0, IntegerType.I8, "result")
                
                // Return the result
                builder.buildRet(result)
            }
        )
    }
    
    @Test
    fun testComplexCastOperations() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testComplexCastOperations",
            testClass = CastOperationsTest::class.java,
            resourceName = "CastOperationsTest_complex.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
                // Create function type: i64 (i8, i32)
                val functionType = FunctionType(
                    returnType = IntegerType.I64,
                    paramTypes = listOf(IntegerType.I8, IntegerType.I32)
                )
                
                // Create the function
                val function = builder.createFunction("complex_cast_ops", functionType)
                
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
                val arg1 = function.parameters[1]  // i32
                
                // Create a sequence of cast operations
                val temp1 = builder.buildSExt(arg0, IntegerType.I64, "temp1")  // temp1 = sext i8 to i64
                val temp2 = builder.buildZExt(arg1, IntegerType.I64, "temp2")  // temp2 = zext i32 to i64
                val temp3 = builder.buildAdd(temp1, temp2, "temp3")            // temp3 = temp1 + temp2
                val temp4 = builder.buildTrunc(temp3, IntegerType.I32, "temp4") // temp4 = trunc i64 to i32
                val result = builder.buildSExt(temp4, IntegerType.I64, "result") // result = sext i32 to i64
                
                // Return the result
                builder.buildRet(result)
            }
        )
    }
    
    @Test
    fun testBitcastIntToPointer() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testBitcastIntToPointer",
            testClass = CastOperationsTest::class.java,
            resourceName = "CastOperationsTest_bitcast_int_ptr.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
                // Create function type: ptr (i64)
                val functionType = FunctionType(
                    returnType = PointerType,
                    paramTypes = listOf(IntegerType.I64)
                )
                
                // Create the function
                val function = builder.createFunction("bitcast_int_to_ptr", functionType)
                
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
                val arg0 = function.parameters[0]
                
                // Create bitcast instruction (i64 to ptr)
                val result = builder.buildBitcast(arg0, PointerType, "result")
                
                // Return the result
                builder.buildRet(result)
            }
        )
    }
}