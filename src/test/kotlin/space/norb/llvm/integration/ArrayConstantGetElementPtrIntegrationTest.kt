package space.norb.llvm.integration

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import space.norb.llvm.structure.Module
import space.norb.llvm.structure.Function
import space.norb.llvm.structure.BasicBlock
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.ArrayType
import space.norb.llvm.types.IntegerType
import space.norb.llvm.types.PointerType
import space.norb.llvm.values.globals.GlobalVariable
import space.norb.llvm.values.constants.ArrayConstant
import space.norb.llvm.values.constants.IntConstant
import space.norb.llvm.values.constants.NullPointerConstant
import space.norb.llvm.instructions.memory.GetElementPtrInst
import space.norb.llvm.instructions.memory.LoadInst
import space.norb.llvm.builder.IRBuilder
import space.norb.llvm.visitors.IRPrinter

/**
 * Integration tests for ArrayConstant with GetElementPtr.
 */
@DisplayName("ArrayConstant GetElementPtr Integration Tests")
class ArrayConstantGetElementPtrIntegrationTest {

    @Test
    @DisplayName("GetElementPtr should work with global ArrayConstant")
    fun testGetElementPtrWithGlobalArrayConstant() {
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
        val functionType = FunctionType(IntegerType.I32, listOf(IntegerType.I32))
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
        
        // Verify IR output contains expected elements
        assertTrue(irOutput.contains("@global_array"))
        assertTrue(irOutput.contains("[5 x i32]"))
        assertTrue(irOutput.contains("[i32 10, i32 20, i32 30, i32 40, i32 50]"))
        assertTrue(irOutput.contains("getelementptr [5 x i32], ptr @global_array, i64 0, i32 %arg0"))
        assertTrue(irOutput.contains("load i32, ptr %element_ptr"))
    }

    @Test
    @DisplayName("GetElementPtr should work with nested ArrayConstant")
    fun testGetElementPtrWithNestedArrayConstant() {
        val module = Module("testModule")
        val builder = IRBuilder(module)
        
        // Create inner array constants
        val innerArray1 = ArrayConstant.create(IntegerType.I32,
            IntConstant(1L, IntegerType.I32),
            IntConstant(2L, IntegerType.I32)
        )
        
        val innerArray2 = ArrayConstant.create(IntegerType.I32,
            IntConstant(3L, IntegerType.I32),
            IntConstant(4L, IntegerType.I32)
        )
        
        // Create outer array constant: [2 x [2 x i32]]
        val outerArrayConstant = ArrayConstant.create(ArrayType(2, innerArray1.type), innerArray1, innerArray2)
        
        // Create global variable with nested array constant
        val globalNestedArray = GlobalVariable.create(
            name = "global_nested_array",
            module = module,
            initializer = outerArrayConstant,
            isConstantValue = true
        )
        module.globalVariables.add(globalNestedArray)
        
        // Create function that accesses nested array elements
        val functionType = FunctionType(IntegerType.I32, listOf(IntegerType.I32, IntegerType.I32))
        val function = builder.createFunction("get_nested_array_element", functionType)
        module.functions.add(function)
        
        val entryBlock = function.insertBasicBlock("entry")
        if (function.entryBlock == null) {
            function.entryBlock = entryBlock
        }
        builder.positionAtEnd(entryBlock)
        
        // Get function parameters (outer and inner indices)
        val outerIndex = function.parameters[0]
        val innerIndex = function.parameters[1]
        
        // Create GEP to access nested array element: array[outer][inner]
        val gep = builder.insertGep(
            elementType = outerArrayConstant.type,
            address = globalNestedArray,
            indices = listOf(IntConstant(0L, IntegerType.I64), outerIndex, innerIndex),
            name = "element_ptr"
        )
        
        // Load the element
        val load = builder.insertLoad(IntegerType.I32, gep, "element_value")
        
        // Return the loaded value
        builder.insertRet(load)
        
        // Print IR and verify
        val irPrinter = IRPrinter()
        val irOutput = irPrinter.print(module)
        
        // Verify IR output contains expected elements
        assertTrue(irOutput.contains("@global_nested_array"))
        assertTrue(irOutput.contains("[2 x [2 x i32]]"))
        assertTrue(irOutput.contains("[i32 1, i32 2]"))
        assertTrue(irOutput.contains("[i32 3, i32 4]"))
        assertTrue(irOutput.contains("getelementptr [2 x [2 x i32]], ptr @global_nested_array, i64 0, i32 %arg0, i32 %arg1"))
        assertTrue(irOutput.contains("load i32, ptr %element_ptr"))
    }

