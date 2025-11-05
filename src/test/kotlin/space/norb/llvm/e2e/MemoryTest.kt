package space.norb.llvm.e2e

import space.norb.llvm.e2e.framework.IRTestFramework
import space.norb.llvm.structure.Module
import space.norb.llvm.structure.Function
import space.norb.llvm.structure.BasicBlock
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.IntegerType
import space.norb.llvm.types.PointerType
import space.norb.llvm.builder.BuilderUtils
import kotlin.test.Test

/**
 * End-to-end tests for memory operations using the IRTestFramework.
 */
class MemoryTest {
    
    @Test
    fun testAllocaStoreLoad() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testAllocaStoreLoad",
            testClass = MemoryTest::class.java,
            resourceName = "MemoryTest_alloca_store_load.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
                // Create function type: i32 (i32)
                val functionType = FunctionType(
                    returnType = IntegerType.I32,
                    paramTypes = listOf(IntegerType.I32)
                )
                
                // Create the function
                val function = builder.createFunction("alloca_store_load", functionType)
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
                
                // Allocate memory on stack
                val alloca = builder.buildAlloca(IntegerType.I32, "var")
                
                // Store the argument value
                builder.buildStore(arg0, alloca)
                
                // Load the value back
                val loaded = builder.buildLoad(IntegerType.I32, alloca, "loaded")
                
                // Return the loaded value
                builder.buildRet(loaded)
            }
        )
    }
    
    @Test
    fun testGetElementPtr() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testGetElementPtr",
            testClass = MemoryTest::class.java,
            resourceName = "MemoryTest_getelementptr.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
                // Create function type: i32 (i32*)
                val functionType = FunctionType(
                    returnType = IntegerType.I32,
                    paramTypes = listOf(PointerType)
                )
                
                // Create the function
                val function = builder.createFunction("getelementptr_example", functionType)
                module.functions.add(function)
                
                // Create entry block
                val entryBlock = builder.createBasicBlock("entry", function)
                function.basicBlocks.add(entryBlock)
                
                if (function.entryBlock == null) {
                    function.entryBlock = entryBlock
                }
                
                builder.positionAtEnd(entryBlock)
                
                // Get function argument (pointer to i32)
                val arg0 = function.parameters[0]
                
                // Create indices for GEP (get element at index 2)
                val index = BuilderUtils.getIntConstant(2, IntegerType.I32)
                
                // Get pointer to element at index 2
                val gep = builder.buildGep(IntegerType.I32, arg0, listOf(index), "ptr_to_elem")
                
                // Load the value at that location
                val loaded = builder.buildLoad(IntegerType.I32, gep, "loaded")
                
                // Return the loaded value
                builder.buildRet(loaded)
            }
        )
    }
}