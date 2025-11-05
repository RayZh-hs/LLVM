package space.norb.llvm.structure

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.BeforeEach
import space.norb.llvm.types.IntegerType
import space.norb.llvm.types.FunctionType
import space.norb.llvm.values.constants.IntConstant
import space.norb.llvm.values.globals.GlobalVariable
import space.norb.llvm.enums.LinkageType
import space.norb.llvm.values.Metadata
import space.norb.llvm.values.MDString

/**
 * Unit tests for Module.
 */
@DisplayName("Module Tests")
class ModuleTest {
    
    private lateinit var module: Module
    
    @BeforeEach
    fun setUp() {
        module = Module("testModule")
    }
    
    @Test
    @DisplayName("Module should be created with correct name")
    fun testModuleCreation() {
        val name = "testModule"
        val testModule = Module(name)
        
        assertEquals(name, testModule.name, "Module should have the correct name")
        assertTrue(testModule.functions.isEmpty(), "New module should have no functions")
        assertTrue(testModule.globalVariables.isEmpty(), "New module should have no global variables")
        assertTrue(testModule.namedMetadata.isEmpty(), "New module should have no named metadata")
        assertNull(testModule.targetTriple, "New module should have no target triple")
        assertNull(testModule.dataLayout, "New module should have no data layout")
    }
    
    @Test
    @DisplayName("Module should handle functions correctly")
    fun testModuleFunctions() {
        assertTrue(module.functions.isEmpty(), "Module should start with no functions")
        
        val functionType = FunctionType(IntegerType.I32, listOf(IntegerType.I32))
        val function = Function("testFunction", functionType, module)
        
        module.functions.add(function)
        
        assertEquals(1, module.functions.size, "Module should have one function")
        assertEquals(function, module.functions[0], "Module should contain the added function")
        assertEquals(module, function.module, "Function should reference the module")
    }
    
    @Test
    @DisplayName("Module should handle multiple functions correctly")
    fun testModuleMultipleFunctions() {
        val functionType1 = FunctionType(IntegerType.I32, listOf(IntegerType.I32))
        val functionType2 = FunctionType(IntegerType.I64, listOf(IntegerType.I64))
        
        val function1 = Function("function1", functionType1, module)
        val function2 = Function("function2", functionType2, module)
        
        module.functions.addAll(listOf(function1, function2))
        
        assertEquals(2, module.functions.size, "Module should have two functions")
        assertTrue(module.functions.contains(function1), "Module should contain function1")
        assertTrue(module.functions.contains(function2), "Module should contain function2")
    }
    
    @Test
    @DisplayName("Module should handle global variables correctly")
    fun testModuleGlobalVariables() {
        assertTrue(module.globalVariables.isEmpty(), "Module should start with no global variables")
        
        val initializer = IntConstant(42, IntegerType.I32)
        val globalVariable = GlobalVariable.create("testGlobal", module, initializer)
        
        module.globalVariables.add(globalVariable)
        
        assertEquals(1, module.globalVariables.size, "Module should have one global variable")
        assertEquals(globalVariable, module.globalVariables[0], "Module should contain the added global variable")
        assertEquals(module, globalVariable.module, "Global variable should reference the module")
    }
    
    @Test
    @DisplayName("Module should handle multiple global variables correctly")
    fun testModuleMultipleGlobalVariables() {
        val initializer1 = IntConstant(42, IntegerType.I32)
        val initializer2 = IntConstant(100, IntegerType.I64)
        
        val globalVariable1 = GlobalVariable.create("global1", module, initializer1)
        val globalVariable2 = GlobalVariable.create("global2", module, initializer2)
        
        module.globalVariables.addAll(listOf(globalVariable1, globalVariable2))
        
        assertEquals(2, module.globalVariables.size, "Module should have two global variables")
        assertTrue(module.globalVariables.contains(globalVariable1), "Module should contain globalVariable1")
        assertTrue(module.globalVariables.contains(globalVariable2), "Module should contain globalVariable2")
    }
    
    @Test
    @DisplayName("Module should handle named metadata correctly")
    fun testModuleNamedMetadata() {
        assertTrue(module.namedMetadata.isEmpty(), "Module should start with no named metadata")
        
        val metadata = MDString("testMetadata")
        module.namedMetadata["testKey"] = metadata
        
        assertEquals(1, module.namedMetadata.size, "Module should have one named metadata")
        assertEquals(metadata, module.namedMetadata["testKey"], "Module should contain the added metadata")
    }
    
    @Test
    @DisplayName("Module should handle multiple named metadata correctly")
    fun testModuleMultipleNamedMetadata() {
        val metadata1 = MDString("metadata1")
        val metadata2 = MDString("metadata2")
        
        module.namedMetadata["key1"] = metadata1
        module.namedMetadata["key2"] = metadata2
        
        assertEquals(2, module.namedMetadata.size, "Module should have two named metadata")
        assertEquals(metadata1, module.namedMetadata["key1"], "Module should contain metadata1")
        assertEquals(metadata2, module.namedMetadata["key2"], "Module should contain metadata2")
    }
    