    @Test
    @DisplayName("GetElementPtr should work with constant indices on ArrayConstant")
    fun testGetElementPtrWithConstantIndices() {
        val module = Module("testModule")
        val builder = IRBuilder(module)
        
        // Create global array constant: [i32 100, i32 200, i32 300]
        val arrayConstant = ArrayConstant.create(IntegerType.I32,
            IntConstant(100L, IntegerType.I32),
            IntConstant(200L, IntegerType.I32),
            IntConstant(300L, IntegerType.I32)
        )
        
        // Create global variable with array constant
        val globalArray = GlobalVariable.create(
            name = "const_index_array",
            module = module,
            initializer = arrayConstant,
            isConstantValue = true
        )
        module.globalVariables.add(globalArray)
        
        // Create function that accesses specific array element with constant index
        val functionType = FunctionType(IntegerType.I32, emptyList())
        val function = builder.createFunction("get_second_element", functionType)
        module.functions.add(function)
        
        val entryBlock = function.insertBasicBlock("entry")
        if (function.entryBlock == null) {
            function.entryBlock = entryBlock
        }
        builder.positionAtEnd(entryBlock)
        
        // Create GEP with constant index to access array[1]
        val gep = builder.insertGep(
            elementType = arrayConstant.type,
            address = globalArray,
            indices = listOf(IntConstant(0L, IntegerType.I64), IntConstant(1L, IntegerType.I32)),
            name = "second_ptr"
        )
        
        // Load the element
        val load = builder.insertLoad(IntegerType.I32, gep, "second_value")
        
        // Return the loaded value
        builder.insertRet(load)
        
        // Print IR and verify
        val irPrinter = IRPrinter()
        val irOutput = irPrinter.print(module)
        
        // Verify IR output contains expected elements
        assertTrue(irOutput.contains("@const_index_array"))
        assertTrue(irOutput.contains("[3 x i32]"))
        assertTrue(irOutput.contains("[i32 100, i32 200, i32 300]"))
        assertTrue(irOutput.contains("getelementptr [3 x i32], ptr @const_index_array, i64 0, i32 1"))
        assertTrue(irOutput.contains("load i32, ptr %second_ptr"))
    }

    @Test
    @DisplayName("GetElementPtr should validate array bounds with ArrayConstant")
    fun testGetElementPtrArrayBoundsValidation() {
        val module = Module("testModule")
        
        // Create global array constant: [i32 1, i32 2, i32 3]
        val arrayConstant = ArrayConstant.create(IntegerType.I32,
            IntConstant(1L, IntegerType.I32),
            IntConstant(2L, IntegerType.I32),
            IntConstant(3L, IntegerType.I32)
        )
        
        // Create global variable with array constant
        val globalArray = GlobalVariable.create(
            name = "bounds_test_array",
            module = module,
            initializer = arrayConstant,
            isConstantValue = true
        )
        
        // Test that GEP can be created with valid indices
        val validGep = GetElementPtrInst.createArrayIndex(
            name = "valid_ptr",
            arrayType = arrayConstant.type,
            arrayPointer = globalArray,
            elementIndex = IntConstant(2L, IntegerType.I32) // Valid index (last element)
        )
        
        assertEquals(arrayConstant.type, validGep.elementType)
        assertEquals(globalArray, validGep.pointer)
        assertEquals(2, validGep.getNumIndices())
        assertEquals(IntegerType.I32, validGep.getFinalElementType())
        
        // Test that GEP can be created with out-of-bounds indices (LLVM allows this at compile time)
        val outOfBoundsGep = GetElementPtrInst.createArrayIndex(
            name = "oob_ptr",
            arrayType = arrayConstant.type,
            arrayPointer = globalArray,
            elementIndex = IntConstant(10L, IntegerType.I32) // Out of bounds index
        )
        
        assertEquals(arrayConstant.type, outOfBoundsGep.elementType)
        assertEquals(globalArray, outOfBoundsGep.pointer)
        assertEquals(2, outOfBoundsGep.getNumIndices())
        assertEquals(IntegerType.I32, outOfBoundsGep.getFinalElementType())
    }
}