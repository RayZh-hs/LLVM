package space.norb.llvm.instructions.terminators

import space.norb.llvm.core.Value
import space.norb.llvm.core.Type
import space.norb.llvm.instructions.base.TerminatorInst
import space.norb.llvm.visitors.IRVisitor

/**
 * Branch instruction (conditional and unconditional).
 */
class BranchInst(
    name: String,
    type: Type,
    destination: Value,
    condition: Value? = null
) : TerminatorInst(name, type, condition?.let { listOf(it, destination) } ?: listOf(destination)) {
    override fun <T> accept(visitor: IRVisitor<T>): T = visitor.visitBranchInst(this)
    
    // Constructor for unconditional branch
    constructor(name: String, type: Type, target: Value) : this(name, type, target, null)
    
    // Constructor for conditional branch
    constructor(name: String, type: Type, condition: Value, trueTarget: Value, falseTarget: Value) :
        this(name, type, trueTarget, condition)
}