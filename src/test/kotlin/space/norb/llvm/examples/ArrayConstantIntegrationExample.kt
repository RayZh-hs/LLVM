package space.norb.llvm.examples

import space.norb.llvm.structure.Module
import space.norb.llvm.builder.IRBuilder
import space.norb.llvm.types.ArrayType
import space.norb.llvm.types.IntegerType
import space.norb.llvm.types.FunctionType
import space.norb.llvm.values.constants.ArrayConstant
import space.norb.llvm.values.constants.IntConstant
import space.norb.llvm.values.globals.GlobalVariable
import space.norb.llvm.visitors.IRPrinter

/**
 * Simple integration example demonstrating ArrayConstant with GlobalVariable.
 * This example shows how to:
 * 1. Create an ArrayConstant with integer elements
 * 2. Use it as an initializer for a GlobalVariable
 * 3. Access array elements using GetElementPtr
 * 4. Generate proper LLVM IR output
 */
object ArrayConstantIntegrationExample {
    
    fun createExample(): String {
        val module = Module("ArrayConstantIntegrationExample")
        val builder = IRBuilder(module)
        
        // Create an array type: [4 x i32]
        val arrayType = ArrayType(4, IntegerType.I32)
        
        // Create an ArrayConstant with specific values
        val arrayConstant = ArrayConstant.create(
            arrayType,
            IntConstant(10L, IntegerType.I32),
            IntConstant(20L, IntegerType.I32),
            IntConstant(30L, IntegerType.I32),
            IntConstant(40L, IntegerType.I32)
        )
        
        // Create a global variable with the array constant as initializer
        val globalArray = GlobalVariable.create(
            name = "myGlobalArray",
            module = module,
            initializer = arrayConstant,
            isConstantValue = true
        )
        module.globalVariables.add(globalArray)
        
        // Create a function that accesses the global array
        val functionType = FunctionType(IntegerType.I32, emptyList())
        val function = builder.createFunction("getThirdElement", functionType)
        module.functions.add(function)
        
        val entryBlock = function.insertBasicBlock("entry")
        builder.positionAtEnd(entryBlock)
        
        // Get pointer to the third element (index 2) using GetElementPtr
        val elementPtr = builder.insertGep(
            elementType = arrayType,
            address = globalArray,
            indices = listOf(IntConstant(0L, IntegerType.I64), IntConstant(2L, IntegerType.I32)),
            name = "element_ptr"
        )
        
        // Load the value from the array element
        val loadedValue = builder.insertLoad(IntegerType.I32, elementPtr, "element_value")
        
        // Return the loaded value
        builder.insertRet(loadedValue)
        
        // Generate and return the IR
        val irPrinter = IRPrinter()
        return irPrinter.print(module)
    }
    
    fun main() {
        val irOutput = createExample()
        println("Generated LLVM IR:")
        println(irOutput)
        
        // Verify the IR contains expected elements
        val expectedElements = listOf(
            "@myGlobalArray",
            "constant",
            "[4 x i32]",
            "[i32 10, i32 20, i32 30, i32 40]",
            "define i32 @getThirdElement",
            "getelementptr",
            "load i32",
            "ret i32 30"
        )
        
        println("\nVerification:")
        expectedElements.forEach { expected ->
            val found = irOutput.contains(expected)
            println("$expected: ${if (found) "✓" else "✗"}")
        }
    }
}

// Run the example if this file is executed directly
fun main() = ArrayConstantIntegrationExample.main()