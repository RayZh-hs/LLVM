package space.norb.llvm.structure

import space.norb.llvm.core.Type
import space.norb.llvm.core.Value
import space.norb.llvm.types.FunctionType

/**
 * Function in LLVM IR.
 */
class Function(
    override val name: String,
    override val type: FunctionType,
    val module: Module
) : Value {
    val returnType: Type = type.returnType
    val parameters: List<Argument> = type.paramTypes.mapIndexed { index, paramType ->
        Argument("arg$index", paramType, this, index)
    }
    val basicBlocks: MutableList<BasicBlock> = mutableListOf()
    var entryBlock: BasicBlock? = null
}