package space.norb.llvm.values.globals

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.BeforeEach
import space.norb.llvm.structure.Module
import space.norb.llvm.types.IntegerType
import space.norb.llvm.types.PointerType
import space.norb.llvm.values.constants.IntConstant
import space.norb.llvm.values.constants.NullPointerConstant
import space.norb.llvm.enums.LinkageType

/**
 * Unit tests for GlobalVariable.
 */
@DisplayName("GlobalVariable Tests")
class GlobalVariableTest {
    
    private lateinit var module: Module
    
    @BeforeEach
    fun setUp() {
        module = Module("testModule")
    }
    
    @Test
    @DisplayName("GlobalVariable should be created with correct properties")
    fun testGlobalVariableCreation() {
        val name = "testGlobal"
        val initializer = IntConstant(42, IntegerType.I32)
        val globalVariable = GlobalVariable.create(name, module, initializer)
        
        assertEquals(name, globalVariable.name, "GlobalVariable should have the correct name")
        assertEquals(PointerType, globalVariable.type, "GlobalVariable should have PointerType")
        assertEquals(module, globalVariable.module, "GlobalVariable should reference the module")
        assertEquals(initializer, globalVariable.initializer, "GlobalVariable should have the correct initializer")
        assertEquals(IntegerType.I32, globalVariable.elementType, "GlobalVariable should have the correct element type")
        assertFalse(globalVariable.isConstantValue, "GlobalVariable should not be constant by default")
        assertEquals(LinkageType.EXTERNAL, globalVariable.linkage, "GlobalVariable should have EXTERNAL linkage by default")
    }
    
    @Test
    @DisplayName("GlobalVariable should be created without initializer")
    fun testGlobalVariableCreationWithoutInitializer() {
        val name = "testGlobal"
        val globalVariable = GlobalVariable.create(name, module)
        
        assertEquals(name, globalVariable.name, "GlobalVariable should have the correct name")
        assertEquals(PointerType, globalVariable.type, "GlobalVariable should have PointerType")
        assertEquals(module, globalVariable.module, "GlobalVariable should reference the module")
        assertNull(globalVariable.initializer, "GlobalVariable should have no initializer")
        assertNull(globalVariable.elementType, "GlobalVariable should have no element type without initializer")
        assertFalse(globalVariable.isConstantValue, "GlobalVariable should not be constant by default")
        assertEquals(LinkageType.EXTERNAL, globalVariable.linkage, "GlobalVariable should have EXTERNAL linkage by default")
    }
    
    @Test
    @DisplayName("GlobalVariable should be created with explicit element type")
    fun testGlobalVariableCreationWithElementType() {
        val name = "testGlobal"
        val elementType = IntegerType.I64
        val globalVariable = GlobalVariable.createWithElementType(name, elementType, module)
        
        assertEquals(name, globalVariable.name, "GlobalVariable should have the correct name")
        assertEquals(PointerType, globalVariable.type, "GlobalVariable should have PointerType")
        assertEquals(module, globalVariable.module, "GlobalVariable should reference the module")
        assertNull(globalVariable.initializer, "GlobalVariable should have no initializer")
        assertEquals(elementType, globalVariable.elementType, "GlobalVariable should have the correct element type")
        assertFalse(globalVariable.isConstantValue, "GlobalVariable should not be constant by default")
        assertEquals(LinkageType.EXTERNAL, globalVariable.linkage, "GlobalVariable should have EXTERNAL linkage by default")
    }
    
    @Test
    @DisplayName("GlobalVariable should be created with constant flag")
    fun testGlobalVariableCreationWithConstantFlag() {
        val name = "testGlobal"
        val initializer = IntConstant(42, IntegerType.I32)
        val globalVariable = GlobalVariable.create(name, module, initializer, isConstantValue = true)
        
        assertTrue(globalVariable.isConstantValue, "GlobalVariable should be constant when specified")
        assertTrue(globalVariable.isConstant(), "GlobalVariable should be constant when specified")
    }
    
