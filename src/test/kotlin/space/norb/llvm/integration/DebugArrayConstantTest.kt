package space.norb.llvm.integration

import org.junit.jupiter.api.Test
import space.norb.llvm.structure.Module
import space.norb.llvm.types.ArrayType
import space.norb.llvm.types.IntegerType
import space.norb.llvm.values.globals.GlobalVariable
import space.norb.llvm.values.constants.ArrayConstant
import space.norb.llvm.values.constants.IntConstant
import space.norb.llvm.instructions.memory.GetElementPtrInst
import space.norb.llvm.builder.IRBuilder
import space.norb.llvm.visitors.IRPrinter

/**
 * Debug test to see actual IR output
 */
class DebugArrayConstantTest {

    @Test
    fun debugArrayConstantIR() {
        val module = Module("testModule")
        val builder = IRBuilder(module)
        
        // Create global array constant: [i32 10, i32 20, i32 30, i32 40, i32 50]
        val arrayConstant = ArrayConstant.create(IntegerType.I32,
            IntConstant(10L, IntegerType.I32),
            IntConstant(20L, IntegerType.I32),
            IntConstant(30L, IntegerType.I32),
            IntConstant(40L, IntegerType.I32),
            IntConstant(50L, IntegerType.I32)
        )
        
        // Create global variable with array constant
        val globalArray = GlobalVariable.create(
            name = "global_array",
            module = module,
            initializer = arrayConstant,
            isConstantValue = true
        )
        module.globalVariables.add(globalArray)
        
        // Create function that accesses array elements
        val functionType = space.norb.llvm.types.FunctionType(IntegerType.I32, listOf(IntegerType.I32))
        val function = builder.createFunction("get_array_element", functionType)
        module.functions.add(function)
        
        val entryBlock = function.insertBasicBlock("entry")
        if (function.entryBlock == null) {
            function.entryBlock = entryBlock
        }
        builder.positionAtEnd(entryBlock)
        
        // Get function parameter (index)
        val indexParam = function.parameters[0]
        
        // Create GEP to access array element: array[index]
        val gep = builder.insertGep(
            elementType = arrayConstant.type,
            address = globalArray,
            indices = listOf(IntConstant(0L, IntegerType.I64), indexParam),
            name = "element_ptr"
        )
        
        // Load the element
        val load = builder.insertLoad(IntegerType.I32, gep, "element_value")
        
        // Return the loaded value
        builder.insertRet(load)
        
        // Print IR and verify
        val irPrinter = IRPrinter()
        val irOutput = irPrinter.print(module)
        
        println("=== ACTUAL IR OUTPUT ===")
        println(irOutput)
        println("=== END IR OUTPUT ===")
        
        // Check what we're actually looking for
        println("Looking for: getelementptr [5 x i32], ptr @global_array, i64 0, i32 %0")
        println("Contains global_array: ${irOutput.contains("@global_array")}")
        println("Contains [5 x i32]: ${irOutput.contains("[5 x i32]")}")
        println("Contains getelementptr: ${irOutput.contains("getelementptr")}")
        println("Contains getelementptr with pattern: ${irOutput.contains("getelementptr [5 x i32], ptr @global_array, i64 0, i32 %0")}")
    }
}