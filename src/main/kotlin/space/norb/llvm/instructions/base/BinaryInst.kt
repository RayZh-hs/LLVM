package space.norb.llvm.instructions.base

import space.norb.llvm.core.Value
import space.norb.llvm.core.Type

/**
 * Sealed class for binary instructions.
 */
sealed class BinaryInst(
    name: String,
    type: Type,
    val lhs: Value,
    val rhs: Value
) : Instruction(name, type, listOf(lhs, rhs))