    @Test
    @DisplayName("GlobalVariable should be created with custom linkage")
    fun testGlobalVariableCreationWithCustomLinkage() {
        val name = "testGlobal"
        val initializer = IntConstant(42, IntegerType.I32)
        val linkage = LinkageType.INTERNAL
        val globalVariable = GlobalVariable.create(name, module, initializer, linkage = linkage)
        
        assertEquals(linkage, globalVariable.linkage, "GlobalVariable should have the specified linkage")
    }
    
    @Test
    @DisplayName("GlobalVariable should handle hasInitializer correctly")
    fun testGlobalVariableHasInitializer() {
        val name = "testGlobal"
        val initializer = IntConstant(42, IntegerType.I32)
        
        val globalWithInitializer = GlobalVariable.create(name, module, initializer)
        assertTrue(globalWithInitializer.hasInitializer(), "GlobalVariable with initializer should return true for hasInitializer")
        
        val globalWithoutInitializer = GlobalVariable.create(name, module)
        assertFalse(globalWithoutInitializer.hasInitializer(), "GlobalVariable without initializer should return false for hasInitializer")
    }
    
    @Test
    @DisplayName("GlobalVariable should handle isConstant correctly")
    fun testGlobalVariableIsConstant() {
        val name = "testGlobal"
        val initializer = IntConstant(42, IntegerType.I32)
        
        val constantGlobal = GlobalVariable.create(name, module, initializer, isConstantValue = true)
        assertTrue(constantGlobal.isConstant(), "Constant global variable should return true for isConstant")
        
        val variableGlobal = GlobalVariable.create(name, module, initializer, isConstantValue = false)
        assertFalse(variableGlobal.isConstant(), "Variable global variable should return false for isConstant")
    }
    
    @Test
    @DisplayName("GlobalVariable should work with different initializer types")
    fun testGlobalVariableWithDifferentInitializerTypes() {
        val i8Initializer = IntConstant(8, IntegerType.I8)
        val i8Global = GlobalVariable.create("i8Global", module, i8Initializer)
        assertEquals(IntegerType.I8, i8Global.elementType)
        
        val i32Initializer = IntConstant(32, IntegerType.I32)
        val i32Global = GlobalVariable.create("i32Global", module, i32Initializer)
        assertEquals(IntegerType.I32, i32Global.elementType)
        
        val i64Initializer = IntConstant(64, IntegerType.I64)
        val i64Global = GlobalVariable.create("i64Global", module, i64Initializer)
        assertEquals(IntegerType.I64, i64Global.elementType)
    }
    
    @Test
    @DisplayName("GlobalVariable should work with null pointer initializer")
    fun testGlobalVariableWithNullPointerInitializer() {
        val name = "testGlobal"
        val elementType = IntegerType.I32
        val nullInitializer = NullPointerConstant.create(elementType)
        val globalVariable = GlobalVariable.create(name, module, nullInitializer)
        
        assertEquals(nullInitializer, globalVariable.initializer, "GlobalVariable should have null pointer initializer")
        assertEquals(elementType, globalVariable.elementType, "GlobalVariable should have correct element type")
    }
    
    @Test
    @DisplayName("GlobalVariable should work with all linkage types")
    fun testGlobalVariableWithAllLinkageTypes() {
        val name = "testGlobal"
        val initializer = IntConstant(42, IntegerType.I32)
        
        LinkageType.values().forEach { linkageType ->
            val globalVariable = GlobalVariable.create(name + "_" + linkageType.name, module, initializer, linkage = linkageType)
            assertEquals(linkageType, globalVariable.linkage, "GlobalVariable should have correct linkage type: $linkageType")
        }
    }
    
    @Test
    @DisplayName("GlobalVariable should implement Constant interface correctly")
    fun testGlobalVariableImplementsConstant() {
        val name = "testGlobal"
        val initializer = IntConstant(42, IntegerType.I32)
        val globalVariable = GlobalVariable.create(name, module, initializer)
        
        // Should be assignable to Constant
        val constant: space.norb.llvm.core.Constant = globalVariable
        assertEquals(name, constant.name)
        assertEquals(PointerType, constant.type)
        
        // Should be assignable to Value
        val value: space.norb.llvm.core.Value = globalVariable
        assertEquals(name, value.name)
        assertEquals(PointerType, value.type)
    }
    
