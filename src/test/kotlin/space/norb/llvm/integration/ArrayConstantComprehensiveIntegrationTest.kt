package space.norb.llvm.integration

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import space.norb.llvm.structure.Module
import space.norb.llvm.structure.Function
import space.norb.llvm.structure.BasicBlock
import space.norb.llvm.core.Type
import space.norb.llvm.builder.IRBuilder
import space.norb.llvm.types.ArrayType
import space.norb.llvm.types.IntegerType
import space.norb.llvm.types.FloatingPointType
import space.norb.llvm.types.PointerType
import space.norb.llvm.types.FunctionType
import space.norb.llvm.values.constants.ArrayConstant
import space.norb.llvm.values.constants.IntConstant
import space.norb.llvm.values.constants.FloatConstant
import space.norb.llvm.values.constants.NullPointerConstant
import space.norb.llvm.values.globals.GlobalVariable
import space.norb.llvm.visitors.IRPrinter

/**
 * Comprehensive integration tests for ArrayConstant with the entire LLVM system.
 * This test demonstrates ArrayConstant working seamlessly with:
 * - GlobalVariable initialization
 * - GetElementPtr instructions
 * - Function calls with array parameters
 * - Memory operations (load/store) with arrays
 */
@DisplayName("ArrayConstant Comprehensive Integration Tests")
class ArrayConstantComprehensiveIntegrationTest {

    @Test
    @DisplayName("ArrayConstant should work with GlobalVariable, GetElementPtr, and memory operations")
    fun testArrayConstantWithGlobalVariableAndMemoryOperations() {
        val module = Module("ArrayConstantIntegrationTest")
        val builder = IRBuilder(module)
        
        // Create an array constant for global initialization
        val globalArrayConstant = ArrayConstant.create(
            ArrayType(5, IntegerType.I32),
            IntConstant(10L, IntegerType.I32),
            IntConstant(20L, IntegerType.I32),
            IntConstant(30L, IntegerType.I32),
            IntConstant(40L, IntegerType.I32),
            IntConstant(50L, IntegerType.I32)
        )
        
        // Create a global variable with the array constant as initializer
        val globalArray = GlobalVariable.create(
            name = "globalArray",
            module = module,
            initializer = globalArrayConstant,
            isConstantValue = true
        )
        module.globalVariables.add(globalArray)
        
        // Create a function that accesses the global array
        val functionType = FunctionType(IntegerType.I32, emptyList())
        val mainFunction = builder.createFunction("main", functionType)
        module.functions.add(mainFunction)
        
        val entryBlock = mainFunction.insertBasicBlock("entry")
        builder.positionAtEnd(entryBlock)
        
        // Get pointer to the third element (index 2) using GetElementPtr
        val elementPtr = builder.insertGep(
            elementType = globalArrayConstant.type,
            address = globalArray,
            indices = listOf(IntConstant(0L, IntegerType.I64), IntConstant(2L, IntegerType.I32)),
            name = "element_ptr"
        )
        
        // Load the value from the array element
        val loadedValue = builder.insertLoad(IntegerType.I32, elementPtr, "element_value")
        
        // Return the loaded value
        builder.insertRet(loadedValue)
        
        // Verify the IR output
        val irPrinter = IRPrinter()
        val irOutput = irPrinter.print(module)
        
        // Check that the global array is correctly defined
        assertTrue(irOutput.contains("@globalArray"))
        assertTrue(irOutput.contains("constant"))
        assertTrue(irOutput.contains("[5 x i32]"))
        assertTrue(irOutput.contains("[i32 10, i32 20, i32 30, i32 40, i32 50]"))
        
        // Check that the GetElementPtr instruction is correctly generated
        assertTrue(irOutput.contains("getelementptr"))
        
        // Check that the load instruction is correctly generated
        assertTrue(irOutput.contains("load"))
        
        // Check that the return instruction is correctly generated
        assertTrue(irOutput.contains("ret i32"))
    }

