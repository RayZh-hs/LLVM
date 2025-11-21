package space.norb.llvm.builder

import space.norb.llvm.structure.Module
import space.norb.llvm.structure.BasicBlock
import space.norb.llvm.structure.Function
import space.norb.llvm.core.Value
import space.norb.llvm.core.Type
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.VoidType
import space.norb.llvm.types.IntegerType
import space.norb.llvm.types.FloatingPointType
import space.norb.llvm.instructions.base.Instruction
import space.norb.llvm.instructions.terminators.ReturnInst
import space.norb.llvm.instructions.terminators.BranchInst
import space.norb.llvm.instructions.terminators.SwitchInst
import space.norb.llvm.instructions.binary.AddInst
import space.norb.llvm.instructions.binary.SubInst
import space.norb.llvm.instructions.binary.MulInst
import space.norb.llvm.instructions.binary.AndInst
import space.norb.llvm.instructions.binary.OrInst
import space.norb.llvm.instructions.binary.XorInst
import space.norb.llvm.instructions.binary.SDivInst
import space.norb.llvm.instructions.memory.AllocaInst
import space.norb.llvm.instructions.memory.LoadInst
import space.norb.llvm.instructions.memory.StoreInst
import space.norb.llvm.instructions.memory.GetElementPtrInst
import space.norb.llvm.instructions.other.CallInst
import space.norb.llvm.instructions.other.CommentAttachment
import space.norb.llvm.instructions.other.ICmpInst
import space.norb.llvm.instructions.other.PhiNode
import space.norb.llvm.instructions.casts.BitcastInst
import space.norb.llvm.instructions.casts.SExtInst
import space.norb.llvm.instructions.casts.ZExtInst
import space.norb.llvm.instructions.casts.TruncInst
import space.norb.llvm.enums.IcmpPredicate
import space.norb.llvm.utils.Renamer

/**
 * Builder for constructing LLVM IR.
 */
class IRBuilder(val module: Module) {
    private var currentBlock: BasicBlock? = null
    private var insertionPoint: MutableListIterator<Instruction>? = null
    
    // Positioning methods
    fun positionAtEnd(block: BasicBlock) {
        currentBlock = block
        insertionPoint = block.instructions.listIterator(block.instructions.size)
    }
    
    fun positionBefore(instruction: Instruction) {
        currentBlock = instruction.parent
        val iterator = instruction.parent.instructions.listIterator()
        while (iterator.hasNext()) {
            if (iterator.next() == instruction) {
                insertionPoint = iterator
                break
            }
        }
    }
    
    fun clearInsertionPoint() {
        currentBlock = null
        insertionPoint = null
    }

    // Comments and annotations
    fun insertComment(text: String, name: String? = null): CommentAttachment {
        val rename = name ?: Renamer.another()
        val comment = CommentAttachment(rename, text)
        return insertInstruction(comment) as CommentAttachment
    }
    
    private fun insertInstruction(instruction: Instruction): Instruction {
        val block = currentBlock ?: throw IllegalStateException("No insertion point set")
        
        // Check if this is a terminator instruction
        if (instruction is space.norb.llvm.instructions.base.TerminatorInst) {
            // Replace existing terminator if any
            block.terminator?.let { existing ->
                val index = block.instructions.indexOf(existing)
                if (index >= 0) {
                    block.instructions[index] = instruction
                } else {
                    block.instructions.add(instruction)
                }
            } ?: run {
                block.instructions.add(instruction)
            }
            block.terminator = instruction
        } else {
            // Insert at current insertion point or at end
            insertionPoint?.let { iterator ->
                iterator.add(instruction)
            } ?: run {
                block.instructions.add(instruction)
            }
        }
        
        instruction.parent = block
        return instruction
    }
    
    fun createFunction(name: String, type: FunctionType): Function {
        return Function(name, type, module)
    }
    
    /**
     * Create a function with custom parameter names.
     * This overload merges the parameter names into the type and delegates to the constructor.
     */
    fun createFunction(name: String, type: FunctionType, paramNames: List<String>): Function {
        val updatedType = type.copy(paramNames = paramNames)
        return Function(name, updatedType, module)
    }
    
