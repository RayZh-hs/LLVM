package space.norb.llvm.integration

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import space.norb.llvm.structure.Module
import space.norb.llvm.builder.IRBuilder
import space.norb.llvm.types.ArrayType
import space.norb.llvm.types.FloatingPointType
import space.norb.llvm.types.PointerType
import space.norb.llvm.types.FunctionType
import space.norb.llvm.values.constants.ArrayConstant
import space.norb.llvm.values.constants.FloatConstant
import space.norb.llvm.values.globals.GlobalVariable
import space.norb.llvm.visitors.IRPrinter

/**
 * Debug test to see actual IR output
 */
@DisplayName("Debug IR Output Test")
class DebugIROutputTest {

    @Test
    @DisplayName("Debug float array IR output")
    fun debugFloatArrayIR() {
        val module = Module("DebugTest")
        
        // Create a float array constant
        val arrayType = ArrayType(3, FloatingPointType.FloatType)
        val arrayConstant = ArrayConstant.create(
            arrayType,
            FloatConstant(1.1, FloatingPointType.FloatType),
            FloatConstant(2.2, FloatingPointType.FloatType),
            FloatConstant(3.3, FloatingPointType.FloatType)
        )
        
        // Create a global variable with the array constant
        val globalArray = GlobalVariable.create(
            name = "globalFloatArray",
            module = module,
            initializer = arrayConstant,
            isConstantValue = true
        )
        module.globalVariables.add(globalArray)
        
        // Print the IR
        val irPrinter = IRPrinter()
        val irOutput = irPrinter.print(module)
        
        println("=== IR Output ===")
        println(irOutput)
        println("=== End IR Output ===")
        
        // Check what's actually in the output
        assertTrue(irOutput.contains("@globalFloatArray"))
        assertTrue(irOutput.contains("[3 x float]"))
    }

    @Test
    @DisplayName("Debug zero array IR output")
    fun debugZeroArrayIR() {
        val module = Module("DebugTest")
        
        // Create a zero array constant
        val arrayType = ArrayType(4, space.norb.llvm.types.IntegerType.I32)
        val zeroArrayConstant = ArrayConstant.createZero(arrayType)
        
        // Create a global variable with the zero array constant
        val globalArray = GlobalVariable.create(
            name = "globalZeroArray",
            module = module,
            initializer = zeroArrayConstant,
            isConstantValue = true
        )
        module.globalVariables.add(globalArray)
        
        // Print the IR
        val irPrinter = IRPrinter()
        val irOutput = irPrinter.print(module)
        
        println("=== IR Output ===")
        println(irOutput)
        println("=== End IR Output ===")
        
        // Check what's actually in the output
        assertTrue(irOutput.contains("@globalZeroArray"))
        assertTrue(irOutput.contains("[4 x i32]"))
    }
}