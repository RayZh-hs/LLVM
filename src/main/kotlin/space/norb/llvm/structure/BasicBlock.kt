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
}