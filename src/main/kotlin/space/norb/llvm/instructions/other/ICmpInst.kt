package space.norb.llvm.instructions.other

import space.norb.llvm.core.Value
import space.norb.llvm.core.Type
import space.norb.llvm.instructions.base.OtherInst
import space.norb.llvm.enums.IcmpPredicate
import space.norb.llvm.visitors.IRVisitor

/**
 * Integer comparison instruction.
 */
class ICmpInst(
    name: String,
    type: Type,
    predicate: IcmpPredicate,
    lhs: Value,
    rhs: Value
) : OtherInst(name, type, listOf(lhs, rhs)) {
    override fun <T> accept(visitor: IRVisitor<T>): T = visitor.visitICmpInst(this)
}