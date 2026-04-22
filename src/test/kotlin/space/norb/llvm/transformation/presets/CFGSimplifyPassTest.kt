package space.norb.llvm.transformation.presets

import org.junit.jupiter.api.Test
import space.norb.llvm.analysis.AnalysisManager
import space.norb.llvm.builder.IRBuilder
import space.norb.llvm.instructions.base.TerminatorInst
import space.norb.llvm.structure.Module
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.VoidType
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CFGSimplifyPassTest {

    private fun Module.printCFG() {
        val sb = StringBuilder()
        for (function in functions) {
            sb.append("Function ${function.name}:\n")
            for (block in function.basicBlocks) {
                sb.append("  Block ${block.name}:\n")
                for (inst in block.instructions) {
                    sb.append("    ${inst::class.simpleName}\n")
                }
                sb.append("    Terminator: ${block.terminator!!::class.simpleName}\n")
                sb.append("    Next blocks: ${block.terminator!!.getSuccessors().joinToString { it.name ?: "<unnamed>" }}\n")
            }
        }
        print(sb.toString())
    }

    @Test
    fun `test basic block merging`() {
        val module = Module("test")
        val am = AnalysisManager(module)
        val builder = IRBuilder(module)

        val funcType = FunctionType(VoidType, emptyList())
        val function = module.registerFunction("main", funcType)
        
        val entry = function.insertBasicBlock("entry")
        val next = function.insertBasicBlock("next")
        val exit = function.insertBasicBlock("exit")

        builder.positionAtEnd(entry)
        builder.insertBr(next)

        builder.positionAtEnd(next)
        builder.insertBr(exit)

        builder.positionAtEnd(exit)
        builder.insertRetVoid()

        module.printCFG()

        // Before pass: 3 blocks
        assertEquals(3, function.basicBlocks.size)

        CFGSimplifyPass.run(module, am)

        module.printCFG()

        // After pass: should be 1 block if both next and exit are merged into entry
        // Wait, 'exit' also has only one predecessor ('next').
        // So 'entry' devours 'next', then 'entry' devours 'exit'.
        // Final function should have only 1 block.

        assertEquals(1, function.basicBlocks.size)
        val finalBlock = function.basicBlocks.single()
        assertEquals(1, finalBlock.instructions.count { it is TerminatorInst })
        assertTrue(finalBlock.terminator === finalBlock.instructions.last())
        ValidationPass.run(module, AnalysisManager(module))
    }

    @Test
    fun `test dead code elimination`() {
        val module = Module("test")
        val am = AnalysisManager(module)
        val builder = IRBuilder(module)

        val funcType = FunctionType(VoidType, emptyList())
        val function = module.registerFunction("main", funcType)
        
        val entry = function.insertBasicBlock("entry")
        val dead = function.insertBasicBlock("dead")
        val exit = function.insertBasicBlock("exit")

        builder.positionAtEnd(entry)
        builder.insertBr(exit)

        builder.positionAtEnd(dead)
        builder.insertBr(exit)

        builder.positionAtEnd(exit)
        builder.insertRetVoid()

        // 'dead' has no predecessors in the CFG starting from entry.
        
        assertEquals(3, function.basicBlocks.size)

        CFGSimplifyPass.run(module, am)

        // 'dead' should be removed.
        // After 'dead' is removed, 'exit' only has 'entry' as a predecessor.
        // Then 'entry' and 'exit' should be merged.

        assertEquals(1, function.basicBlocks.size)
        assertTrue(function.basicBlocks.none { it.name == "dead" })
        ValidationPass.run(module, AnalysisManager(module))
    }
}
