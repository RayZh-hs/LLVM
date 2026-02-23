package space.norb.llvm.values

import space.norb.llvm.structure.Module
import space.norb.llvm.structure.Function
import space.norb.llvm.structure.BasicBlock
import space.norb.llvm.builder.IRBuilder
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.IntegerType
import space.norb.llvm.types.VoidType
import space.norb.llvm.instructions.binary.AddInst
import space.norb.llvm.values.constants.IntConstant
import space.norb.llvm.visitors.IRPrinter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertNotNull

class CustomMetadataTest {
    @Test
    fun testInstructionMetadata() {
        val module = Module("test")
        val funcType = FunctionType(IntegerType.I32, listOf(IntegerType.I32, IntegerType.I32))
        val func = module.registerFunction("test_func", funcType)
        val block = BasicBlock("entry", func)
        func.basicBlocks.add(block)
        
        val val1 = IntConstant(1, IntegerType.I32)
        val val2 = IntConstant(2, IntegerType.I32)
        val addInst = AddInst("sum", IntegerType.I32, val1, val2)
        block.instructions.add(addInst)
        
        // Attach metadata
        val md = MDString("debug_info")
        addInst.setMetadata("dbg", md)
        
        // Inspect metadata
        assertEquals(md, addInst.getMetadata("dbg"))
        assertTrue(addInst.hasMetadata())
        assertEquals(1, addInst.getAllMetadata().size)
        
        // Check IR printing
        val ir = module.toIRString()
        assertTrue(ir.contains(", !dbg !\"debug_info\""))
    }

    @Test
    fun testFunctionMetadata() {
        val module = Module("test")
        val funcType = FunctionType(IntegerType.I32, emptyList())
        val func = module.registerFunction("test_func", funcType)
        // Add a basic block to make it 'define' instead of 'declare'
        val block = BasicBlock("entry", func)
        func.basicBlocks.add(block)
        
        val md = MDNode(listOf(MDString("func_meta")))
        func.setMetadata("meta", md)
        
        assertEquals(md, func.getMetadata("meta"))
        
        val ir = module.toIRString()
        assertTrue(ir.contains("define i32 @test_func() !meta !{!\"func_meta\"}"))
    }

    @Test
    fun testGlobalVariableMetadata() {
        val module = Module("test")
        val globalValue = IntConstant(42, IntegerType.I32)
        val global = module.registerGlobalVariable("my_global", globalValue)
        
        val md = MDString("global_meta")
        global.setMetadata("custom", md)
        
        assertEquals(md, global.getMetadata("custom"))
        
        val ir = module.toIRString()
        println("GLOBAL IR:\n$ir")
        assertTrue(ir.contains("@my_global = global i32 42, !custom !\"global_meta\""))
    }

    @Test
    fun testNamedMetadata() {
        val module = Module("test")
        val md = MDNode(listOf(MDString("flag1"), MDString("flag2")))
        module.namedMetadata["llvm.module.flags"] = md
        
        val ir = module.toIRString()
        assertTrue(ir.contains("!llvm.module.flags = !{!\"flag1\", !\"flag2\"}"))
    }

    @Test
    fun testBuilderMetadata() {
        val module = Module("test")
        val builder = IRBuilder(module)
        val funcType = FunctionType(VoidType, emptyList())
        val func = module.registerFunction("test_func", funcType)
        val block = BasicBlock("entry", func)
        func.basicBlocks.add(block)
        builder.positionAtEnd(block)
        
        val md = MDString("loc1")
        builder.setCurrentDebugLocation(md)
        
        val ret = builder.insertRetVoid()
        
        assertEquals(md, ret.getMetadata("dbg"))
        assertTrue(module.toIRString().contains("ret void, !dbg !\"loc1\""))
    }
}
