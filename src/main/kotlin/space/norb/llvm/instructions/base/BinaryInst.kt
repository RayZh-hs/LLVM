package space.norb.llvm.instructions.base

import space.norb.llvm.core.Value
import space.norb.llvm.core.Type

/**
 * Abstract class for binary instructions.
 */
abstract class BinaryInst(
    name: String,
    type: Type,
    val lhs: Value,
    val rhs: Value
) : Instruction(name, type, listOf(lhs, rhs))