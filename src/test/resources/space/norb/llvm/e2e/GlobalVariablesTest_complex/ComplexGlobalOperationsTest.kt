package space.norb.llvm.e2e.extracted.global_variables

import space.norb.llvm.structure.Module
import space.norb.llvm.structure.Function
import space.norb.llvm.structure.BasicBlock
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.IntegerType
import space.norb.llvm.values.globals.GlobalVariable
import space.norb.llvm.values.constants.IntConstant
import space.norb.llvm.enums.LinkageType
import space.norb.llvm.builder.IRBuilder

/**
 * Extracted test case for complex global operations.
 * Originally from GlobalVariablesTest.testComplexGlobalOperations()
 */
object ComplexGlobalOperationsTest {
    
    /**
     * Builds the IR for this test case.
     *
     * @param module The module to add the IR to
     * @param builder The IR builder to use
     */
    fun buildIR(module: Module, builder: IRBuilder) {
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
        module.functions.add(function)
        
        // Create entry block
        val entryBlock = builder.createBasicBlock("entry", function)
        function.basicBlocks.add(entryBlock)
        
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
}