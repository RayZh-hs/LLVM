package space.norb.llvm.visitors

import space.norb.llvm.structure.Module
import space.norb.llvm.structure.Function
import space.norb.llvm.structure.BasicBlock
import space.norb.llvm.structure.Argument
import space.norb.llvm.values.globals.GlobalVariable
import space.norb.llvm.core.Constant
import space.norb.llvm.core.Type
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
import space.norb.llvm.instructions.base.TerminatorInst
import space.norb.llvm.instructions.base.BinaryInst
import space.norb.llvm.instructions.base.MemoryInst
import space.norb.llvm.instructions.base.CastInst
import space.norb.llvm.instructions.base.OtherInst

/**
 * Visitor for printing LLVM IR to string format.
 *
 * This IR printer generates LLVM IR using un-typed pointers, complying with the
 * latest LLVM IR standard. All pointers are represented as "ptr" regardless of
 * the element type they point to.
 *
 * Examples of IR output:
 * ```
 * %ptr = alloca i32
 * %val = load i32, ptr %ptr
 * %gep = getelementptr [10 x i32], ptr %array, i64 0, i64 5
 * ```
 *
 * Key characteristics:
 * - All pointer types are represented as "ptr" instead of "elementType*"
 * - Type information is conveyed through other mechanisms (e.g., explicit types in instructions)
 * - Pointer operations require explicit type information where needed
 */
class IRPrinter : IRVisitor<Unit> {
    private val output = StringBuilder()
    private var indentLevel = 0
    
    fun print(module: Module): String {
        visitModule(module)
        return output.toString()
    }
    
    private fun indent() = "  ".repeat(indentLevel)
    
    override fun visitModule(module: Module) {
        output.appendLine("; Module: ${module.name}")
        module.functions.forEach { visitFunction(it) }
    }
    
    override fun visitFunction(function: Function) {
        output.appendLine()
        output.appendLine("define ${function.type} ${function.name} {")
        indentLevel++
        function.basicBlocks.forEach { visitBasicBlock(it) }
        indentLevel--
        output.appendLine("}")
    }
    
    override fun visitBasicBlock(block: BasicBlock) {
        output.appendLine()
        output.appendLine("${indent()}${block.name}:")
        indentLevel++
        block.instructions.forEach { it.accept(this) }
        block.terminator?.accept(this)
        indentLevel--
    }
    
    override fun visitArgument(argument: Argument) {
        val typeStr = if (argument.type.isPointerType()) {
            "ptr" // Use un-typed pointer syntax
        } else {
            argument.type.toString()
        }
        output.append("$typeStr ${argument.name}")
    }
    
    override fun visitGlobalVariable(globalVariable: GlobalVariable) {
        val typeStr = if (globalVariable.type.isPointerType()) {
            "ptr" // Use un-typed pointer syntax
        } else {
            globalVariable.type.toString()
        }
        output.append("@${globalVariable.name} = global $typeStr")
    }
    
    override fun visitConstant(constant: Constant) {
        output.append(constant.toString())
    }
    
    override fun visitMetadata(metadata: Metadata) {
        output.append(metadata.toIRString())
    }
    
    override fun visitReturnInst(inst: ReturnInst) {
        val operands = inst.getOperandsList()
        val value = operands.firstOrNull()
        if (value != null) {
            output.appendLine("${indent()}ret ${value.type} ${value.name}")
        } else {
            output.appendLine("${indent()}ret void")
        }
    }
    
    override fun visitBranchInst(inst: BranchInst) {
        val operands = inst.getOperandsList()
        output.appendLine("${indent()}br label ${operands.first().name}")
    }
    
    override fun visitSwitchInst(inst: SwitchInst) {
        val operands = inst.getOperandsList()
        output.appendLine("${indent()}switch ${operands[0].type} ${operands[0].name}, label ${operands[1].name} [")
        indentLevel++
        // TODO: Handle cases
        indentLevel--
        output.appendLine("${indent()}]")
    }
    
    override fun visitAddInst(inst: AddInst) {
        output.appendLine("${indent()}${inst.name} = add ${inst.lhs.type} ${inst.lhs.name}, ${inst.rhs.name}")
    }
    
    override fun visitSubInst(inst: SubInst) {
        output.appendLine("${indent()}${inst.name} = sub ${inst.lhs.type} ${inst.lhs.name}, ${inst.rhs.name}")
    }
    
    override fun visitMulInst(inst: MulInst) {
        output.appendLine("${indent()}${inst.name} = mul ${inst.lhs.type} ${inst.lhs.name}, ${inst.rhs.name}")
    }
    
    override fun visitSDivInst(inst: SDivInst) {
        output.appendLine("${indent()}${inst.name} = sdiv ${inst.lhs.type} ${inst.lhs.name}, ${inst.rhs.name}")
    }
    
    override fun visitAndInst(inst: AndInst) {
        output.appendLine("${indent()}${inst.name} = and ${inst.lhs.type} ${inst.lhs.name}, ${inst.rhs.name}")
    }
    
    override fun visitOrInst(inst: OrInst) {
        output.appendLine("${indent()}${inst.name} = or ${inst.lhs.type} ${inst.lhs.name}, ${inst.rhs.name}")
    }
    
    override fun visitXorInst(inst: XorInst) {
        output.appendLine("${indent()}${inst.name} = xor ${inst.lhs.type} ${inst.lhs.name}, ${inst.rhs.name}")
    }
    
