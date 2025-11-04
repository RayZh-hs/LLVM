package space.norb.llvm.instructions.memory

import space.norb.llvm.core.Value
import space.norb.llvm.core.Type
import space.norb.llvm.instructions.base.MemoryInst
import space.norb.llvm.visitors.IRVisitor

/**
 * Stack memory allocation instruction.
 */
class AllocaInst(
    name: String,
    type: Type,
    arraySize: Value? = null
) : MemoryInst(name, type, if (arraySize != null) listOf(arraySize) else emptyList()) {
    override fun <T> accept(visitor: IRVisitor<T>): T = visitor.visitAllocaInst(this)
}