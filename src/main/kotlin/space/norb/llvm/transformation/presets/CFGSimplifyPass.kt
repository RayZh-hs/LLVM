package space.norb.llvm.transformation.presets

import space.norb.llvm.analysis.AnalysisManager
import space.norb.llvm.analysis.presets.PredecessorAnalysis
import space.norb.llvm.instructions.other.CommentAttachment
import space.norb.llvm.instructions.terminators.BranchInst
import space.norb.llvm.structure.BasicBlock
import space.norb.llvm.structure.BasicBlockId
import space.norb.llvm.structure.Module
import space.norb.llvm.transformation.IRPass
import space.norb.llvm.utils.Renamer

object CFGSimplifyPass : IRPass() {
    private fun BasicBlock.devour(other: BasicBlock) {
        this.instructions.add(CommentAttachment(name = Renamer.another(), comment = "Collapsed block ${other.name}"))
        this.instructions.addAll(other.instructions)
        this.terminator = other.terminator
    }

    override fun run(module: Module, am: AnalysisManager): Module {
        // 1. Run dead code elimination to remove unreachable blocks.
        DeadCodeEliminationPass.run(module, am)
        DeadCodeEliminationPass.updateAnalysisManager(am)
        // 2. Now all blocks except the entry blocks have predecessors. Perform basic block merging.
        val predecessorMap = am.get(PredecessorAnalysis::class)
        for (function in module.functions) {
            val removedBlocks = mutableListOf<BasicBlockId>()
            for (block in function.basicBlocks) {
                if (removedBlocks.contains(block.id)) continue
                var terminator = block.terminator!!
                while (terminator is BranchInst && terminator.isUnconditional()) {
                    val dest = terminator.getDestination() as BasicBlock
                    if (dest.id == block.id) break // Avoid infinite loop on self-loop
                    if (predecessorMap[dest.id]!!.size <= 1) {
                        print("Collapsing block ${dest.name} into ${block.name}\n")
                        // This means that the destination block is only reachable from this block
                        removedBlocks.add(dest.id)
                        terminator = dest.terminator!!
                        block.devour(dest)
                    } else break
                }
            }
            // Remove the devoured blocks from the function's basic block list
            function.basicBlocks.removeIf { removedBlocks.contains(it.id) }
        }
        return module
    }
}