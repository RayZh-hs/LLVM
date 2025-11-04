package space.norb.llvm.values.constants

import space.norb.llvm.core.Constant
import space.norb.llvm.types.FloatingPointType

/**
 * Constant floating-point value in LLVM IR.
 */
data class FloatConstant(
    val value: Double,
    override val type: FloatingPointType
) : Constant(value.toString(), type)