    @Test
    @DisplayName("Module should handle target triple correctly")
    fun testModuleTargetTriple() {
        assertNull(module.targetTriple, "Module should start with no target triple")
        
        val targetTriple = "x86_64-pc-linux-gnu"
        module.targetTriple = targetTriple
        
        assertEquals(targetTriple, module.targetTriple, "Module should have the correct target triple")
    }
    
    @Test
    @DisplayName("Module should handle data layout correctly")
    fun testModuleDataLayout() {
        assertNull(module.dataLayout, "Module should start with no data layout")
        
        val dataLayout = "e-m:e-i64:64-f80:128-n8:16:32:64-S128"
        module.dataLayout = dataLayout
        
        assertEquals(dataLayout, module.dataLayout, "Module should have the correct data layout")
    }
    
    @Test
    @DisplayName("Module should handle mixed content correctly")
    fun testModuleMixedContent() {
        // Add functions
        val functionType = FunctionType(IntegerType.I32, listOf(IntegerType.I32))
        val function = Function("testFunction", functionType, module)
        module.functions.add(function)
        
        // Add global variables
        val initializer = IntConstant(42, IntegerType.I32)
        val globalVariable = GlobalVariable.create("testGlobal", module, initializer)
        module.globalVariables.add(globalVariable)
        
        // Add named metadata
        val metadata = MDString("testMetadata")
        module.namedMetadata["testKey"] = metadata
        
        // Set target triple and data layout
        module.targetTriple = "x86_64-pc-linux-gnu"
        module.dataLayout = "e-m:e-i64:64-f80:128-n8:16:32:64-S128"
        
        // Verify all content is present
        assertEquals(1, module.functions.size, "Module should have one function")
        assertEquals(1, module.globalVariables.size, "Module should have one global variable")
        assertEquals(1, module.namedMetadata.size, "Module should have one named metadata")
        assertEquals("x86_64-pc-linux-gnu", module.targetTriple, "Module should have correct target triple")
        assertEquals("e-m:e-i64:64-f80:128-n8:16:32:64-S128", module.dataLayout, "Module should have correct data layout")
    }
    
    @Test
    @DisplayName("Module should handle empty name")
    fun testModuleEmptyName() {
        val emptyModule = Module("")
        assertEquals("", emptyModule.name, "Module should handle empty name")
    }
    
    @Test
    @DisplayName("Module should handle special characters in name")
    fun testModuleSpecialCharactersInName() {
        val specialName = "test-module_123"
        val specialModule = Module(specialName)
        assertEquals(specialName, specialModule.name, "Module should handle special characters in name")
    }
    
    @Test
    @DisplayName("Module should handle removal of functions")
    fun testModuleFunctionRemoval() {
        val functionType = FunctionType(IntegerType.I32, listOf(IntegerType.I32))
        val function = Function("testFunction", functionType, module)
        module.functions.add(function)
        
        assertEquals(1, module.functions.size, "Module should have one function")
        
        module.functions.remove(function)
        assertTrue(module.functions.isEmpty(), "Module should have no functions after removal")
    }
    
    @Test
    @DisplayName("Module should handle removal of global variables")
    fun testModuleGlobalVariableRemoval() {
        val initializer = IntConstant(42, IntegerType.I32)
        val globalVariable = GlobalVariable.create("testGlobal", module, initializer)
        module.globalVariables.add(globalVariable)
        
        assertEquals(1, module.globalVariables.size, "Module should have one global variable")
        
        module.globalVariables.remove(globalVariable)
        assertTrue(module.globalVariables.isEmpty(), "Module should have no global variables after removal")
    }
    
    @Test
    @DisplayName("Module should handle removal of named metadata")
    fun testModuleNamedMetadataRemoval() {
        val metadata = MDString("testMetadata")
        module.namedMetadata["testKey"] = metadata
        
        assertEquals(1, module.namedMetadata.size, "Module should have one named metadata")
        
        module.namedMetadata.remove("testKey")
        assertTrue(module.namedMetadata.isEmpty(), "Module should have no named metadata after removal")
    }
    
    @Test
    @DisplayName("Module should handle clearing of collections")
    fun testModuleClearCollections() {
        // Add some content
        val functionType = FunctionType(IntegerType.I32, listOf(IntegerType.I32))
        val function = Function("testFunction", functionType, module)
        module.functions.add(function)
        
        val initializer = IntConstant(42, IntegerType.I32)
        val globalVariable = GlobalVariable.create("testGlobal", module, initializer)
        module.globalVariables.add(globalVariable)
        
        val metadata = MDString("testMetadata")
        module.namedMetadata["testKey"] = metadata
        
        // Clear collections
        module.functions.clear()
        module.globalVariables.clear()
        module.namedMetadata.clear()
        
        assertTrue(module.functions.isEmpty(), "Functions should be cleared")
        assertTrue(module.globalVariables.isEmpty(), "Global variables should be cleared")
        assertTrue(module.namedMetadata.isEmpty(), "Named metadata should be cleared")
    }
    
    @Test
    @DisplayName("Module toString should contain name information")
    fun testModuleToString() {
        val toString = module.toString()
        assertTrue(toString.contains("testModule"), "toString should contain the module name")
    }
}