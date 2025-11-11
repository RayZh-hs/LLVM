package space.norb.llvm.integration

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import space.norb.llvm.structure.Module
import space.norb.llvm.builder.IRBuilder
import space.norb.llvm.types.ArrayType
import space.norb.llvm.types.IntegerType
import space.norb.llvm.types.FunctionType
import space.norb.llvm.values.constants.ArrayConstant
import space.norb.llvm.values.constants.IntConstant
import space.norb.llvm.values.globals.GlobalVariable
import space.norb.llvm.visitors.IRPrinter

@DisplayName("ArrayConstant Simple Integration Test")
class ArrayConstantSimpleIntegrationTest {

    @Test
    @DisplayName("ArrayConstant should integrate properly with GlobalVariable and generate correct IR")
    fun testArrayConstantIntegration() {
        val module = Module("ArrayConstantIntegrationTest")
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
        
        // Generate and verify the IR output
        val irPrinter = IRPrinter()
        val irOutput = irPrinter.print(module)
        
        println("Generated IR:")
        println(irOutput)
        
        // Verify the IR contains expected elements
        assertTrue(irOutput.contains("@myGlobalArray"))
        assertTrue(irOutput.contains("constant"))
        assertTrue(irOutput.contains("[4 x i32]"))
        assertTrue(irOutput.contains("[i32 10, i32 20, i32 30, i32 40]"))
        assertTrue(irOutput.contains("define i32 @getThirdElement"))
        assertTrue(irOutput.contains("getelementptr"))
        assertTrue(irOutput.contains("load i32"))
        assertTrue(irOutput.contains("ret i32 %element_value"))
    }
}