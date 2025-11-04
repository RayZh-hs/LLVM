package space.norb.llvm.instructions.casts

import space.norb.llvm.core.Value
import space.norb.llvm.core.Type
import space.norb.llvm.instructions.base.CastInst
import space.norb.llvm.visitors.IRVisitor

/**
 * Bitwise cast between types instruction.
 */
class BitcastInst(
    name: String,
    type: Type,
    value: Value
) : CastInst(name, type, value) {
    override fun <T> accept(visitor: IRVisitor<T>): T = visitor.visitBitcastInst(this)
}