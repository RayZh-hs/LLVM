package space.norb.llvm.instructions.binary

import space.norb.llvm.core.Value
import space.norb.llvm.core.Type
import space.norb.llvm.instructions.base.BinaryInst
import space.norb.llvm.visitors.IRVisitor

/**
 * Subtraction instruction.
 */
class SubInst(
    name: String,
    type: Type,
    lhs: Value,
    rhs: Value
) : BinaryInst(name, type, lhs, rhs) {
    override fun <T> accept(visitor: IRVisitor<T>): T = visitor.visitSubInst(this)
}