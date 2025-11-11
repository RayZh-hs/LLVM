package space.norb.llvm.integration

import org.junit.jupiter.api.Test
import space.norb.llvm.structure.Module
import space.norb.llvm.builder.IRBuilder
import space.norb.llvm.types.ArrayType
import space.norb.llvm.types.IntegerType
import space.norb.llvm.types.FloatingPointType
import space.norb.llvm.types.PointerType
import space.norb.llvm.types.FunctionType
import space.norb.llvm.values.constants.ArrayConstant
import space.norb.llvm.values.constants.IntConstant
import space.norb.llvm.values.constants.FloatConstant
import space.norb.llvm.values.globals.GlobalVariable
import space.norb.llvm.visitors.IRPrinter

/**
 * Debug test to see the actual IR output for function calls with array parameters
 */
class DebugFunctionCallTest {

    @Test
    fun debugFunctionCallIR() {
        val module = Module("DebugFunctionCallTest")
        val builder = IRBuilder(module)
        
        // Create an array type for function parameters
        val arrayType = ArrayType(3, FloatingPointType.FloatType)
        
        // Create a function that takes an array parameter
        val arrayFunctionType = FunctionType(FloatingPointType.FloatType, listOf(PointerType))
        val arrayFunction = builder.createFunction("processArray", arrayFunctionType)
        module.functions.add(arrayFunction)
        
        // Create the function body
        val arrayFuncEntry = arrayFunction.insertBasicBlock("entry")
        builder.positionAtEnd(arrayFuncEntry)
        
        // Get the array parameter
        val arrayParam = arrayFunction.parameters[0]
        
        // Get pointer to the second element (index 1)
        val elementPtr = builder.insertGep(
            elementType = arrayType,
            address = arrayParam,
            indices = listOf(IntConstant(0L, IntegerType.I64), IntConstant(1L, IntegerType.I32)),
            name = "element_ptr"
        )
        
        // Load the value
        val loadedValue = builder.insertLoad(FloatingPointType.FloatType, elementPtr, "element_value")
        
        // Return the loaded value
        builder.insertRet(loadedValue)
        
        // Create a main function that calls the array function
        val mainFunctionType = FunctionType(FloatingPointType.FloatType, emptyList())
        val mainFunction = builder.createFunction("main", mainFunctionType)
        module.functions.add(mainFunction)
        
        val mainEntry = mainFunction.insertBasicBlock("entry")
        builder.positionAtEnd(mainEntry)
        
        // Create a local array constant
        val localArrayConstant = ArrayConstant.create(
            arrayType,
            FloatConstant(1.1, FloatingPointType.FloatType),
            FloatConstant(2.2, FloatingPointType.FloatType),
            FloatConstant(3.3, FloatingPointType.FloatType)
        )
        
        // Allocate space for the array
        val arrayAlloca = builder.insertAlloca(arrayType, "array_alloca")
        
        // Store the array constant to memory (element by element)
        for (i in 0 until 3) {
            val elementPtr = builder.insertGep(
                elementType = arrayType,
                address = arrayAlloca,
                indices = listOf(IntConstant(0L, IntegerType.I64), IntConstant(i.toLong(), IntegerType.I32)),
                name = "element_ptr_$i"
            )
            builder.insertStore(localArrayConstant.elements[i], elementPtr)
        }
        
        // Call the function with the array
        val callResult = builder.insertCall(arrayFunction, listOf(arrayAlloca), "call_result")
        
        // Return the result
        builder.insertRet(callResult)
        
        // Generate IR
        val irPrinter = IRPrinter()
        val ir = irPrinter.print(module)
        
        // Print the IR to see what it actually looks like
        println("=== IR Output ===")
        println(ir)
        println("=== End IR Output ===")
    }
}