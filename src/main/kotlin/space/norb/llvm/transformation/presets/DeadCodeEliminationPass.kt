package space.norb.llvm.transformation.presets

import space.norb.llvm.analysis.AnalysisManager
import space.norb.llvm.analysis.presets.PredecessorAnalysis
import space.norb.llvm.structure.Module
import space.norb.llvm.transformation.IRPass

object DeadCodeEliminationPass : IRPass() {
    override fun run(module: Module, am: AnalysisManager): Module {
        val predecessorMap = am.get(PredecessorAnalysis::class)
        for (function in module.functions) {
            // The first block is the entry block, so it cannot be dead code.
            for (block in function.basicBlocks.slice(1 until function.basicBlocks.size)) {
                val predecessors = predecessorMap.getOrDefault(block.id, emptyList()).size
                if (predecessors == 0) {
                    function.basicBlocks.remove(block)
                }
            }
        }

        return module
    }
}