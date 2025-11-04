package space.norb.llvm.instructions.other

import space.norb.llvm.core.Value
import space.norb.llvm.core.Type
import space.norb.llvm.instructions.base.OtherInst
import space.norb.llvm.visitors.IRVisitor

/**
 * Function call instruction.
 */
class CallInst(
    name: String,
    type: Type,
    callee: Value,
    args: List<Value>
) : OtherInst(name, type, listOf(callee) + args) {
    override fun <T> accept(visitor: IRVisitor<T>): T = visitor.visitCallInst(this)
}