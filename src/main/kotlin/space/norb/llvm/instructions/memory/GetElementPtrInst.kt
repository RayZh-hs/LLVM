package space.norb.llvm.instructions.memory

import space.norb.llvm.core.Value
import space.norb.llvm.core.Type
import space.norb.llvm.instructions.base.MemoryInst
import space.norb.llvm.visitors.IRVisitor

/**
 * Get element pointer calculation instruction.
 */
class GetElementPtrInst(
    name: String,
    type: Type,
    pointer: Value,
    indices: List<Value>
) : MemoryInst(name, type, listOf(pointer) + indices) {
    override fun <T> accept(visitor: IRVisitor<T>): T = visitor.visitGetElementPtrInst(this)
}