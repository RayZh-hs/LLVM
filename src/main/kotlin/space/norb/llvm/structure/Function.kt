package space.norb.llvm.structure

import space.norb.llvm.core.Type
import space.norb.llvm.core.Value
import space.norb.llvm.types.FunctionType
import space.norb.llvm.visitors.IRVisitor

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
    
    override fun <T> accept(visitor: IRVisitor<T>): T = visitor.visitFunction(this)
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Function) return false
        return name == other.name && type == other.type && module == other.module
    }
    
    override fun hashCode(): Int {
        return 31 * name.hashCode() + type.hashCode() + module.hashCode()
    }
    
    override fun toString(): String {
        return "Function(name=$name, type=$type, module=${module.name})"
    }
}