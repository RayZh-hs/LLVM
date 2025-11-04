package space.norb.llvm.instructions.other

import space.norb.llvm.core.Value
import space.norb.llvm.core.Type
import space.norb.llvm.instructions.base.OtherInst
import space.norb.llvm.visitors.IRVisitor

/**
 * PHI node for SSA form.
 */
class PhiNode(
    name: String,
    type: Type,
    incomingValues: List<Pair<Value, Value>>
) : OtherInst(name, type, incomingValues.flatMap { listOf(it.first, it.second) }) {
    override fun <T> accept(visitor: IRVisitor<T>): T = visitor.visitPhiNode(this)
}