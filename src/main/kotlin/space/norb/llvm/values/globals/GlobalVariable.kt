package space.norb.llvm.values.globals

import space.norb.llvm.core.Constant
import space.norb.llvm.types.PointerType
import space.norb.llvm.structure.Module
import space.norb.llvm.enums.LinkageType

/**
 * Global variable in LLVM IR.
 */
class GlobalVariable(
    override val name: String,
    override val type: PointerType,
    val module: Module,
    val initializer: Constant? = null,
    val isConstantValue: Boolean = false,
    val linkage: LinkageType = LinkageType.EXTERNAL
) : Constant(name, type)