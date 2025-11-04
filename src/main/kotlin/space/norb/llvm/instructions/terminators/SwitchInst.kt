package space.norb.llvm.instructions.terminators

import space.norb.llvm.core.Value
import space.norb.llvm.core.Type
import space.norb.llvm.instructions.base.TerminatorInst
import space.norb.llvm.visitors.IRVisitor

/**
 * Switch instruction.
 */
class SwitchInst(
    name: String,
    type: Type,
    condition: Value,
    defaultDestination: Value,
    cases: List<Pair<Value, Value>> = emptyList()
) : TerminatorInst(name, type, listOf(condition, defaultDestination) + cases.flatMap { listOf(it.first, it.second) }) {
    override fun <T> accept(visitor: IRVisitor<T>): T = visitor.visitSwitchInst(this)
}