package space.norb.llvm.instructions.base

import space.norb.llvm.core.Value
import space.norb.llvm.core.Type

/**
 * Abstract class for memory instructions.
 */
abstract class MemoryInst(
    name: String,
    type: Type,
    operands: List<Value>
) : Instruction(name, type, operands)