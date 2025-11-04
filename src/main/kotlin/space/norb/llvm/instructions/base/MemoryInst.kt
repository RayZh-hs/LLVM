package space.norb.llvm.instructions.base

import space.norb.llvm.core.Value
import space.norb.llvm.core.Type

/**
 * Sealed class for memory instructions.
 */
sealed class MemoryInst(
    name: String,
    type: Type,
    operands: List<Value>
) : Instruction(name, type, operands)