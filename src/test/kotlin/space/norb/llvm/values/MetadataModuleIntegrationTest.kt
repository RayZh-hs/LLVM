package space.norb.llvm.values

import space.norb.llvm.structure.Module
import space.norb.llvm.visitors.IRPrinter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Integration test to verify that Metadata works correctly with Module.
 */
class MetadataModuleIntegrationTest {
    
    @Test
    fun testModuleWithMetadata() {
        // Create a module
        val module = Module("test_module")
        
        // Create some metadata
        val debugInfo = MDString("debug_info", "debug")
        val versionInfo = MDNode(listOf(MDString("1.0.0"), MDString("test")), "version")
        
        // Add metadata to the module
        module.namedMetadata["debug"] = debugInfo
        module.namedMetadata["version"] = versionInfo
        
        // Verify metadata is stored correctly
        assertEquals(debugInfo, module.namedMetadata["debug"])
        assertEquals(versionInfo, module.namedMetadata["version"])
        assertEquals(2, module.namedMetadata.size)
        
        // Test that metadata can be printed
        val printer = IRPrinter()
        debugInfo.accept(printer)
        versionInfo.accept(printer)
        
        // Verify metadata properties
        assertTrue(debugInfo.canAttachToValues())
        assertTrue(versionInfo.canAttachToValues())
        assertEquals("metadata", debugInfo.type.toString())
        assertEquals("metadata", versionInfo.type.toString())
    }
    
    @Test
    fun testModuleMetadataIRString() {
        val module = Module("ir_test")
        
        val mdString = MDString("test_string")
        val mdNode = MDNode(listOf(mdString, MDString("another")))
        
        module.namedMetadata["test"] = mdString
        module.namedMetadata["node"] = mdNode
        
        // Test IR string generation
        assertEquals("!\"test_string\"", mdString.toIRString())
        assertTrue(mdNode.toIRString().contains("!{"))
        assertTrue(mdNode.toIRString().contains("!\"test_string\""))
        assertTrue(mdNode.toIRString().contains("!\"another\""))
    }
}