    @Test
    @DisplayName("GlobalVariable equality should work correctly")
    fun testGlobalVariableEquality() {
        val initializer = IntConstant(42, IntegerType.I32)
        
        val global1 = GlobalVariable.create("sameName", module, initializer)
        val global2 = GlobalVariable.create("sameName", module, initializer)
        val global3 = GlobalVariable.create("differentName", module, initializer)
        
        assertEquals(global1, global2, "GlobalVariables with same properties should be equal")
        assertEquals(global1.hashCode(), global2.hashCode(), "Equal GlobalVariables should have same hash code")
        
        assertNotEquals(global1, global3, "GlobalVariables with different names should not be equal")
    }
    
    @Test
    @DisplayName("GlobalVariable toString should contain name and type information")
    fun testGlobalVariableToString() {
        val name = "testGlobal"
        val initializer = IntConstant(42, IntegerType.I32)
        val globalVariable = GlobalVariable.create(name, module, initializer)
        
        val toString = globalVariable.toString()
        assertTrue(toString.contains(name), "toString should contain the global variable name")
        assertTrue(toString.contains(PointerType.toString()), "toString should contain the type")
    }
    
    @Test
    @DisplayName("GlobalVariable should handle empty name")
    fun testGlobalVariableEmptyName() {
        val name = ""
        val initializer = IntConstant(42, IntegerType.I32)
        val globalVariable = GlobalVariable.create(name, module, initializer)
        
        assertEquals(name, globalVariable.name, "GlobalVariable should handle empty name")
    }
    
    @Test
    @DisplayName("GlobalVariable should handle special characters in name")
    fun testGlobalVariableSpecialCharactersInName() {
        val name = "test-global_123"
        val initializer = IntConstant(42, IntegerType.I32)
        val globalVariable = GlobalVariable.create(name, module, initializer)
        
        assertEquals(name, globalVariable.name, "GlobalVariable should handle special characters in name")
    }
    
    @Test
    @DisplayName("GlobalVariable should maintain type information")
    fun testGlobalVariableTypeInformation() {
        val globalVariable = GlobalVariable.create("testGlobal", module)
        
        assertTrue(globalVariable.type.isPointerType(), "GlobalVariable type should be a pointer type")
        assertFalse(globalVariable.type.isIntegerType(), "GlobalVariable type should not be an integer type")
        assertFalse(globalVariable.type.isFloatingPointType(), "GlobalVariable type should not be a floating-point type")
    }
    
    @Test
    @DisplayName("GlobalVariable should work with createWithElementType and initializer")
    fun testGlobalVariableCreateWithElementTypeAndInitializer() {
        val name = "testGlobal"
        val elementType = IntegerType.I64
        val initializer = IntConstant(100, IntegerType.I32) // Different type from element type
        val globalVariable = GlobalVariable.createWithElementType(name, elementType, module, initializer)
        
        assertEquals(elementType, globalVariable.elementType, "GlobalVariable should have the specified element type")
        assertEquals(initializer, globalVariable.initializer, "GlobalVariable should have the specified initializer")
    }
    
    @Test
    @DisplayName("GlobalVariable should work with createWithElementType and constant flag")
    fun testGlobalVariableCreateWithElementTypeAndConstantFlag() {
        val name = "testGlobal"
        val elementType = IntegerType.I64
        val globalVariable = GlobalVariable.createWithElementType(name, elementType, module, isConstantValue = true)
        
        assertEquals(elementType, globalVariable.elementType, "GlobalVariable should have the specified element type")
        assertTrue(globalVariable.isConstantValue, "GlobalVariable should be constant when specified")
        assertTrue(globalVariable.isConstant(), "GlobalVariable should be constant when specified")
    }
    
    @Test
    @DisplayName("GlobalVariable should work with createWithElementType and custom linkage")
    fun testGlobalVariableCreateWithElementTypeAndCustomLinkage() {
        val name = "testGlobal"
        val elementType = IntegerType.I64
        val linkage = LinkageType.PRIVATE
        val globalVariable = GlobalVariable.createWithElementType(name, elementType, module, linkage = linkage)
        
        assertEquals(elementType, globalVariable.elementType, "GlobalVariable should have the specified element type")
        assertEquals(linkage, globalVariable.linkage, "GlobalVariable should have the specified linkage")
    }
}