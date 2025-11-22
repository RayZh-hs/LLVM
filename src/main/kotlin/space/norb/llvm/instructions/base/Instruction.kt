package space.norb.llvm.instructions.base

import space.norb.llvm.core.User
import space.norb.llvm.core.Value
import space.norb.llvm.core.Type
import space.norb.llvm.structure.BasicBlock

/**
 * Base class for all LLVM instructions.
 */
abstract class Instruction(
    override val name: String,
    override val type: Type,
    operands: List<Value>
) : User(name, type, operands) {
    lateinit var parent: BasicBlock
    var inlineComment: String? = null
    
    override fun getParent(): Any? {
        // Instructions belong to basic blocks
        return if (::parent.isInitialized) parent else null
    }
}
