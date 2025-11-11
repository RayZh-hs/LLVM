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
import space.norb.llvm.values.constants.FloatConstant
import space.norb.llvm.types.FloatingPointType
import space.norb.llvm.visitors.IRPrinter

/**
 * Integration tests for ArrayConstant with IRPrinter.
 */
@DisplayName("ArrayConstant IRPrinter Integration Tests")
class ArrayConstantIRPrinterIntegrationTest {

    @Test
    @DisplayName("IRPrinter should correctly print simple ArrayConstant")
    fun testIRPrinterSimpleArrayConstant() {
        val module = Module("testModule")
        
        // Create simple array constant: [i32 1, i32 2, i32 3]
        val arrayConstant = ArrayConstant.create(IntegerType.I32,
            IntConstant(1L, IntegerType.I32),
            IntConstant(2L, IntegerType.I32),
            IntConstant(3L, IntegerType.I32)
        )
        
        // Create global variable with array constant
        val globalArray = GlobalVariable.create(
            name = "simple_array",
            module = module,
            initializer = arrayConstant,
            isConstantValue = true
        )
        module.globalVariables.add(globalArray)
        
        // Print IR and verify
        val irPrinter = IRPrinter()
        val irOutput = irPrinter.print(module)
        
        // Verify IR output contains expected elements
        assertTrue(irOutput.contains("@simple_array"))
        assertTrue(irOutput.contains("[3 x i32]"))
        assertTrue(irOutput.contains("[i32 1, i32 2, i32 3]"))
    }

    @Test
    @DisplayName("IRPrinter should correctly print nested ArrayConstant")
    fun testIRPrinterNestedArrayConstant() {
        val module = Module("testModule")
        
        // Create inner array constants
        val innerArray1 = ArrayConstant.create(IntegerType.I32,
            IntConstant(10L, IntegerType.I32),
            IntConstant(20L, IntegerType.I32)
        )
        
        val innerArray2 = ArrayConstant.create(IntegerType.I32,
            IntConstant(30L, IntegerType.I32),
            IntConstant(40L, IntegerType.I32)
        )
        
        // Create outer array constant: [2 x [2 x i32]]
        val outerArrayConstant = ArrayConstant.create(ArrayType(2, innerArray1.type), innerArray1, innerArray2)
        
        // Create global variable with nested array constant
        val globalNestedArray = GlobalVariable.create(
            name = "nested_array",
            module = module,
            initializer = outerArrayConstant,
            isConstantValue = true
        )
        module.globalVariables.add(globalNestedArray)
        
        // Print IR and verify
        val irPrinter = IRPrinter()
        val irOutput = irPrinter.print(module)
        
        // Verify IR output contains expected elements
        assertTrue(irOutput.contains("@nested_array"))
        assertTrue(irOutput.contains("[2 x [2 x i32]]"))
        assertTrue(irOutput.contains("[i32 10, i32 20]"))
        assertTrue(irOutput.contains("[i32 30, i32 40]"))
        // Check that nested arrays are properly formatted
        assertTrue(irOutput.contains("[[2 x i32] [i32 10, i32 20], [2 x i32] [i32 30, i32 40]]"))
    }

    @Test
    @DisplayName("IRPrinter should correctly print float ArrayConstant")
    fun testIRPrinterFloatArrayConstant() {
        val module = Module("testModule")
        
        // Create float array constant: [float 1.0, float 2.5, float 3.14]
        val arrayConstant = ArrayConstant.create(FloatingPointType.FloatType,
            FloatConstant(1.0, FloatingPointType.FloatType),
            FloatConstant(2.5, FloatingPointType.FloatType),
            FloatConstant(3.14, FloatingPointType.FloatType)
        )
        
        // Create global variable with float array constant
        val globalFloatArray = GlobalVariable.create(
            name = "float_array",
            module = module,
            initializer = arrayConstant,
            isConstantValue = true
        )
        module.globalVariables.add(globalFloatArray)
        
        // Print IR and verify
        val irPrinter = IRPrinter()
        val irOutput = irPrinter.print(module)
        
        // Verify IR output contains expected elements
        assertTrue(irOutput.contains("@float_array"))
        assertTrue(irOutput.contains("[3 x float]"))
        assertTrue(irOutput.contains("[float 1.000000e+00, float 2.500000e+00, float 3.140000e+00]"))
    }

    @Test
    @DisplayName("IRPrinter should correctly print zero-initialized ArrayConstant")
    fun testIRPrinterZeroArrayConstant() {
        val module = Module("testModule")
        
        // Create zero-initialized array constant: [i32 0, i32 0, i32 0, i32 0]
        val arrayType = ArrayType(4, IntegerType.I32)
        val arrayConstant = ArrayConstant.createZero(arrayType)
        
        // Create global variable with zero array constant
        val globalZeroArray = GlobalVariable.create(
            name = "zero_array",
            module = module,
            initializer = arrayConstant,
            isConstantValue = true
        )
        module.globalVariables.add(globalZeroArray)
        
        // Print IR and verify
        val irPrinter = IRPrinter()
        val irOutput = irPrinter.print(module)
        
        // Verify IR output contains expected elements
        assertTrue(irOutput.contains("@zero_array"))
        assertTrue(irOutput.contains("[4 x i32]"))
        assertTrue(irOutput.contains("[i32 0, i32 0, i32 0, i32 0]"))
    }

    @Test
    @DisplayName("IRPrinter should correctly print large ArrayConstant")
    fun testIRPrinterLargeArrayConstant() {
        val module = Module("testModule")
        
        // Create large array constant: [i32 1, i32 2, ..., i32 100]
        val elements = (1..100).map { IntConstant(it.toLong(), IntegerType.I32) }
        val arrayConstant = ArrayConstant.create(IntegerType.I32, elements)
        
        // Create global variable with large array constant
        val globalLargeArray = GlobalVariable.create(
            name = "large_array",
            module = module,
            initializer = arrayConstant,
            isConstantValue = true
        )
        module.globalVariables.add(globalLargeArray)
        
        // Print IR and verify
        val irPrinter = IRPrinter()
        val irOutput = irPrinter.print(module)
        
        // Verify IR output contains expected elements
        assertTrue(irOutput.contains("@large_array"))
        assertTrue(irOutput.contains("[100 x i32]"))
        assertTrue(irOutput.contains("[i32 1, i32 2, i32 3, i32 4, i32 5"))
        assertTrue(irOutput.contains("i32 96, i32 97, i32 98, i32 99, i32 100]"))
    }
}