    @Test
    @DisplayName("ArrayConstant should work with function calls and array parameters")
    fun testArrayConstantWithFunctionCalls() {
        val module = Module("ArrayConstantFunctionCallTest")
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
        
        // Store the entire array constant to memory in a single operation
        builder.insertStore(localArrayConstant, arrayAlloca)
        
        // Call the function with the array
        val callResult = builder.insertCall(arrayFunction, listOf(arrayAlloca), "call_result")
        
        // Return the result
        builder.insertRet(callResult)
        
        // Verify the IR output
        val irPrinter = IRPrinter()
        val irOutput = irPrinter.print(module)
        
        // Check that the function definitions are correct
        assertTrue(irOutput.contains("define float @processArray"))
        assertTrue(irOutput.contains("define float @main"))
        
        // Check that the array constant is correctly defined
        assertTrue(irOutput.contains("[3 x float]"))
        assertTrue(irOutput.contains("[float 1.100000e+00, float 2.200000e+00, float 3.300000e+00]"))
        
        // Check that the function call is correctly generated
        assertTrue(irOutput.contains("call float @processArray"))
        
        // Check that memory operations are correctly generated
        assertTrue(irOutput.contains("alloca"))
        assertTrue(irOutput.contains("store"))
    }

    @Test
    @DisplayName("ArrayConstant should work with nested arrays and complex operations")
    fun testArrayConstantWithNestedArrays() {
        val module = Module("ArrayConstantNestedArrayTest")
        val builder = IRBuilder(module)
        
        // Create nested array types
        val innerArrayType = ArrayType(2, IntegerType.I32)
        val outerArrayType = ArrayType(3, innerArrayType)
        
        // Create nested array constants
        val innerArray1 = ArrayConstant.create(
            innerArrayType,
            IntConstant(1L, IntegerType.I32),
            IntConstant(2L, IntegerType.I32)
        )
        
        val innerArray2 = ArrayConstant.create(
            innerArrayType,
            IntConstant(3L, IntegerType.I32),
            IntConstant(4L, IntegerType.I32)
        )
        
        val outerArray = ArrayConstant.create(
            outerArrayType,
            innerArray1,
            innerArray2,
            innerArray1
        )
        
        // Create a global variable with the nested array
        val globalNestedArray = GlobalVariable.create(
            name = "globalNestedArray",
            module = module,
            initializer = outerArray,
            isConstantValue = true
        )
        module.globalVariables.add(globalNestedArray)
        
        // Create a function that accesses nested array elements
        val functionType = FunctionType(IntegerType.I32, emptyList())
        val mainFunction = builder.createFunction("main", functionType)
        module.functions.add(mainFunction)
        
        val entryBlock = mainFunction.insertBasicBlock("entry")
        builder.positionAtEnd(entryBlock)
        
        // Get pointer to the second inner array (index 1)
        val innerArrayPtr = builder.insertGep(
            elementType = outerArrayType,
            address = globalNestedArray,
            indices = listOf(IntConstant(0L, IntegerType.I64), IntConstant(1L, IntegerType.I32)),
            name = "inner_array_ptr"
        )
        
        // Get pointer to the first element of the second inner array (index 0)
        val elementPtr = builder.insertGep(
            elementType = innerArrayType,
            address = innerArrayPtr,
            indices = listOf(IntConstant(0L, IntegerType.I64), IntConstant(0L, IntegerType.I32)),
            name = "element_ptr"
        )
        
        // Load the value
        val loadedValue = builder.insertLoad(IntegerType.I32, elementPtr, "element_value")
        
        // Return the loaded value
        builder.insertRet(loadedValue)
        
        // Verify the IR output
        val irPrinter = IRPrinter()
        val irOutput = irPrinter.print(module)
        
        // Check that the nested array is correctly defined
        assertTrue(irOutput.contains("@globalNestedArray"))
        assertTrue(irOutput.contains("constant"))
        assertTrue(irOutput.contains("[3 x [2 x i32]]"))
        assertTrue(irOutput.contains("[2 x i32] [i32 1, i32 2]"))
        assertTrue(irOutput.contains("[2 x i32] [i32 3, i32 4]"))
        
        // Check that the GetElementPtr instructions are correctly generated
        assertTrue(irOutput.contains("getelementptr"))
    }

