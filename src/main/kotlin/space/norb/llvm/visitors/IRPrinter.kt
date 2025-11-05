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
 * Visitor for printing LLVM IR to string format.
 *
 * ## LLVM IR Compliance Notice
 *
 * **IMPORTANT**: This IR printer generates LLVM IR using the legacy typed pointer model
 * which does NOT comply with the latest LLVM IR standard. The current LLVM IR standard
 * has moved to un-typed pointers (similar to `void*` in C) where all pointers are of a single type.
 *
 * ### Current Implementation (Legacy Model)
 *
 * This IR printer generates typed pointer syntax where each pointer includes pointee type
 * information in the IR output (e.g., "i32*", "float*", "%struct.MyType*"). This is the
 * legacy LLVM IR model that has been deprecated.
 *
 * Examples of current IR output:
 * ```
 * %ptr = alloca i32
 * %val = load i32, i32* %ptr
 * %gep = getelementptr [10 x i32], [10 x i32]* %array, i64 0, i64 5
 * ```
 *
 * ### Target Implementation (LLVM IR Compliant)
 *
 * The target implementation should generate un-typed pointer syntax:
 * ```
 * %ptr = alloca i32
 * %val = load i32, ptr %ptr
 * %gep = getelementptr [10 x i32], ptr %array, i64 0, i64 5
 * ```
 *
 * Key differences in target IR output:
 * - All pointer types are represented as "ptr" instead of "elementType*"
 * - Type information is conveyed through other mechanisms (e.g., metadata, type casts)
 * - Pointer operations require explicit type information where needed
 *
 * ### Migration Path
 *
 * For migration details and implementation plan, see:
 * @see docs/ptr-migration-todo.md
 *
 * The migration will:
 * - Update all pointer type printing to use "ptr" syntax
 * - Modify memory instruction printing to handle un-typed pointers
 * - Update GEP instruction printing for un-typed pointer context
 * - Ensure all generated IR complies with latest LLVM IR standard
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
        output.append("${argument.type} ${argument.name}")
    }
    
    override fun visitGlobalVariable(globalVariable: GlobalVariable) {
        output.append("@${globalVariable.name} = global ${globalVariable.type}")
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
        // TODO: Update for un-typed pointer compliance
        // Current: allocates typed pointer (legacy model)
        // Target: should allocate un-typed pointer "ptr" with element type info elsewhere
        output.appendLine("${indent()}${inst.name} = alloca ${inst.type}")
    }
    
    override fun visitLoadInst(inst: LoadInst) {
        // TODO: Update for un-typed pointer compliance
        // Current: loads from typed pointer (legacy model)
        // Target: should load from un-typed pointer "ptr" with explicit result type
        val operands = inst.getOperandsList()
        output.appendLine("${indent()}${inst.name} = load ${operands.first().type}, ${operands.first().name}")
    }
    
    override fun visitStoreInst(inst: StoreInst) {
        // TODO: Update for un-typed pointer compliance
        // Current: stores to typed pointer (legacy model)
        // Target: should store to un-typed pointer "ptr" with explicit value type
        val operands = inst.getOperandsList()
        output.appendLine("${indent()}store ${operands[0].type} ${operands[0].name}, ${operands[1].type} ${operands[1].name}")
    }
    
    override fun visitGetElementPtrInst(inst: GetElementPtrInst) {
        // TODO: Update for un-typed pointer compliance
        // Current: GEP with typed pointer (legacy model)
        // Target: GEP with un-typed pointer "ptr" and explicit element type info
        val operands = inst.getOperandsList()
        output.appendLine("${indent()}${inst.name} = getelementptr ${operands.first().type}, ${operands.first().name}")
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
        output.appendLine("${indent()}${inst.name} = bitcast ${inst.value.type} ${inst.value.name} to ${inst.type}")
    }
    
    override fun visitCallInst(inst: CallInst) {
        val operands = inst.getOperandsList()
        output.appendLine("${indent()}${inst.name} = call ${operands.first().type} ${operands.first().name}()")
    }
    
    override fun visitICmpInst(inst: ICmpInst) {
        val operands = inst.getOperandsList()
        output.appendLine("${indent()}${inst.name} = icmp ${operands[0].type} ${operands[0].name}, ${operands[1].name}")
    }
    
    override fun visitPhiNode(inst: PhiNode) {
        val operands = inst.getOperandsList()
        output.appendLine("${indent()}${inst.name} = phi ${inst.type} [${operands.joinToString(", ") { it.name }}]")
    }
}