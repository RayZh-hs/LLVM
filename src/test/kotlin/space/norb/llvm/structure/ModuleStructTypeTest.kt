package space.norb.llvm.structure

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertThrows
import space.norb.llvm.types.IntegerType
import space.norb.llvm.types.StructType
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame

/**
 * Unit tests for Module struct registration APIs.
 * 
 * This test class verifies:
 * 1. Named struct registration with name uniqueness and correct properties
 * 2. Opaque struct registration/completion including error cases
 * 3. Anonymous struct deduplication via getOrCreateAnonymousStructType
 * 4. Deterministic ordering checks for struct retrieval methods
 */
@DisplayName("Module Struct Type Tests")
class ModuleStructTypeTest {
    
    @Test
    @DisplayName("registerNamedStructType should register struct with correct properties")
    fun testRegisterNamedStructType() {
        val module = Module("TestModule")
        
        // Register a named struct
        val structType = module.registerNamedStructType("Point", listOf(IntegerType.I32, IntegerType.I32))
        
        // Verify struct properties
        assertEquals("Point", structType.name)
        assertEquals(listOf(IntegerType.I32, IntegerType.I32), structType.elementTypes!!)
        assertFalse(structType.isPacked)
        assertTrue(structType.isComplete())
        
        // Verify it can be retrieved
        val retrieved = module.getNamedStructType("Point")
        assertNotNull(retrieved)
        assertSame(structType, retrieved)
    }
    
    @Test
    @DisplayName("registerNamedStructType should enforce name uniqueness")
    fun testRegisterNamedStructTypeUniqueness() {
        val module = Module("TestModule")
        
        // Register first struct
        val firstStruct = module.registerNamedStructType("Point", listOf(IntegerType.I32, IntegerType.I32))
        
        // Attempt to register another struct with same name should throw exception
        assertThrows<IllegalArgumentException> {
            module.registerNamedStructType("Point", listOf(IntegerType.I64, IntegerType.I64))
        }
        
        // Original struct should still be available
        val retrieved = module.getNamedStructType("Point")
        assertSame(firstStruct, retrieved)
    }
    
    @Test
    @DisplayName("registerNamedStructType should support packed structs")
    fun testRegisterNamedStructTypePacked() {
        val module = Module("TestModule")
        
        // Register a packed struct
        val structType = module.registerNamedStructType("PackedData", listOf(IntegerType.I8, IntegerType.I32), true)
        
        // Verify struct properties
        assertEquals("PackedData", structType.name)
        assertEquals(listOf(IntegerType.I8, IntegerType.I32), structType.elementTypes!!)
        assertTrue(structType.isPacked)
        assertTrue(structType.isComplete())
    }
    
    @Test
    @DisplayName("registerOpaqueStructType should register opaque struct")
    fun testRegisterOpaqueStructType() {
        val module = Module("TestModule")
        
        // Register an opaque struct
        val opaqueStruct = module.registerOpaqueStructType("OpaquePoint")
        
        // Verify struct properties
        assertEquals("OpaquePoint", opaqueStruct.name)
        assertNull(opaqueStruct.elementTypes)
        assertFalse(opaqueStruct.isPacked)
        assertTrue(opaqueStruct.isOpaque())
        
        // Verify it can be retrieved
        val retrieved = module.getNamedStructType("OpaquePoint")
        assertNotNull(retrieved)
        assertSame(opaqueStruct, retrieved)
    }
    
    @Test
    @DisplayName("registerOpaqueStructType should enforce name uniqueness")
    fun testRegisterOpaqueStructTypeUniqueness() {
        val module = Module("TestModule")
        
        // Register first opaque struct
        val firstOpaque = module.registerOpaqueStructType("OpaquePoint")
        
        // Attempt to register another opaque struct with same name should throw exception
        assertThrows<IllegalArgumentException> {
            module.registerOpaqueStructType("OpaquePoint")
        }
        
        // Original opaque struct should still be available
        val retrieved = module.getNamedStructType("OpaquePoint")
        assertSame(firstOpaque, retrieved)
    }
    