    /**
     * Convenience overload to create a function with return type, parameter types, and optional parameter names.
     */
    fun createFunction(
        name: String,
        returnType: Type,
        paramTypes: List<Type>,
        paramNames: List<String>? = null,
        isVarArg: Boolean = false
    ): Function {
        val functionType = FunctionType(returnType, paramTypes, isVarArg, paramNames)
        return Function(name, functionType, module)
    }
    
    @Deprecated("Use Function.insertBasicBlock instead", ReplaceWith("function.insertBasicBlock(name)"))
    fun insertBasicBlock(name: String, function: Function): BasicBlock {
        val block = BasicBlock(name, function)
        // Automatically add the block to the function's basic blocks list
        if (function.basicBlocks.isEmpty()) {
            function.entryBlock = block
        }
        function.basicBlocks.add(block)
        return block
    }
    
    // Terminator methods
    fun insertRet(value: Value?): ReturnInst {
        val ret = ReturnInst("ret", value?.type ?: VoidType, value)
        return insertInstruction(ret) as ReturnInst
    }
    
    fun insertRetVoid(): ReturnInst {
        return insertRet(null)
    }
    
    fun insertBr(target: BasicBlock): BranchInst {
        val br = BranchInst.createUnconditional("br", VoidType, target)
        return insertInstruction(br) as BranchInst
    }
    
    fun insertCondBr(condition: Value, trueTarget: BasicBlock, falseTarget: BasicBlock): BranchInst {
        val br = BranchInst.createConditional("condbr", VoidType, condition, trueTarget, falseTarget)
        return insertInstruction(br) as BranchInst
    }
    
    fun insertSwitch(condition: Value, defaultDest: BasicBlock, cases: List<Pair<Value, BasicBlock>>, name: String? = null): SwitchInst {
        val rename = name ?: Renamer.another()
        val sw = SwitchInst.create(rename, VoidType, condition, defaultDest, cases)
        return insertInstruction(sw) as SwitchInst
    }
    
    // Binary operations
    fun insertAdd(lhs: Value, rhs: Value, name: String? = null): AddInst {
        val rename = name ?: Renamer.another()
        val add = AddInst(rename, lhs.type, lhs, rhs)
        return insertInstruction(add) as AddInst
    }
    
    fun insertSub(lhs: Value, rhs: Value, name: String? = null): SubInst {
        val rename = name ?: Renamer.another()
        val sub = SubInst.create(rename, lhs, rhs)
        return insertInstruction(sub) as SubInst
    }
    
    fun insertMul(lhs: Value, rhs: Value, name: String? = null): MulInst {
        val rename = name ?: Renamer.another()
        val mul = MulInst.create(rename, lhs, rhs)
        return insertInstruction(mul) as MulInst
    }
    
    fun insertAnd(lhs: Value, rhs: Value, name: String? = null): AndInst {
        val rename = name ?: Renamer.another()
        val and = AndInst.create(rename, lhs, rhs)
        return insertInstruction(and) as AndInst
    }
    
    fun insertOr(lhs: Value, rhs: Value, name: String? = null): OrInst {
        val rename = name ?: Renamer.another()
        val or = OrInst.create(rename, lhs, rhs)
        return insertInstruction(or) as OrInst
    }
    
    fun insertXor(lhs: Value, rhs: Value, name: String? = null): XorInst {
        val rename = name ?: Renamer.another()
        val xor = XorInst.create(rename, lhs, rhs)
        return insertInstruction(xor) as XorInst
    }
    
    fun insertSDiv(lhs: Value, rhs: Value, name: String? = null): SDivInst {
        val rename = name ?: Renamer.another()
        val sdiv = SDivInst.create(rename, lhs, rhs)
        return insertInstruction(sdiv) as SDivInst
    }
    
