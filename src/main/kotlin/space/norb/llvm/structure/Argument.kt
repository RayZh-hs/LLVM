package space.norb.llvm.structure

import space.norb.llvm.core.Value
import space.norb.llvm.core.Type
import space.norb.llvm.visitors.IRVisitor

/**
 * Function argument in LLVM IR.
 */
class Argument(
    override val name: String,
    override val type: Type,
    val function: Function,
    val index: Int
) : Value {
    override fun <T> accept(visitor: IRVisitor<T>): T = visitor.visitArgument(this)
}