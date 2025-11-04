package space.norb.llvm.instructions.memory

import space.norb.llvm.core.Value
import space.norb.llvm.core.Type
import space.norb.llvm.instructions.base.MemoryInst
import space.norb.llvm.visitors.IRVisitor

/**
 * Load value from memory instruction.
 */
class LoadInst(
    name: String,
    type: Type,
    pointer: Value
) : MemoryInst(name, type, listOf(pointer)) {
    override fun <T> accept(visitor: IRVisitor<T>): T = visitor.visitLoadInst(this)
}