    // Cast operations
    fun insertBitcast(value: Value, destType: Type, name: String? = null): BitcastInst {
        val rename = name ?: Renamer.another()
        val bitcast = BitcastInst.create(rename, value, destType)
        return insertInstruction(bitcast) as BitcastInst
    }
    
    fun insertSExt(value: Value, destType: IntegerType, name: String? = null): SExtInst {
        val rename = name ?: Renamer.another()
        val sext = SExtInst.create(rename, value, destType)
        return insertInstruction(sext) as SExtInst
    }
    
    fun insertZExt(value: Value, destType: IntegerType, name: String? = null): ZExtInst {
        val rename = name ?: Renamer.another()
        val zext = ZExtInst.create(rename, value, destType)
        return insertInstruction(zext) as ZExtInst
    }
    
    fun insertTrunc(value: Value, destType: IntegerType, name: String? = null): TruncInst {
        val rename = name ?: Renamer.another()
        val trunc = TruncInst.create(rename, value, destType)
        return insertInstruction(trunc) as TruncInst
    }
    
    // Memory operations
    fun insertAlloca(allocatedType: Type, name: String? = null): AllocaInst {
        val rename = name ?: Renamer.another()
        val alloca = AllocaInst(rename, allocatedType)
        return insertInstruction(alloca) as AllocaInst
    }

    fun insertLoad(loadedType: Type, address: Value, name: String? = null): LoadInst {
        val rename = name ?: Renamer.another()
        val load = LoadInst(rename, loadedType, address)
        return insertInstruction(load) as LoadInst
    }
    
    fun insertStore(value: Value, address: Value): StoreInst {
        val store = StoreInst("store", value.type, value, address)
        return insertInstruction(store) as StoreInst
    }
    
    fun insertGep(elementType: Type, address: Value, indices: List<Value>, name: String? = null): GetElementPtrInst {
        val rename = name ?: Renamer.another()
        val gep = GetElementPtrInst(rename, elementType, address, indices)
        return insertInstruction(gep) as GetElementPtrInst
    }

    // Other operations
    fun insertCall(function: Function, args: List<Value>, name: String? = null): CallInst {
        val rename = name ?: Renamer.another()
        val call = CallInst.createDirectCall(rename, function, args)
        return insertInstruction(call) as CallInst
    }
    
    fun insertIndirectCall(funcPtr: Value, args: List<Value>, returnType: Type, name: String? = null): CallInst {
        val rename = name ?: Renamer.another()
        val call = CallInst.createIndirectCall(rename, returnType, funcPtr, args)
        return insertInstruction(call) as CallInst
    }
    
    fun insertICmp(pred: IcmpPredicate, lhs: Value, rhs: Value, name: String? = null): ICmpInst {
        val rename = name ?: Renamer.another()
        val icmp = ICmpInst.create(rename, pred, lhs, rhs)
        return insertInstruction(icmp) as ICmpInst
    }
    
    fun insertPhi(type: Type, incomingValues: List<Pair<Value, BasicBlock>>, name: String? = null): PhiNode {
        val rename = name ?: Renamer.another()
        val phi = PhiNode.create(rename, type, incomingValues.map { Pair(it.first, it.second) })
        return insertInstruction(phi) as PhiNode
    }
    
    // Convenience methods for common operations
    fun insertNot(value: Value, name: String? = null): XorInst {
        val negOne = when {
            value.type.isIntegerType() -> {
                BuilderUtils.getIntConstant(-1, value.type as IntegerType)
            }
            else -> throw IllegalArgumentException("Not operation only supported for integer types")
        }
        return insertXor(value, negOne, name)
    }
    
    fun insertNeg(value: Value, name: String? = null): SubInst {
        val zero = when {
            value.type.isIntegerType() -> BuilderUtils.getIntConstant(0, value.type as IntegerType)
            value.type.isFloatingPointType() -> BuilderUtils.getFloatConstant(0.0, value.type as FloatingPointType)
            else -> throw IllegalArgumentException("Negation not supported for type: ${value.type}")
        }
        return insertSub(zero, value, name)
    }
}
