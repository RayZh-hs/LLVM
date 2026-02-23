package space.norb.llvm.transformation.presets

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import space.norb.llvm.analysis.AnalysisManager
import space.norb.llvm.builder.IRBuilder
import space.norb.llvm.structure.Module
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.VoidType
import kotlin.test.assertTrue

class ValidationPassTest {

    @Test
    fun `test valid module passes`() {
        val module = Module("test")
        val builder = IRBuilder(module)
        
        val funcType = FunctionType(VoidType, emptyList())
        val function = module.registerFunction("main", funcType)
        val block = function.insertBasicBlock("entry")
        builder.positionAtEnd(block)
        builder.insertRetVoid()
        
        val am = AnalysisManager(module)
        ValidationPass.run(module, am) // Should not throw
    }

    @Test
    fun `test module with missing terminator fails`() {
        val module = Module("test")
        val builder = IRBuilder(module)
        
        val funcType = FunctionType(VoidType, emptyList())
        val function = module.registerFunction("main", funcType)
        function.insertBasicBlock("entry")
        // No return created
        
        val am = AnalysisManager(module)
        assertThrows<IllegalStateException> {
            ValidationPass.run(module, am)
        }.also {
            assertTrue(it.message!!.contains("must have a terminator"))
        }
    }

    @Test
    fun `test module with terminator not at end fails`() {
        val module = Module("test")
        val builder = IRBuilder(module)
        
        val funcType = FunctionType(VoidType, emptyList())
        val function = module.registerFunction("main", funcType)
        val block = function.insertBasicBlock("entry")
        builder.positionAtEnd(block)
        builder.insertRetVoid()
        
        // Manually add another instruction after the terminator
        // By using positionAtEnd, the builder will add after the current end.
        // But insertInstruction has logic to replace/add terminator.
        // Let's manually push an instruction to basicBlock.instructions.
        val comment = space.norb.llvm.instructions.other.CommentAttachment("oops", "Oops")
        block.instructions.add(comment)
        
        val am = AnalysisManager(module)
        assertThrows<IllegalStateException> {
            ValidationPass.run(module, am)
        }.also {
            assertTrue(it.message!!.contains("must be the last instruction"))
        }
    }
}
