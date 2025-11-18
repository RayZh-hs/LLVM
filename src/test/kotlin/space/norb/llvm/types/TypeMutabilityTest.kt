package space.norb.llvm.types

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName

/**
 * Unit tests for Type mutability.
 */
@DisplayName("Type Mutability Tests")
class TypeMutabilityTest {
    
    @Test
    @DisplayName("FunctionType should allow modification of returnType")
    fun testFunctionTypeReturnTypeMutability() {
        val functionType = FunctionType(IntegerType.I32, listOf(IntegerType.I32))
        assertEquals(IntegerType.I32, functionType.returnType, "Initial return type should be I32")
        
        // Modify return type
        functionType.returnType = IntegerType.I64
        assertEquals(IntegerType.I64, functionType.returnType, "Return type should be mutable")
    }
    
    @Test
    @DisplayName("FunctionType should allow modification of paramTypes")
    fun testFunctionTypeParamTypesMutability() {
        val functionType = FunctionType(IntegerType.I32, listOf(IntegerType.I32))
        assertEquals(listOf(IntegerType.I32), functionType.paramTypes, "Initial param types should be [I32]")
        
        // Modify parameter types
        functionType.paramTypes = listOf(IntegerType.I64, IntegerType.I32)
        assertEquals(listOf(IntegerType.I64, IntegerType.I32), functionType.paramTypes, "Parameter types should be mutable")
    }
    
    @Test
    @DisplayName("FunctionType should allow modification of isVarArg")
    fun testFunctionTypeIsVarArgMutability() {
        val functionType = FunctionType(IntegerType.I32, listOf(IntegerType.I32))
        assertEquals(false, functionType.isVarArg, "Initial isVarArg should be false")
        
        // Modify isVarArg
        functionType.isVarArg = true
        assertEquals(true, functionType.isVarArg, "isVarArg should be mutable")
    }
    
    @Test
    @DisplayName("FunctionType should allow modification of paramNames")
    fun testFunctionTypeParamNamesMutability() {
        val functionType = FunctionType(IntegerType.I32, listOf(IntegerType.I32))
        assertNull(functionType.paramNames, "Initial paramNames should be null")
        
        // Modify parameter names
        functionType.paramNames = listOf("a", "b")
        assertEquals(listOf("a", "b"), functionType.paramNames, "Parameter names should be mutable")
    }
    
    @Test
    @DisplayName("ArrayType should allow modification of numElements")
    fun testArrayTypeNumElementsMutability() {
        val arrayType = ArrayType(5, IntegerType.I32)
        assertEquals(5, arrayType.numElements, "Initial numElements should be 5")
        
        // Modify numElements
        arrayType.numElements = 10
        assertEquals(10, arrayType.numElements, "numElements should be mutable")
    }
    
    @Test
    @DisplayName("ArrayType should allow modification of elementType")
    fun testArrayTypeElementTypeMutability() {
        val arrayType = ArrayType(5, IntegerType.I32)
        assertEquals(IntegerType.I32, arrayType.elementType, "Initial elementType should be I32")
        
        // Modify elementType
        arrayType.elementType = IntegerType.I64
        assertEquals(IntegerType.I64, arrayType.elementType, "elementType should be mutable")
    }
    
    @Test
    @DisplayName("Modified FunctionType should reflect changes in toString")
    fun testFunctionTypeToStringAfterModification() {
        val functionType = FunctionType(IntegerType.I32, listOf(IntegerType.I32))
        val originalString = functionType.toString()
        
        // Modify the function type
        functionType.returnType = IntegerType.I64
        functionType.paramTypes = listOf(IntegerType.I64, IntegerType.I32)
        functionType.isVarArg = true
        
        val modifiedString = functionType.toString()
        assertNotEquals(originalString, modifiedString, "toString should reflect changes")
        assertTrue(modifiedString.contains("i64"), "toString should contain new return type")
        assertTrue(modifiedString.contains("i64, i32"), "toString should contain new param types")
        assertTrue(modifiedString.contains("..."), "toString should contain vararg indicator")
    }
    
    @Test
    @DisplayName("Modified ArrayType should reflect changes in toString")
    fun testArrayTypeToStringAfterModification() {
        val arrayType = ArrayType(5, IntegerType.I32)
        val originalString = arrayType.toString()
        
        // Modify the array type
        arrayType.numElements = 10
        arrayType.elementType = IntegerType.I64
        
        val modifiedString = arrayType.toString()
        assertNotEquals(originalString, modifiedString, "toString should reflect changes")
        assertTrue(modifiedString.contains("10"), "toString should contain new numElements")
        assertTrue(modifiedString.contains("i64"), "toString should contain new elementType")
    }
}