package space.norb.llvm.instructions.memory

import space.norb.llvm.core.Type
import space.norb.llvm.types.PointerType
import space.norb.llvm.types.ArrayType
import space.norb.llvm.types.StructType
import space.norb.llvm.types.IntegerType
import space.norb.llvm.values.constants.IntConstant
import space.norb.llvm.values.constants.NullPointerConstant
import space.norb.llvm.visitors.IRPrinter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertFailsWith

/**
 * Test cases for GetElementPtrInst instruction.
 */
class GetElementPtrInstTest {
    
    @Test
    fun testBasicArrayGEP() {
        val arrayType = ArrayType(10, IntegerType.I32)
        val arrayPtr = NullPointerConstant.create(arrayType)
        val indices = listOf(
            IntConstant(0L, IntegerType.I64), // Select the array
            IntConstant(5L, IntegerType.I64)  // Select element 5
        )
        
        val gep = GetElementPtrInst("gep", arrayType, arrayPtr, indices)
        
        assertEquals("gep", gep.name)
        assertEquals(arrayType, gep.elementType)
        assertEquals(arrayPtr, gep.pointer)
        assertEquals(indices, gep.indices)
        assertEquals(PointerType, gep.resultType)
        assertFalse(gep.isInBounds)
        assertEquals(2, gep.getNumIndices())
        assertEquals(indices[0], gep.getIndex(0))
        assertEquals(indices[1], gep.getIndex(1))
    }
    
    @Test
    fun testInBoundsGEP() {
        val arrayType = ArrayType(10, IntegerType.I32)
        val arrayPtr = NullPointerConstant.create(arrayType)
        val indices = listOf(
            IntConstant(0L, IntegerType.I64),
            IntConstant(5L, IntegerType.I64)
        )
        
        val gep = GetElementPtrInst.createInBounds("gep", arrayType, arrayPtr, indices)
        
        assertTrue(gep.isInBounds)
    }
    
    @Test
    fun testStructGEP() {
        val structType = StructType(listOf(IntegerType.I32, IntegerType.I64))
        val structPtr = NullPointerConstant.create(structType)
        val indices = listOf(
            IntConstant(0L, IntegerType.I64), // Select the struct
            IntConstant(1L, IntegerType.I32)  // Select field 1
        )
        
        val gep = GetElementPtrInst("field", structType, structPtr, indices)
        
        assertEquals(structType, gep.elementType)
        assertEquals(IntegerType.I64, gep.getFinalElementType())
    }
    
    @Test
    fun testNestedArrayGEP() {
        val innerArray = ArrayType(5, IntegerType.I32)
        val outerArray = ArrayType(10, innerArray)
        val arrayPtr = NullPointerConstant.create(outerArray)
        val indices = listOf(
            IntConstant(0L, IntegerType.I64), // Select outer array
            IntConstant(3L, IntegerType.I64), // Select inner array at index 3
            IntConstant(2L, IntegerType.I64)  // Select element 2 of inner array
        )
        
        val gep = GetElementPtrInst("nested", outerArray, arrayPtr, indices)
        
        assertEquals(IntegerType.I32, gep.getFinalElementType())
    }
    
    @Test
    fun testFactoryMethods() {
        val arrayType = ArrayType(10, IntegerType.I32)
        val arrayPtr = NullPointerConstant.create(arrayType)
        val elementIndex = IntConstant(5L, IntegerType.I64)
        
        // Test array indexing factory method
        val arrayGep = GetElementPtrInst.createArrayIndex("elem", arrayType, arrayPtr, elementIndex)
        assertEquals(2, arrayGep.getNumIndices())
        assertEquals(IntegerType.I32, arrayGep.getFinalElementType())
        
        // Test struct field factory method
        val structType = StructType(listOf(IntegerType.I32, IntegerType.I64))
        val structPtr = NullPointerConstant.create(structType)
        val structGep = GetElementPtrInst.createStructField("field", structType, structPtr, 1)
        assertEquals(2, structGep.getNumIndices())
        assertEquals(IntegerType.I64, structGep.getFinalElementType())
        assertTrue(structGep.isInBounds)
    }
    
