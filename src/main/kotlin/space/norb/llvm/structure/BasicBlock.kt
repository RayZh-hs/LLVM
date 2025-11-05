package space.norb.llvm.structure

import space.norb.llvm.core.Value
import space.norb.llvm.types.LabelType
import space.norb.llvm.instructions.base.Instruction
import space.norb.llvm.instructions.base.TerminatorInst
import space.norb.llvm.visitors.IRVisitor

/**
 * Basic block in LLVM IR.
 */
class BasicBlock(
    override val name: String,
    val function: Function
) : Value {
    override val type: LabelType = LabelType
    val instructions: MutableList<Instruction> = mutableListOf()
    var terminator: TerminatorInst? = null
    
    override fun <T> accept(visitor: IRVisitor<T>): T = visitor.visitBasicBlock(this)
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BasicBlock) return false
        return name == other.name && function == other.function
    }
    
    override fun hashCode(): Int {
        return 31 * name.hashCode() + function.hashCode()
    }
    
    override fun toString(): String {
        return "BasicBlock(name=$name, type=$type, function=${function.name})"
    }
}