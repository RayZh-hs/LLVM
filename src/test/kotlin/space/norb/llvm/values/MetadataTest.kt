package space.norb.llvm.values

import space.norb.llvm.visitors.IRPrinter
import space.norb.llvm.visitors.IRValidator
import space.norb.llvm.values.constants.IntConstant
import space.norb.llvm.types.IntegerType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

/**
 * Test cases for Metadata values.
 */
class MetadataTest {
    
    @Test
    fun testMDString() {
        val mdString = MDString("test_string", "test_md")
        
        // Test basic properties
        assertEquals("test_string", mdString.value)
        assertEquals("test_md", mdString.name)
        assertEquals("metadata", mdString.type.toString())
        
        // Test methods
        assertFalse(mdString.isEmpty())
        assertEquals(11, mdString.getLength())
        assertTrue(mdString.canAttachToValues())
        assertFalse(mdString.isDistinct())
        
        // Test IR string representation
        assertEquals("!\"test_string\"", mdString.toIRString())
        
        // Test visitor pattern
        val printer = IRPrinter()
        mdString.accept(printer)
        
        val validator = IRValidator()
        assertTrue(mdString.accept(validator))
    }
    
    @Test
    fun testMDStringEmpty() {
        val emptyMDString = MDString("")
        
        assertTrue(emptyMDString.isEmpty())
        assertEquals(0, emptyMDString.getLength())
        assertEquals("!\"\"", emptyMDString.toIRString())
    }
    
    @Test
    fun testMDNode() {
        val mdString1 = MDString("operand1")
        val mdString2 = MDString("operand2")
        val mdNode = MDNode(listOf(mdString1, mdString2), "test_node")
        
        // Test basic properties
        assertEquals(2, mdNode.getNumOperands())
        assertEquals(mdString1, mdNode.getOperand(0))
        assertEquals(mdString2, mdNode.getOperand(1))
        assertEquals("test_node", mdNode.name)
        assertEquals("metadata", mdNode.type.toString())
        
        // Test methods
        assertFalse(mdNode.isEmpty())
        assertTrue(mdNode.canAttachToValues())
        assertFalse(mdNode.isDistinct())
        
        // Test IR string representation
        val irString = mdNode.toIRString()
        assertTrue(irString.contains("!{"))
        assertTrue(irString.contains("!\"operand1\""))
        assertTrue(irString.contains("!\"operand2\""))
        
        // Test visitor pattern
        val printer = IRPrinter()
        mdNode.accept(printer)
        
        val validator = IRValidator()
        assertTrue(mdNode.accept(validator))
    }
    
    @Test
    fun testMDNodeDistinct() {
        val mdString = MDString("distinct_test")
        val distinctMDNode = MDNode(listOf(mdString), distinct = true)
        
        assertTrue(distinctMDNode.isDistinct())
        val irString = distinctMDNode.toIRString()
        assertTrue(irString.contains("!distinct "))
    }
    
    @Test
    fun testMDNodeEmpty() {
        val emptyMDNode = MDNode(emptyList())
        
        assertTrue(emptyMDNode.isEmpty())
        assertEquals(0, emptyMDNode.getNumOperands())
        assertEquals("!{}", emptyMDNode.toIRString())
    }
    
    @Test
    fun testConstantAsMetadata() {
        val intConstant = IntConstant(42, IntegerType.I32)
        val constantAsMetadata = ConstantAsMetadata(intConstant, "const_md")
        
        // Test basic properties
        assertEquals(intConstant, constantAsMetadata.getConstant())
        assertEquals(IntegerType.I32, constantAsMetadata.getConstantType())
        assertEquals("const_md", constantAsMetadata.name)
        assertEquals("metadata", constantAsMetadata.type.toString())
        
        // Test methods
        assertTrue(constantAsMetadata.canAttachToValues())
        assertFalse(constantAsMetadata.isDistinct())
        
        // Test IR string representation
        assertEquals("IntConstant(value=42, type=i32)", constantAsMetadata.toIRString())
        
        // Test visitor pattern
        val printer = IRPrinter()
        constantAsMetadata.accept(printer)
        
        val validator = IRValidator()
        assertTrue(constantAsMetadata.accept(validator))
    }
    
    @Test
    fun testMetadataIdentifier() {
        val namedMD = MDString("test", "named_metadata")
        assertEquals("named_metadata", namedMD.getIdentifier())
        
        val unnamedMD = MDString("test")
        assertTrue(unnamedMD.getIdentifier().startsWith("metadata_"))
    }
    
    @Test
    fun testMDNodeOperandBounds() {
        val mdNode = MDNode(listOf(MDString("test")))
        
        // Valid access
        assertEquals(MDString("test"), mdNode.getOperand(0))
        
        // Invalid access should throw exception
        try {
            mdNode.getOperand(1)
            assertTrue(false, "Should have thrown IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            assertTrue(true)
        }
        
        try {
            mdNode.getOperand(-1)
            assertTrue(false, "Should have thrown IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            assertTrue(true)
        }
    }
}