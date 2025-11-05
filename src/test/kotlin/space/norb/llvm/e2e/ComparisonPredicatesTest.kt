package space.norb.llvm.e2e

import space.norb.llvm.e2e.framework.IRTestFramework
import space.norb.llvm.structure.Module
import space.norb.llvm.structure.Function
import space.norb.llvm.structure.BasicBlock
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.IntegerType
import space.norb.llvm.enums.IcmpPredicate
import kotlin.test.Test

/**
 * End-to-end tests for comparison predicates using the IRTestFramework.
 * Tests include EQ, UGT, UGE, ULT, ULE, SGT, SGE, SLT, SLE comparison predicates.
 */
class ComparisonPredicatesTest {
    
    @Test
    fun testEQPredicate() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testEQPredicate",
            testClass = ComparisonPredicatesTest::class.java,
            resourceName = "ComparisonPredicatesTest_eq.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
                // Create function type: i1 (i32, i32)
                val functionType = FunctionType(
                    returnType = IntegerType.I1,
                    paramTypes = listOf(IntegerType.I32, IntegerType.I32)
                )
                
                // Create the function
                val function = builder.createFunction("eq_test", functionType)
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
                
                // Create EQ comparison
                val result = builder.buildICmp(IcmpPredicate.EQ, arg0, arg1, "result")
                
