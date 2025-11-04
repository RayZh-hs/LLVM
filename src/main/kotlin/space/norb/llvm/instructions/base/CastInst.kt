package space.norb.llvm.instructions.base

import space.norb.llvm.core.Value
import space.norb.llvm.core.Type

/**
 * Abstract class for cast instructions.
 */
abstract class CastInst(
    name: String,
    override val type: Type,
    val value: Value
) : Instruction(name, type, listOf(value))