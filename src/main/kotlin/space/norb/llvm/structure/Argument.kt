package space.norb.llvm.structure

import space.norb.llvm.core.Value
import space.norb.llvm.core.Type
import space.norb.llvm.visitors.IRVisitor

/**
 * Function argument in LLVM IR.
 */
class Argument(
    override val name: String,
    override val type: Type,
    val function: Function,
    val index: Int
) : Value {
    override fun <T> accept(visitor: IRVisitor<T>): T = visitor.visitArgument(this)
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Argument) return false
        return name == other.name && type == other.type && function == other.function && index == other.index
    }
    
    override fun hashCode(): Int {
        return 31 * name.hashCode() + type.hashCode() + function.hashCode() + index
    }
    
    override fun toString(): String {
        return "Argument(name=$name, type=$type, function=${function.name}, index=$index)"
    }
    
    override fun getParent(): Any? {
        // Arguments belong to functions
        return function
    }
}