    @Test
    @DisplayName("ArrayConstant should work with zero-initialized arrays and different element types")
    fun testArrayConstantWithZeroInitializationAndDifferentTypes() {
        val module = Module("ArrayConstantZeroInitTest")
        val builder = IRBuilder(module)
        
        // Create zero-initialized arrays of different types
        val intArrayZero = ArrayConstant.createZero(ArrayType(4, IntegerType.I32))
        val floatArrayZero = ArrayConstant.createZero(ArrayType(3, FloatingPointType.FloatType))
        val pointerArrayZero = ArrayConstant.createZero(ArrayType(2, PointerType))
        
        // Create global variables with zero-initialized arrays
        val globalIntArray = GlobalVariable.create(
            name = "globalIntArray",
            module = module,
            initializer = intArrayZero,
            isConstantValue = true
        )
        module.globalVariables.add(globalIntArray)
        
        val globalFloatArray = GlobalVariable.create(
            name = "globalFloatArray",
            module = module,
            initializer = floatArrayZero,
            isConstantValue = true
        )
        module.globalVariables.add(globalFloatArray)
        
        val globalPointerArray = GlobalVariable.create(
            name = "globalPointerArray",
            module = module,
            initializer = pointerArrayZero,
            isConstantValue = true
        )
        module.globalVariables.add(globalPointerArray)
        
        // Create a function that uses these arrays
        val functionType = FunctionType(IntegerType.I32, emptyList())
        val mainFunction = builder.createFunction("main", functionType)
        module.functions.add(mainFunction)
        
        val entryBlock = mainFunction.insertBasicBlock("entry")
        builder.positionAtEnd(entryBlock)
        
        // Load the first element from each array
        val intElementPtr = builder.insertGep(
            elementType = intArrayZero.type,
            address = globalIntArray,
            indices = listOf(IntConstant(0L, IntegerType.I64), IntConstant(0L, IntegerType.I32)),
            name = "int_element_ptr"
        )
        val intElement = builder.insertLoad(IntegerType.I32, intElementPtr, "int_element")
        
        val floatElementPtr = builder.insertGep(
            elementType = floatArrayZero.type,
            address = globalFloatArray,
            indices = listOf(IntConstant(0L, IntegerType.I64), IntConstant(0L, IntegerType.I32)),
            name = "float_element_ptr"
        )
        val floatElement = builder.insertLoad(FloatingPointType.FloatType, floatElementPtr, "float_element")
        
        val pointerElementPtr = builder.insertGep(
            elementType = pointerArrayZero.type,
            address = globalPointerArray,
            indices = listOf(IntConstant(0L, IntegerType.I64), IntConstant(0L, IntegerType.I32)),
            name = "pointer_element_ptr"
        )
        val pointerElement = builder.insertLoad(PointerType, pointerElementPtr, "pointer_element")
        
        // For this test, we'll just return the int element
        // In a real implementation, you would use FPToSI and PtrToInt casts
        builder.insertRet(intElement)
        
        // Verify the IR output
        val irPrinter = IRPrinter()
        val irOutput = irPrinter.print(module)
        
        // Check that the zero-initialized arrays are correctly defined
        assertTrue(irOutput.contains("@globalIntArray"))
        assertTrue(irOutput.contains("[i32 0, i32 0, i32 0, i32 0]"))
        assertTrue(irOutput.contains("@globalFloatArray"))
        assertTrue(irOutput.contains("@globalPointerArray"))
        
        // Check that the load instructions are correctly generated
        assertTrue(irOutput.contains("load i32"))
        assertTrue(irOutput.contains("load float"))
        assertTrue(irOutput.contains("load ptr"))
        
        // Note: FPToSI and PtrToInt instructions are not implemented yet
        // In a real implementation, these would be used for type conversions
    }
}