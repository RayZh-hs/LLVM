package space.norb.llvm.values.constants

import space.norb.llvm.core.Constant
import space.norb.llvm.types.IntegerType

/**
 * Constant integer value in LLVM IR.
 */
data class IntConstant(
    val value: Long,
    override val type: IntegerType
) : Constant(value.toString(), type)