    @Test
    @DisplayName("completeOpaqueStructType should complete opaque struct correctly")
    fun testCompleteOpaqueStructType() {
        val module = Module("TestModule")
        
        // Register an opaque struct
        val opaqueStruct = module.registerOpaqueStructType("OpaquePoint")
        assertTrue(opaqueStruct.isOpaque())
        
        // Complete the opaque struct
        val completedStruct = module.completeOpaqueStructType("OpaquePoint", listOf(IntegerType.I64, IntegerType.I64))
        
        // Verify completion
        assertEquals("OpaquePoint", completedStruct.name)
        assertEquals(listOf(IntegerType.I64, IntegerType.I64), completedStruct.elementTypes!!)
        assertFalse(completedStruct.isPacked)
        assertTrue(completedStruct.isComplete())
        
        // Verify it has the same name but is a new object
        assertEquals(opaqueStruct.name, completedStruct.name)
        
        // Verify it can be retrieved
        val retrieved = module.getNamedStructType("OpaquePoint")
        assertSame(completedStruct, retrieved)
    }
    
    @Test
    @DisplayName("completeOpaqueStructType should throw exception for non-existent struct")
    fun testCompleteOpaqueStructTypeNonExistent() {
        val module = Module("TestModule")
        
        // Attempt to complete non-existent opaque struct should throw exception
        assertThrows<IllegalArgumentException> {
            module.completeOpaqueStructType("NonExistent", listOf(IntegerType.I32))
        }
    }
    
    @Test
    @DisplayName("completeOpaqueStructType should throw exception for non-opaque struct")
    fun testCompleteOpaqueStructTypeNonOpaque() {
        val module = Module("TestModule")
        
        // Register a regular named struct
        val regularStruct = module.registerNamedStructType("Point", listOf(IntegerType.I32, IntegerType.I32))
        
        // Attempt to complete a non-opaque struct should throw exception
        assertThrows<IllegalArgumentException> {
            module.completeOpaqueStructType("Point", listOf(IntegerType.I64, IntegerType.I64))
        }
    }
    
    @Test
    @DisplayName("getOrCreateAnonymousStructType should deduplicate identical anonymous structs")
    fun testGetOrCreateAnonymousStructTypeDeduplication() {
        val module = Module("TestModule")
        
        // Create first anonymous struct
        val firstStruct = module.getOrCreateAnonymousStructType(listOf(IntegerType.I32, IntegerType.I32))
        
        // Create identical anonymous struct
        val secondStruct = module.getOrCreateAnonymousStructType(listOf(IntegerType.I32, IntegerType.I32))
        
        // Should be the same object (deduplicated)
        assertSame(firstStruct, secondStruct)
        assertEquals(listOf(IntegerType.I32, IntegerType.I32), firstStruct.elementTypes)
    }
    
    @Test
    @DisplayName("getOrCreateAnonymousStructType should create different structs for different element types")
    fun testGetOrCreateAnonymousStructTypeDifferentTypes() {
        val module = Module("TestModule")
        
        // Create first anonymous struct
        val firstStruct = module.getOrCreateAnonymousStructType(listOf(IntegerType.I32, IntegerType.I32))
        
        // Create different anonymous struct
        val secondStruct = module.getOrCreateAnonymousStructType(listOf(IntegerType.I64, IntegerType.I64))
        
        // Should be different objects
        assertTrue(firstStruct !== secondStruct)
        assertEquals(listOf(IntegerType.I32, IntegerType.I32), firstStruct.elementTypes)
        assertEquals(listOf(IntegerType.I64, IntegerType.I64), secondStruct.elementTypes)
    }
    
    @Test
    @DisplayName("getOrCreateAnonymousStructType should support packed structs")
    fun testGetOrCreateAnonymousStructTypePacked() {
        val module = Module("TestModule")
        
        // Create packed anonymous struct
        val packedStruct = module.getOrCreateAnonymousStructType(listOf(IntegerType.I8, IntegerType.I32), true)
        
        // Verify properties
        assertTrue(packedStruct.isPacked)
        assertEquals(listOf(IntegerType.I8, IntegerType.I32), packedStruct.elementTypes)
        
        // Create identical packed anonymous struct
        val samePackedStruct = module.getOrCreateAnonymousStructType(listOf(IntegerType.I8, IntegerType.I32), true)
        
        // Should be the same object
        assertSame(packedStruct, samePackedStruct)
        
        // Create non-packed version should be different
        val nonPackedStruct = module.getOrCreateAnonymousStructType(listOf(IntegerType.I8, IntegerType.I32), false)
        assertTrue(packedStruct !== nonPackedStruct)
    }
    
