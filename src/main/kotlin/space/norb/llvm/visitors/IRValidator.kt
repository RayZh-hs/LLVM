package space.norb.llvm.visitors

import space.norb.llvm.structure.Module
import space.norb.llvm.structure.Function
import space.norb.llvm.structure.BasicBlock
import space.norb.llvm.structure.Argument
import space.norb.llvm.values.globals.GlobalVariable
import space.norb.llvm.core.Constant
import space.norb.llvm.instructions.terminators.ReturnInst
import space.norb.llvm.instructions.terminators.BranchInst
import space.norb.llvm.instructions.terminators.SwitchInst
import space.norb.llvm.instructions.binary.AddInst
import space.norb.llvm.instructions.binary.SubInst
import space.norb.llvm.instructions.binary.MulInst
import space.norb.llvm.instructions.binary.SDivInst
import space.norb.llvm.instructions.binary.AndInst
import space.norb.llvm.instructions.binary.OrInst
import space.norb.llvm.instructions.binary.XorInst
import space.norb.llvm.instructions.memory.AllocaInst
import space.norb.llvm.instructions.memory.LoadInst
import space.norb.llvm.instructions.memory.StoreInst
import space.norb.llvm.instructions.memory.GetElementPtrInst
import space.norb.llvm.instructions.casts.TruncInst
import space.norb.llvm.instructions.casts.ZExtInst
import space.norb.llvm.instructions.casts.SExtInst
import space.norb.llvm.instructions.casts.BitcastInst
import space.norb.llvm.instructions.other.CallInst
import space.norb.llvm.instructions.other.ICmpInst
import space.norb.llvm.instructions.other.PhiNode
import space.norb.llvm.values.Metadata

/**
 * Visitor for validating LLVM IR structure and semantics.
 */
class IRValidator : IRVisitor<Boolean> {
    private val errors = mutableListOf<String>()
    
    fun validate(module: Module): Boolean {
        errors.clear()
        visitModule(module)
        return errors.isEmpty()
    }
    
    fun getErrors(): List<String> = errors.toList()
    
    private fun addError(message: String) {
        errors.add(message)
    }
    
    override fun visitModule(module: Module): Boolean {
        module.functions.forEach { visitFunction(it) }
        return errors.isEmpty()
    }
    
    override fun visitFunction(function: Function): Boolean {
        if (function.name.isEmpty()) {
            addError("Function name cannot be empty")
        }
        
        if (function.basicBlocks.isEmpty()) {
            addError("Function ${function.name} must have at least one basic block")
        }
        
        function.basicBlocks.forEach { visitBasicBlock(it) }
        return errors.isEmpty()
    }
    
    override fun visitBasicBlock(block: BasicBlock): Boolean {
        if (block.name.isEmpty()) {
            addError("Basic block name cannot be empty")
        }
        
        if (block.terminator == null) {
            addError("Basic block ${block.name} must have a terminator")
        }
        
        block.instructions.forEach { it.accept(this) }
        block.terminator?.accept(this)
        return errors.isEmpty()
    }
    
    override fun visitArgument(argument: Argument): Boolean {
        if (argument.name.isEmpty()) {
            addError("Argument name cannot be empty")
        }
        return errors.isEmpty()
    }
    
    override fun visitGlobalVariable(globalVariable: GlobalVariable): Boolean {
        if (globalVariable.name.isEmpty()) {
            addError("Global variable name cannot be empty")
        }
        return errors.isEmpty()
    }
    
    override fun visitConstant(constant: Constant): Boolean {
        // Constants should always be valid
        return true
    }
    
    override fun visitMetadata(metadata: Metadata): Boolean {
        // Metadata should always be valid
        return true
    }
    
    override fun visitReturnInst(inst: ReturnInst): Boolean {
        val operands = inst.getOperandsList()
        val returnValue = operands.firstOrNull()
        if (returnValue != null && returnValue.type != inst.parent.function.returnType) {
            addError("Return value type ${returnValue.type} doesn't match function return type ${inst.parent.function.returnType}")
        }
        return errors.isEmpty()
    }
    
    override fun visitBranchInst(inst: BranchInst): Boolean {
        val operands = inst.getOperandsList()
        if (operands.isEmpty()) {
            addError("Branch instruction must have at least one target")
        }
        return errors.isEmpty()
    }
    
    override fun visitSwitchInst(inst: SwitchInst): Boolean {
        val operands = inst.getOperandsList()
        if (operands.size < 2) {
            addError("Switch instruction must have condition and default target")
        }
        return errors.isEmpty()
    }
    
    override fun visitAddInst(inst: AddInst): Boolean = validateBinaryInst(inst, "add")
    override fun visitSubInst(inst: SubInst): Boolean = validateBinaryInst(inst, "sub")
    override fun visitMulInst(inst: MulInst): Boolean = validateBinaryInst(inst, "mul")
    override fun visitSDivInst(inst: SDivInst): Boolean = validateBinaryInst(inst, "sdiv")
    override fun visitAndInst(inst: AndInst): Boolean = validateBinaryInst(inst, "and")
    override fun visitOrInst(inst: OrInst): Boolean = validateBinaryInst(inst, "or")
    override fun visitXorInst(inst: XorInst): Boolean = validateBinaryInst(inst, "xor")
    
    private fun validateBinaryInst(inst: Any, opName: String): Boolean {
        // Basic validation for binary instructions
        return true
    }
    
    override fun visitAllocaInst(inst: AllocaInst): Boolean {
        if (inst.name.isEmpty()) {
            addError("Alloca instruction must have a name")
        }
        return errors.isEmpty()
    }
    
    override fun visitLoadInst(inst: LoadInst): Boolean {
        val operands = inst.getOperandsList()
        if (operands.isEmpty()) {
            addError("Load instruction must have a pointer operand")
        }
        return errors.isEmpty()
    }
    
    override fun visitStoreInst(inst: StoreInst): Boolean {
        val operands = inst.getOperandsList()
        if (operands.size < 2) {
            addError("Store instruction must have value and pointer operands")
        }
        return errors.isEmpty()
    }
    
    override fun visitGetElementPtrInst(inst: GetElementPtrInst): Boolean {
        val operands = inst.getOperandsList()
        if (operands.isEmpty()) {
            addError("GetElementPtr instruction must have at least a pointer operand")
        }
        return errors.isEmpty()
    }
    
    override fun visitTruncInst(inst: TruncInst): Boolean = validateCastInst(inst, "trunc")
    override fun visitZExtInst(inst: ZExtInst): Boolean = validateCastInst(inst, "zext")
    override fun visitSExtInst(inst: SExtInst): Boolean = validateCastInst(inst, "sext")
    override fun visitBitcastInst(inst: BitcastInst): Boolean = validateCastInst(inst, "bitcast")
    
    private fun validateCastInst(inst: Any, opName: String): Boolean {
        // Basic validation for cast instructions
        return true
    }
    
    override fun visitCallInst(inst: CallInst): Boolean {
        val operands = inst.getOperandsList()
        if (operands.isEmpty()) {
            addError("Call instruction must have a callee")
        }
        return errors.isEmpty()
    }
    
    override fun visitICmpInst(inst: ICmpInst): Boolean {
        val operands = inst.getOperandsList()
        if (operands.size < 2) {
            addError("ICmp instruction must have two operands")
        }
        return errors.isEmpty()
    }
    
    override fun visitPhiNode(inst: PhiNode): Boolean {
        val operands = inst.getOperandsList()
        if (operands.size % 2 != 0) {
            addError("Phi node must have pairs of values and basic blocks")
        }
        return errors.isEmpty()
    }
}