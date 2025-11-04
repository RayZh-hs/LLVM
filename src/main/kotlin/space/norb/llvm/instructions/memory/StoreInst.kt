package space.norb.llvm.instructions.memory

import space.norb.llvm.core.Value
import space.norb.llvm.core.Type
import space.norb.llvm.instructions.base.MemoryInst
import space.norb.llvm.visitors.IRVisitor

/**
 * Store value to memory instruction.
 */
class StoreInst(
    name: String,
    type: Type,
    value: Value,
    pointer: Value
) : MemoryInst(name, type, listOf(value, pointer)) {
    override fun <T> accept(visitor: IRVisitor<T>): T = visitor.visitStoreInst(this)
}