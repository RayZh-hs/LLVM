package space.norb.llvm.integration

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import space.norb.llvm.structure.Module
import space.norb.llvm.types.ArrayType
import space.norb.llvm.types.IntegerType
import space.norb.llvm.values.globals.GlobalVariable
import space.norb.llvm.values.constants.ArrayConstant
import space.norb.llvm.values.constants.IntConstant
import space.norb.llvm.visitors.IRPrinter

/**
 * Integration tests for ArrayConstant with GlobalVariable.
 */
@DisplayName("ArrayConstant GlobalVariable Integration Tests")
class ArrayConstantGlobalVariableIntegrationTest {

    @Test
    @DisplayName("GlobalVariable should accept ArrayConstant as initializer")
    fun testGlobalVariableWithArrayConstantInitializer() {
        val module = Module("testModule")
        
        // Create an array constant: [i32 1, i32 2, i32 3]
        val arrayConstant = ArrayConstant.create(IntegerType.I32,
            IntConstant(1L, IntegerType.I32),
            IntConstant(2L, IntegerType.I32),
            IntConstant(3L, IntegerType.I32)
        )
        
        // Create global variable with array constant as initializer
        val globalArray = GlobalVariable.create(
            name = "global_array",
            module = module,
            initializer = arrayConstant,
            isConstantValue = true
        )
        
        // Verify the global variable was created correctly
        assertEquals("global_array", globalArray.name)
        assertEquals(arrayConstant, globalArray.initializer)
        assertEquals(arrayConstant.type, globalArray.elementType)
        assertTrue(globalArray.isConstantValue)
        assertTrue(globalArray.hasInitializer())
        
        // Add to module and print IR
        module.globalVariables.add(globalArray)
        val irPrinter = IRPrinter()
        val irOutput = irPrinter.print(module)
        
        // Verify IR output contains expected elements
        assertTrue(irOutput.contains("@global_array"))
        assertTrue(irOutput.contains("constant"))
        assertTrue(irOutput.contains("[3 x i32]"))
        assertTrue(irOutput.contains("[i32 1, i32 2, i32 3]"))
    }

    @Test
    @DisplayName("GlobalVariable should handle nested ArrayConstant as initializer")
    fun testGlobalVariableWithNestedArrayConstantInitializer() {
        val module = Module("testModule")
        
        // Create inner array constants: [i32 1, i32 2]
        val innerArray1 = ArrayConstant.create(IntegerType.I32,
            IntConstant(1L, IntegerType.I32),
            IntConstant(2L, IntegerType.I32)
        )
        
        val innerArray2 = ArrayConstant.create(IntegerType.I32,
            IntConstant(3L, IntegerType.I32),
            IntConstant(4L, IntegerType.I32)
        )
        
        // Create outer array constant: [2 x [2 x i32]]
        val outerArrayType = ArrayType(2, innerArray1.type)
        val outerArrayConstant = ArrayConstant.create(outerArrayType, innerArray1, innerArray2)
        
        // Create global variable with nested array constant as initializer
        val globalNestedArray = GlobalVariable.create(
            name = "global_nested_array",
            module = module,
            initializer = outerArrayConstant,
            isConstantValue = true
        )
        
        // Verify the global variable was created correctly
        assertEquals("global_nested_array", globalNestedArray.name)
        assertEquals(outerArrayConstant, globalNestedArray.initializer)
        assertEquals(outerArrayConstant.type, globalNestedArray.elementType)
        assertTrue(globalNestedArray.isConstantValue)
        assertTrue(globalNestedArray.hasInitializer())
        
        // Add to module and print IR
        module.globalVariables.add(globalNestedArray)
        val irPrinter = IRPrinter()
        val irOutput = irPrinter.print(module)
        
        // Verify IR output contains expected elements
        assertTrue(irOutput.contains("@global_nested_array"))
        assertTrue(irOutput.contains("constant"))
        assertTrue(irOutput.contains("[2 x [2 x i32]]"))
        assertTrue(irOutput.contains("[i32 1, i32 2]"))
        assertTrue(irOutput.contains("[i32 3, i32 4]"))
    }

    @Test
    @DisplayName("GlobalVariable should handle zero-initialized ArrayConstant")
    fun testGlobalVariableWithZeroArrayConstantInitializer() {
        val module = Module("testModule")
        
        // Create a zero array constant: [i32 0, i32 0, i32 0]
        val arrayType = ArrayType(3, IntegerType.I32)
        val zeroArrayConstant = ArrayConstant.createZero(arrayType)
        
        // Create global variable with zero array constant as initializer
        val globalZeroArray = GlobalVariable.create(
            name = "global_zero_array",
            module = module,
            initializer = zeroArrayConstant,
            isConstantValue = true
        )
        
        // Verify the global variable was created correctly
        assertEquals("global_zero_array", globalZeroArray.name)
        assertEquals(zeroArrayConstant, globalZeroArray.initializer)
        assertEquals(zeroArrayConstant.type, globalZeroArray.elementType)
        assertTrue(globalZeroArray.isConstantValue)
        assertTrue(globalZeroArray.hasInitializer())
        assertTrue(zeroArrayConstant.isZeroValue())
        
        // Add to module and print IR
        module.globalVariables.add(globalZeroArray)
        val irPrinter = IRPrinter()
        val irOutput = irPrinter.print(module)
        
        // Verify IR output contains expected elements
        assertTrue(irOutput.contains("@global_zero_array"))
        assertTrue(irOutput.contains("constant"))
        assertTrue(irOutput.contains("[3 x i32]"))
        assertTrue(irOutput.contains("[i32 0, i32 0, i32 0]"))
    }

    @Test
    @DisplayName("GlobalVariable should work with ArrayConstant and explicit element type")
    fun testGlobalVariableWithArrayConstantAndExplicitElementType() {
        val module = Module("testModule")
        
        // Create an array constant
        val arrayConstant = ArrayConstant.create(IntegerType.I32,
            IntConstant(10L, IntegerType.I32),
            IntConstant(20L, IntegerType.I32)
        )
        
        // Create global variable with explicit element type (different from array type)
        val explicitElementType = ArrayType(5, IntegerType.I64) // Different type
        val globalArray = GlobalVariable.createWithElementType(
            name = "global_array_explicit",
            elementType = explicitElementType,
            module = module,
            initializer = arrayConstant, // This should still work
            isConstantValue = true
        )
        
        // Verify the global variable was created correctly
        assertEquals("global_array_explicit", globalArray.name)
        assertEquals(arrayConstant, globalArray.initializer)
        assertEquals(explicitElementType, globalArray.elementType) // Should use explicit type
        assertTrue(globalArray.isConstantValue)
        assertTrue(globalArray.hasInitializer())
        
        // Add to module and print IR
        module.globalVariables.add(globalArray)
        val irPrinter = IRPrinter()
        val irOutput = irPrinter.print(module)
        
        // Verify IR output contains expected elements
        assertTrue(irOutput.contains("@global_array_explicit"))
        assertTrue(irOutput.contains("constant"))
        assertTrue(irOutput.contains("[5 x i64]")) // Should use explicit type
    }
}