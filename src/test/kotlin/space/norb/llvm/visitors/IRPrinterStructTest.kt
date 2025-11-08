package space.norb.llvm.visitors

import space.norb.llvm.structure.Module
import space.norb.llvm.types.IntegerType
import space.norb.llvm.types.StructType
import space.norb.llvm.values.globals.GlobalVariable
import space.norb.llvm.values.constants.IntConstant
import space.norb.llvm.enums.LinkageType
import space.norb.llvm.types.FunctionType
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class IRPrinterStructTest {
    
    @Test
    fun testStructDefinitionsEmittedBeforeGlobals() {
        // Create a module with named struct types
        val module = Module("test")
        
        // Register some named struct types
        val pointStruct = module.registerNamedStructType("Point", listOf(IntegerType.I32, IntegerType.I32))
        val personStruct = module.registerNamedStructType("Person", listOf(IntegerType.I32, pointStruct))
        
        // Create an IRPrinter and print the module
        val printer = IRPrinter()
        val output = printer.print(module)
        
        println("Generated IR:")
        println(output)
        
        // Verify that struct definitions appear in the output
        assertTrue(output.contains("%Point = type { i32, i32 }"), "Point struct definition should be present")
        assertTrue(output.contains("%Person = type { i32, %Point }"), "Person struct definition should be present")
        
        // Verify order: header, then struct definitions
        val lines = output.split("\n")
        val headerIndex = lines.indexOfFirst { it.contains("; Module:") }
        val pointDefIndex = lines.indexOfFirst { it.contains("%Point = type") }
        val personDefIndex = lines.indexOfFirst { it.contains("%Person = type") }
        
        assertTrue(headerIndex != -1, "Header should be present")
        assertTrue(pointDefIndex != -1, "Point definition should be present")
        assertTrue(personDefIndex != -1, "Person definition should be present")
        
        // Struct definitions should come after header
        assertTrue(pointDefIndex > headerIndex, "Point definition should come after header")
        assertTrue(personDefIndex > headerIndex, "Person definition should come after header")
        
        // Struct definitions should be in alphabetical order
        assertTrue(personDefIndex < pointDefIndex, "Person should come before Point alphabetically")
        
        // Should have blank line after header before struct definitions
        assertTrue(lines[headerIndex + 1].isEmpty(), "Should have blank line after header")
    }
    
    @Test
    fun testNoStructDefinitionsWhenNoNamedStructs() {
        // Create a module with no named struct types
        val module = Module("empty")
        
        // Create an IRPrinter and print the module
        val printer = IRPrinter()
        val output = printer.print(module)
        
        println("Generated IR for empty module:")
        println(output)
        
        // Should not contain any struct definitions
        assertFalse(output.contains("= type"), "Should not contain any struct type definitions")
        
        // Should only have header
        val lines = output.split("\n").filter { it.isNotEmpty() }
        assertEquals(1, lines.size, "Should only have header line")
        assertTrue(lines[0].contains("; Module: empty"), "Should have correct header")
    }
    
    @Test
    fun testAnonymousStructsNotEmitted() {
        // Create a module with anonymous struct types
        val module = Module("anonymous")
        
        // Create anonymous struct types (these should not be emitted as definitions)
        val anonymousStruct = module.getOrCreateAnonymousStructType(listOf(IntegerType.I32, IntegerType.I64))
        
        // Create an IRPrinter and print the module
        val printer = IRPrinter()
        val output = printer.print(module)
        
        println("Generated IR for anonymous struct module:")
        println(output)
        
        // Should not contain any struct definitions (only anonymous structs)
        assertFalse(output.contains("= type"), "Should not contain any struct type definitions")
        
        // Should only have header
        val lines = output.split("\n").filter { it.isNotEmpty() }
        assertEquals(1, lines.size, "Should only have header line")
    }
    
    @Test
    fun testOpaqueStructDefinitionEmission() {
        // Create a module with opaque struct types
        val module = Module("opaque_test")
        
        // Register opaque struct types
        val opaqueStruct1 = module.registerOpaqueStructType("OpaqueStruct1")
        val opaqueStruct2 = module.registerOpaqueStructType("OpaqueStruct2")
        
        // Create an IRPrinter and print the module
        val printer = IRPrinter()
        val output = printer.print(module)
        
        println("Generated IR for opaque structs:")
        println(output)
        
        // Verify that opaque struct definitions appear in the output
        assertTrue(output.contains("%OpaqueStruct1 = type opaque"), "OpaqueStruct1 definition should be present")
        assertTrue(output.contains("%OpaqueStruct2 = type opaque"), "OpaqueStruct2 definition should be present")
        
        // Verify order: header, then struct definitions in alphabetical order
        val lines = output.split("\n")
        val headerIndex = lines.indexOfFirst { it.contains("; Module:") }
        val opaque1Index = lines.indexOfFirst { it.contains("%OpaqueStruct1 = type") }
        val opaque2Index = lines.indexOfFirst { it.contains("%OpaqueStruct2 = type") }
        
        assertTrue(headerIndex != -1, "Header should be present")
        assertTrue(opaque1Index != -1, "OpaqueStruct1 definition should be present")
        assertTrue(opaque2Index != -1, "OpaqueStruct2 definition should be present")
        
        // Struct definitions should come after header
        assertTrue(opaque1Index > headerIndex, "OpaqueStruct1 definition should come after header")
        assertTrue(opaque2Index > headerIndex, "OpaqueStruct2 definition should come after header")
        
        // Struct definitions should be in alphabetical order
        assertTrue(opaque1Index < opaque2Index, "OpaqueStruct1 should come before OpaqueStruct2 alphabetically")
    }
    
    @Test
    fun testPackedStructDefinitionEmission() {
        // Create a module with packed struct types
        val module = Module("packed_test")
        
        // Register packed struct types
        val packedStruct1 = module.registerNamedStructType("PackedStruct1", listOf(IntegerType.I32, IntegerType.I8), true)
        val packedStruct2 = module.registerNamedStructType("PackedStruct2", listOf(IntegerType.I64, IntegerType.I32, IntegerType.I8), true)
        
        // Create an IRPrinter and print the module
        val printer = IRPrinter()
        val output = printer.print(module)
        
        println("Generated IR for packed structs:")
        println(output)
        
        // Verify that packed struct definitions appear in the output with correct syntax
        assertTrue(output.contains("%PackedStruct1 = type <{ i32, i8 }>"), "PackedStruct1 definition should be present with packed syntax")
        assertTrue(output.contains("%PackedStruct2 = type <{ i64, i32, i8 }>"), "PackedStruct2 definition should be present with packed syntax")
        
        // Verify order: header, then struct definitions in alphabetical order
        val lines = output.split("\n")
        val headerIndex = lines.indexOfFirst { it.contains("; Module:") }
        val packed1Index = lines.indexOfFirst { it.contains("%PackedStruct1 = type") }
        val packed2Index = lines.indexOfFirst { it.contains("%PackedStruct2 = type") }
        
        assertTrue(headerIndex != -1, "Header should be present")
        assertTrue(packed1Index != -1, "PackedStruct1 definition should be present")
        assertTrue(packed2Index != -1, "PackedStruct2 definition should be present")
        
        // Struct definitions should come after header
        assertTrue(packed1Index > headerIndex, "PackedStruct1 definition should come after header")
        assertTrue(packed2Index > headerIndex, "PackedStruct2 definition should come after header")
        
        // Struct definitions should be in alphabetical order
        assertTrue(packed1Index < packed2Index, "PackedStruct1 should come before PackedStruct2 alphabetically")
    }
    
    @Test
    fun testMixedStructTypesEmission() {
        // Create a module with mixed struct types
        val module = Module("mixed_test")
        
        // Register different types of structs in non-alphabetical order
        val packedStruct = module.registerNamedStructType("ZPacked", listOf(IntegerType.I32, IntegerType.I8), true)
        val regularStruct = module.registerNamedStructType("ARegular", listOf(IntegerType.I64, IntegerType.I32), false)
        val opaqueStruct = module.registerOpaqueStructType("MOpaque")
        
        // Complete the opaque struct
        val completedOpaque = module.completeOpaqueStructType("MOpaque", listOf(IntegerType.I16), false)
        
        // Create an IRPrinter and print the module
        val printer = IRPrinter()
        val output = printer.print(module)
        
        println("Generated IR for mixed struct types:")
        println(output)
        
        // Verify that all struct definitions appear in the output
        assertTrue(output.contains("%ARegular = type { i64, i32 }"), "Regular struct definition should be present")
        assertTrue(output.contains("%MOpaque = type { i16 }"), "Completed opaque struct definition should be present")
        assertTrue(output.contains("%ZPacked = type <{ i32, i8 }>"), "Packed struct definition should be present")
        
        // Verify order: header, then struct definitions in alphabetical order
        val lines = output.split("\n")
        val headerIndex = lines.indexOfFirst { it.contains("; Module:") }
        val regularIndex = lines.indexOfFirst { it.contains("%ARegular = type") }
        val opaqueIndex = lines.indexOfFirst { it.contains("%MOpaque = type") }
        val packedIndex = lines.indexOfFirst { it.contains("%ZPacked = type") }
        
        assertTrue(headerIndex != -1, "Header should be present")
        assertTrue(regularIndex != -1, "Regular struct definition should be present")
        assertTrue(opaqueIndex != -1, "Opaque struct definition should be present")
        assertTrue(packedIndex != -1, "Packed struct definition should be present")
        
        // All struct definitions should come after header
        assertTrue(regularIndex > headerIndex, "Regular struct definition should come after header")
        assertTrue(opaqueIndex > headerIndex, "Opaque struct definition should come after header")
        assertTrue(packedIndex > headerIndex, "Packed struct definition should come after header")
        
        // Struct definitions should be in alphabetical order
        assertTrue(regularIndex < opaqueIndex, "ARegular should come before MOpaque alphabetically")
        assertTrue(opaqueIndex < packedIndex, "MOpaque should come before ZPacked alphabetically")
    }
    
    @Test
    fun testStructDefinitionsEmittedBeforeGlobalsAndFunctions() {
        // Create a module with structs, globals, and functions
        val module = Module("order_test")
        
        // Register struct types
        val pointStruct = module.registerNamedStructType("Point", listOf(IntegerType.I32, IntegerType.I32))
        val packedStruct = module.registerNamedStructType("PackedPoint", listOf(IntegerType.I32, IntegerType.I32), true)
        
        // Add a global variable
        val globalInitializer = IntConstant(42, IntegerType.I32)
        val globalVar = GlobalVariable.create("testGlobal", module, globalInitializer)
        module.globalVariables.add(globalVar)
        
        // Add a function
        val functionType = FunctionType(IntegerType.I32, listOf(pointStruct))
        val function = space.norb.llvm.structure.Function("testFunction", functionType, module)
        module.functions.add(function)
        
        // Create an IRPrinter and print the module
        val printer = IRPrinter()
        val output = printer.print(module)
        
        println("Generated IR for order test:")
        println(output)
        
        // Verify that struct definitions appear before globals and functions
        val lines = output.split("\n")
        val headerIndex = lines.indexOfFirst { it.contains("; Module:") }
        val pointDefIndex = lines.indexOfFirst { it.contains("%Point = type") }
        val packedDefIndex = lines.indexOfFirst { it.contains("%PackedPoint = type") }
        val globalIndex = lines.indexOfFirst { it.contains("@testGlobal") }
        val functionIndex = lines.indexOfFirst { it.contains("define") }
        
        assertTrue(headerIndex != -1, "Header should be present")
        assertTrue(pointDefIndex != -1, "Point definition should be present")
        assertTrue(packedDefIndex != -1, "PackedPoint definition should be present")
        assertTrue(globalIndex != -1, "Global variable should be present")
        assertTrue(functionIndex != -1, "Function should be present")
        
        // Struct definitions should come after header but before globals and functions
        assertTrue(pointDefIndex > headerIndex, "Point definition should come after header")
        assertTrue(packedDefIndex > headerIndex, "PackedPoint definition should come after header")
        assertTrue(pointDefIndex < globalIndex, "Point definition should come before global variable")
        assertTrue(packedDefIndex < globalIndex, "PackedPoint definition should come before global variable")
        assertTrue(pointDefIndex < functionIndex, "Point definition should come before function")
        assertTrue(packedDefIndex < functionIndex, "PackedPoint definition should come before function")
        
        // Struct definitions should be in alphabetical order
        assertTrue(packedDefIndex < pointDefIndex, "PackedPoint should come before Point alphabetically")
    }
    
    @Test
    fun testNoStructBlockWhenOnlyAnonymousStructs() {
        // Create a module with only anonymous struct types
        val module = Module("anonymous_only")
        
        // Create anonymous struct types (these should not be emitted as definitions)
        val anonymousStruct1 = module.getOrCreateAnonymousStructType(listOf(IntegerType.I32, IntegerType.I64))
        val anonymousStruct2 = module.getOrCreateAnonymousStructType(listOf(IntegerType.I8, IntegerType.I16), true)
        
        // Create an IRPrinter and print the module
        val printer = IRPrinter()
        val output = printer.print(module)
        
        println("Generated IR for anonymous-only module:")
        println(output)
        
        // Should not contain any struct definitions (only anonymous structs)
        assertFalse(output.contains("= type"), "Should not contain any struct type definitions")
        
        // Should only have header
        val lines = output.split("\n").filter { it.isNotEmpty() }
        assertEquals(1, lines.size, "Should only have header line")
        assertTrue(lines[0].contains("; Module: anonymous_only"), "Should have correct header")
    }
}