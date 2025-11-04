package space.norb.llvm.values.constants

import space.norb.llvm.core.Constant
import space.norb.llvm.types.PointerType

/**
 * Null pointer constant in LLVM IR.
 */
class NullPointerConstant(
    override val type: PointerType
) : Constant("null", type)