    @Test
    fun testValidation() {
        val arrayType = ArrayType(10, IntegerType.I32)
        val validPtr = NullPointerConstant.create(arrayType)
        val validIndices = listOf(IntConstant(0L, IntegerType.I64))
        
        // Test valid GEP
        GetElementPtrInst("valid", arrayType, validPtr, validIndices)
        
        // Test invalid pointer type
        val invalidPtr = IntConstant(0L, IntegerType.I32)
        assertFailsWith<IllegalArgumentException> {
            GetElementPtrInst("invalid", arrayType, invalidPtr, validIndices)
        }
        
        // Test empty indices
        assertFailsWith<IllegalArgumentException> {
            GetElementPtrInst("empty", arrayType, validPtr, emptyList())
        }
        
        // Test non-integer index
        val nonIntIndex = NullPointerConstant.create(IntegerType.I32)
        assertFailsWith<IllegalArgumentException> {
            GetElementPtrInst("nonint", arrayType, validPtr, listOf(nonIntIndex))
        }
    }
    
    @Test
    fun testMemoryInstMethods() {
        val arrayType = ArrayType(10, IntegerType.I32)
        val arrayPtr = NullPointerConstant.create(arrayType)
        val indices = listOf(IntConstant(0L, IntegerType.I64))
        
        val gep = GetElementPtrInst("gep", arrayType, arrayPtr, indices)
        
        // Test pointer operands
        val pointerOps = gep.getPointerOperands()
        assertEquals(1, pointerOps.size)
        assertEquals(arrayPtr, pointerOps[0])
        
        // GEP doesn't read or write memory directly
        assertFalse(gep.mayReadFromMemory())
        assertFalse(gep.mayWriteToMemory())
        assertFalse(gep.isVolatile())
    }
    
    @Test
    fun testIRPrinting() {
        val arrayType = ArrayType(10, IntegerType.I32)
        val arrayPtr = NullPointerConstant.create(arrayType)
        val indices = listOf(
            IntConstant(0L, IntegerType.I64),
            IntConstant(5L, IntegerType.I64)
        )
        
        val gep = GetElementPtrInst("gep", arrayType, arrayPtr, indices)
        val printer = IRPrinter()
        
        // This should not throw an exception
        gep.accept(printer)
    }
    
    @Test
    fun testStructFieldIndexExtraction() {
        val structType = StructType(listOf(IntegerType.I32, IntegerType.I64))
        val structPtr = NullPointerConstant.create(structType)
        
        // Test valid field indices
        for (i in 0..1) {
            val indices = listOf(
                IntConstant(0L, IntegerType.I64),
                IntConstant(i.toLong(), IntegerType.I32)
            )
            val gep = GetElementPtrInst("field$i", structType, structPtr, indices)
            assertEquals(structType.elementTypes[i], gep.getFinalElementType())
        }
        
        // Test out of bounds field index
        val outOfBoundsIndices = listOf(
            IntConstant(0L, IntegerType.I64),
            IntConstant(5L, IntegerType.I32) // Field 5 doesn't exist
        )
        assertFailsWith<IllegalArgumentException> {
            GetElementPtrInst("invalid", structType, structPtr, outOfBoundsIndices)
        }
    }
    
    @Test
    fun testFactoryMethodValidation() {
        val arrayType = ArrayType(10, IntegerType.I32)
        val arrayPtr = NullPointerConstant.create(arrayType)
        val structType = StructType(listOf(IntegerType.I32))
        val structPtr = NullPointerConstant.create(structType)
        
        // Test array indexing factory with valid parameters
        val validElementIndex = IntConstant(5L, IntegerType.I64)
        GetElementPtrInst.createArrayIndex("valid", arrayType, arrayPtr, validElementIndex)
        
        // Test struct field factory with invalid field index
        assertFailsWith<IllegalArgumentException> {
            GetElementPtrInst.createStructField("invalid", structType, structPtr, 5)
        }
    }
}