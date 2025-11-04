package space.norb.llvm.instructions.terminators

import space.norb.llvm.core.Value
import space.norb.llvm.core.Type
import space.norb.llvm.instructions.base.TerminatorInst
import space.norb.llvm.visitors.IRVisitor

/**
 * Function return instruction.
 */
class ReturnInst(
    name: String,
    type: Type,
    returnValue: Value? = null
) : TerminatorInst(name, type, returnValue?.let { listOf(it) } ?: emptyList()) {
    override fun <T> accept(visitor: IRVisitor<T>): T = visitor.visitReturnInst(this)
}