    override fun visitAllocaInst(inst: AllocaInst) {
        output.appendLine("${indent()}${inst.name} = alloca ${inst.allocatedType}")
        // Note: alloca result type is implicit and handled by the instruction's type property
        // The instruction's type will be printed as "ptr" when accessed through other methods
    }
    
    override fun visitLoadInst(inst: LoadInst) {
        val pointer = inst.pointer
        val pointerTypeStr = if (pointer.type.isPointerType()) {
            "ptr" // Use un-typed pointer syntax
        } else {
            pointer.type.toString()
        }
        output.appendLine("${indent()}${inst.name} = load ${inst.loadedType}, $pointerTypeStr ${pointer.name}")
    }
    
    override fun visitStoreInst(inst: StoreInst) {
        val value = inst.value
        val pointer = inst.pointer
        val pointerTypeStr = if (pointer.type.isPointerType()) {
            "ptr" // Use un-typed pointer syntax
        } else {
            pointer.type.toString()
        }
        output.appendLine("${indent()}store ${value.type} ${value.name}, $pointerTypeStr ${pointer.name}")
    }
    
    override fun visitGetElementPtrInst(inst: GetElementPtrInst) {
        val pointer = inst.pointer
        val indices = inst.indices
        val pointerTypeStr = if (pointer.type.isPointerType()) {
            "ptr" // Use un-typed pointer syntax
        } else {
            pointer.type.toString()
        }
        val indicesStr = indices.joinToString(", ") { "${it.type} ${it.name}" }
        output.appendLine("${indent()}${inst.name} = getelementptr ${inst.elementType}, $pointerTypeStr ${pointer.name}, $indicesStr")
    }
    
    override fun visitTruncInst(inst: TruncInst) {
        output.appendLine("${indent()}${inst.name} = trunc ${inst.value.type} ${inst.value.name} to ${inst.type}")
    }
    
    override fun visitZExtInst(inst: ZExtInst) {
        output.appendLine("${indent()}${inst.name} = zext ${inst.value.type} ${inst.value.name} to ${inst.type}")
    }
    
    override fun visitSExtInst(inst: SExtInst) {
        output.appendLine("${indent()}${inst.name} = sext ${inst.value.type} ${inst.value.name} to ${inst.type}")
    }
    
    override fun visitBitcastInst(inst: BitcastInst) {
        val targetTypeStr = if (inst.type.isPointerType()) {
            "ptr" // Use un-typed pointer syntax
        } else {
            inst.type.toString()
        }
        output.appendLine("${indent()}${inst.name} = bitcast ${inst.value.type} ${inst.value.name} to $targetTypeStr")
    }
    
    override fun visitCallInst(inst: CallInst) {
        val operands = inst.getOperandsList()
        val calleeTypeStr = if (operands.first().type.isPointerType()) {
            "ptr" // Use un-typed pointer syntax
        } else {
            operands.first().type.toString()
        }
        output.appendLine("${indent()}${inst.name} = call $calleeTypeStr ${operands.first().name}()")
    }
    
    override fun visitICmpInst(inst: ICmpInst) {
        val operands = inst.getOperandsList()
        output.appendLine("${indent()}${inst.name} = icmp ${operands[0].type} ${operands[0].name}, ${operands[1].name}")
    }
    
    override fun visitPhiNode(inst: PhiNode) {
        val operands = inst.getOperandsList()
        output.appendLine("${indent()}${inst.name} = phi ${inst.type} [${operands.joinToString(", ") { it.name }}]")
    }
    
    override fun visitTerminatorInst(inst: TerminatorInst): Unit = when (inst) {
        is ReturnInst -> visitReturnInst(inst)
        is BranchInst -> visitBranchInst(inst)
        is SwitchInst -> visitSwitchInst(inst)
        else -> throw IllegalArgumentException("Unknown terminator instruction: ${inst::class.simpleName}")
    }
    
    override fun visitBinaryInst(inst: BinaryInst): Unit = when (inst) {
        is AddInst -> visitAddInst(inst)
        is SubInst -> visitSubInst(inst)
        is MulInst -> visitMulInst(inst)
        is SDivInst -> visitSDivInst(inst)
        is AndInst -> visitAndInst(inst)
        is OrInst -> visitOrInst(inst)
        is XorInst -> visitXorInst(inst)
        else -> throw IllegalArgumentException("Unknown binary instruction: ${inst::class.simpleName}")
    }
    
    override fun visitMemoryInst(inst: MemoryInst): Unit = when (inst) {
        is AllocaInst -> visitAllocaInst(inst)
        is LoadInst -> visitLoadInst(inst)
        is StoreInst -> visitStoreInst(inst)
        is GetElementPtrInst -> visitGetElementPtrInst(inst)
        else -> throw IllegalArgumentException("Unknown memory instruction: ${inst::class.simpleName}")
    }
    
    override fun visitCastInst(inst: CastInst): Unit = when (inst) {
        is TruncInst -> visitTruncInst(inst)
        is ZExtInst -> visitZExtInst(inst)
        is SExtInst -> visitSExtInst(inst)
        is BitcastInst -> visitBitcastInst(inst)
        else -> throw IllegalArgumentException("Unknown cast instruction: ${inst::class.simpleName}")
    }
    
    override fun visitOtherInst(inst: OtherInst): Unit = when (inst) {
        is CallInst -> visitCallInst(inst)
        is ICmpInst -> visitICmpInst(inst)
        is PhiNode -> visitPhiNode(inst)
        else -> throw IllegalArgumentException("Unknown other instruction: ${inst::class.simpleName}")
    }
}