                // Return the result
                builder.buildRet(result)
            }
        )
    }
    
    @Test
    fun testUGTPredicate() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testUGTPredicate",
            testClass = ComparisonPredicatesTest::class.java,
            resourceName = "ComparisonPredicatesTest_ugt.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
                // Create function type: i1 (i32, i32)
                val functionType = FunctionType(
                    returnType = IntegerType.I1,
                    paramTypes = listOf(IntegerType.I32, IntegerType.I32)
                )
                
                // Create the function
                val function = builder.createFunction("ugt_test", functionType)
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
                
                // Create UGT comparison
                val result = builder.buildICmp(IcmpPredicate.UGT, arg0, arg1, "result")
                
                // Return the result
                builder.buildRet(result)
            }
        )
    }
    
    @Test
    fun testUGEPredicate() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testUGEPredicate",
            testClass = ComparisonPredicatesTest::class.java,
            resourceName = "ComparisonPredicatesTest_uge.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
                // Create function type: i1 (i32, i32)
                val functionType = FunctionType(
                    returnType = IntegerType.I1,
                    paramTypes = listOf(IntegerType.I32, IntegerType.I32)
                )
                
                // Create the function
                val function = builder.createFunction("uge_test", functionType)
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
                
                // Create UGE comparison
                val result = builder.buildICmp(IcmpPredicate.UGE, arg0, arg1, "result")
                
                // Return the result
                builder.buildRet(result)
            }
        )
    }
    
    @Test
    fun testULTPredicate() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testULTPredicate",
            testClass = ComparisonPredicatesTest::class.java,
            resourceName = "ComparisonPredicatesTest_ult.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
                // Create function type: i1 (i32, i32)
                val functionType = FunctionType(
                    returnType = IntegerType.I1,
                    paramTypes = listOf(IntegerType.I32, IntegerType.I32)
                )
                
                // Create the function
                val function = builder.createFunction("ult_test", functionType)
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
                
                // Create ULT comparison
                val result = builder.buildICmp(IcmpPredicate.ULT, arg0, arg1, "result")
                
                // Return the result
                builder.buildRet(result)
            }
        )
    }
    
    @Test
    fun testULEPredicate() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testULEPredicate",
            testClass = ComparisonPredicatesTest::class.java,
            resourceName = "ComparisonPredicatesTest_ule.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
                // Create function type: i1 (i32, i32)
                val functionType = FunctionType(
                    returnType = IntegerType.I1,
                    paramTypes = listOf(IntegerType.I32, IntegerType.I32)
                )
                
                // Create the function
                val function = builder.createFunction("ule_test", functionType)
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
                
                // Create ULE comparison
                val result = builder.buildICmp(IcmpPredicate.ULE, arg0, arg1, "result")
                
                // Return the result
                builder.buildRet(result)
            }
        )
    }
    
    @Test
    fun testSGTPredicate() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testSGTPredicate",
            testClass = ComparisonPredicatesTest::class.java,
            resourceName = "ComparisonPredicatesTest_sgt.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
                // Create function type: i1 (i32, i32)
                val functionType = FunctionType(
                    returnType = IntegerType.I1,
                    paramTypes = listOf(IntegerType.I32, IntegerType.I32)
                )
                
                // Create the function
                val function = builder.createFunction("sgt_test", functionType)
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
                
                // Create SGT comparison
                val result = builder.buildICmp(IcmpPredicate.SGT, arg0, arg1, "result")
                
                // Return the result
                builder.buildRet(result)
            }
        )
    }
    
    @Test
    fun testSGEPredicate() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testSGEPredicate",
            testClass = ComparisonPredicatesTest::class.java,
            resourceName = "ComparisonPredicatesTest_sge.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
                // Create function type: i1 (i32, i32)
                val functionType = FunctionType(
                    returnType = IntegerType.I1,
                    paramTypes = listOf(IntegerType.I32, IntegerType.I32)
                )
                
                // Create the function
                val function = builder.createFunction("sge_test", functionType)
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
                
                // Create SGE comparison
                val result = builder.buildICmp(IcmpPredicate.SGE, arg0, arg1, "result")
                
                // Return the result
                builder.buildRet(result)
            }
        )
    }
    
    @Test
    fun testSLTPredicate() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testSLTPredicate",
            testClass = ComparisonPredicatesTest::class.java,
            resourceName = "ComparisonPredicatesTest_slt.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
                // Create function type: i1 (i32, i32)
                val functionType = FunctionType(
                    returnType = IntegerType.I1,
                    paramTypes = listOf(IntegerType.I32, IntegerType.I32)
                )
                
                // Create the function
                val function = builder.createFunction("slt_test", functionType)
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
                
                // Create SLT comparison
                val result = builder.buildICmp(IcmpPredicate.SLT, arg0, arg1, "result")
                
                // Return the result
                builder.buildRet(result)
            }
        )
    }
    
    @Test
    fun testSLEPredicate() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testSLEPredicate",
            testClass = ComparisonPredicatesTest::class.java,
            resourceName = "ComparisonPredicatesTest_sle.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
                // Create function type: i1 (i32, i32)
                val functionType = FunctionType(
                    returnType = IntegerType.I1,
                    paramTypes = listOf(IntegerType.I32, IntegerType.I32)
                )
                
                // Create the function
                val function = builder.createFunction("sle_test", functionType)
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
                
                // Create SLE comparison
                val result = builder.buildICmp(IcmpPredicate.SLE, arg0, arg1, "result")
                
                // Return the result
                builder.buildRet(result)
            }
        )
    }
    
    @Test
    fun testComplexComparisons() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testComplexComparisons",
            testClass = ComparisonPredicatesTest::class.java,
            resourceName = "ComparisonPredicatesTest_complex.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
                // Create function type: i32 (i32, i32, i32)
                val functionType = FunctionType(
                    returnType = IntegerType.I32,
                    paramTypes = listOf(IntegerType.I32, IntegerType.I32, IntegerType.I32)
                )
                
                // Create the function
                val function = builder.createFunction("complex_comparisons", functionType)
                module.functions.add(function)
                
                // Create basic blocks
                val entryBlock = builder.createBasicBlock("entry", function)
                val thenBlock = builder.createBasicBlock("then", function)
                val elseBlock = builder.createBasicBlock("else", function)
                val mergeBlock = builder.createBasicBlock("merge", function)
                
                function.basicBlocks.addAll(listOf(entryBlock, thenBlock, elseBlock, mergeBlock))
                
                if (function.entryBlock == null) {
                    function.entryBlock = entryBlock
                }
                
                // Entry block
                builder.positionAtEnd(entryBlock)
                val arg0 = function.parameters[0]
                val arg1 = function.parameters[1]
                val arg2 = function.parameters[2]
                
                // Compare arg0 with arg1 using signed greater than
                val cmp1 = builder.buildICmp(IcmpPredicate.SGT, arg0, arg1, "cmp1")
                // Compare arg1 with arg2 using unsigned less than
                val cmp2 = builder.buildICmp(IcmpPredicate.ULT, arg1, arg2, "cmp2")
                // Combine the two comparisons with AND
                val condition = builder.buildAnd(cmp1, cmp2, "condition")
                builder.buildCondBr(condition, thenBlock, elseBlock)
                
                // Then block
                builder.positionAtEnd(thenBlock)
                val thenResult = builder.buildAdd(arg0, arg2, "then_result")
                builder.buildBr(mergeBlock)
                
                // Else block
                builder.positionAtEnd(elseBlock)
                val elseResult = builder.buildSub(arg0, arg2, "else_result")
                builder.buildBr(mergeBlock)
                
                // Merge block
                builder.positionAtEnd(mergeBlock)
                val result = builder.buildPhi(IntegerType.I32, listOf(
                    Pair(thenResult, thenBlock),
                    Pair(elseResult, elseBlock)
                ), "result")
                builder.buildRet(result)
            }
        )
    }
}