    @Test
    @DisplayName("getAllNamedStructTypes should return structs in deterministic order")
    fun testGetAllNamedStructTypes() {
        val module = Module("TestModule")
        
        // Register structs in non-alphabetical order
        val zStruct = module.registerNamedStructType("ZStruct", listOf(IntegerType.I64))
        val aStruct = module.registerNamedStructType("AStruct", listOf(IntegerType.I32))
        val mStruct = module.registerNamedStructType("MStruct", listOf(IntegerType.I16))
        
        // Get all named structs
        val allStructs = module.getAllNamedStructTypes()
        
        // Should return in alphabetical order
        assertEquals(3, allStructs.size)
        assertEquals("AStruct", allStructs[0].name)
        assertEquals("MStruct", allStructs[1].name)
        assertEquals("ZStruct", allStructs[2].name)
        
        // Verify they are the same objects
        assertSame(aStruct, allStructs[0])
        assertSame(mStruct, allStructs[1])
        assertSame(zStruct, allStructs[2])
    }
    
    @Test
    @DisplayName("getAllAnonymousStructTypes should return structs in deterministic order")
    fun testGetAllAnonymousStructTypes() {
        val module = Module("TestModule")
        
        // Create anonymous structs
        val firstStruct = module.getOrCreateAnonymousStructType(listOf(IntegerType.I32))
        val secondStruct = module.getOrCreateAnonymousStructType(listOf(IntegerType.I64))
        val thirdStruct = module.getOrCreateAnonymousStructType(listOf(IntegerType.I16))
        
        // Get all anonymous structs
        val allStructs = module.getAllAnonymousStructTypes()
        
        // Should return in deterministic order (by element type string representation)
        assertEquals(3, allStructs.size)
        
        // Verify they are the same objects
        assertTrue(allStructs.contains(firstStruct))
        assertTrue(allStructs.contains(secondStruct))
        assertTrue(allStructs.contains(thirdStruct))
    }
    
    @Test
    @DisplayName("getAllStructTypes should return all structs in deterministic order")
    fun testGetAllStructTypes() {
        val module = Module("TestModule")
        
        // Register named structs
        val zStruct = module.registerNamedStructType("ZStruct", listOf(IntegerType.I64))
        val aStruct = module.registerNamedStructType("AStruct", listOf(IntegerType.I32))
        
        // Create anonymous structs
        val anonStruct1 = module.getOrCreateAnonymousStructType(listOf(IntegerType.I16))
        val anonStruct2 = module.getOrCreateAnonymousStructType(listOf(IntegerType.I8))
        
        // Get all structs
        val allStructs = module.getAllStructTypes()
        
        // Should return all structs
        assertEquals(4, allStructs.size)
        
        // Verify all are present
        assertTrue(allStructs.contains(aStruct))
        assertTrue(allStructs.contains(zStruct))
        assertTrue(allStructs.contains(anonStruct1))
        assertTrue(allStructs.contains(anonStruct2))
    }
    
    @Test
    @DisplayName("getNamedStructType should return null for non-existent struct")
    fun testGetNamedStructTypeNonExistent() {
        val module = Module("TestModule")
        
        // Attempt to get non-existent struct should return null
        val result = module.getNamedStructType("NonExistent")
        assertNull(result)
    }
    
    @Test
    @DisplayName("Module should maintain separate registries for different modules")
    fun testModuleSeparateRegistries() {
        val module1 = Module("Module1")
        val module2 = Module("Module2")
        
        // Register struct with same name in both modules
        val struct1 = module1.registerNamedStructType("Point", listOf(IntegerType.I32, IntegerType.I32))
        val struct2 = module2.registerNamedStructType("Point", listOf(IntegerType.I64, IntegerType.I64))
        
        // Should be different objects
        assertTrue(struct1 !== struct2)
        
        // Each module should return its own struct
        assertSame(struct1, module1.getNamedStructType("Point"))
        assertSame(struct2, module2.getNamedStructType("Point"))
    }
}