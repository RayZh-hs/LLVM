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
import space.norb.llvm.instructions.other.ICmpInst
import space.norb.llvm.instructions.other.PhiNode
import space.norb.llvm.instructions.casts.BitcastInst
import space.norb.llvm.instructions.casts.SExtInst
import space.norb.llvm.instructions.casts.ZExtInst
import space.norb.llvm.instructions.casts.TruncInst
import space.norb.llvm.enums.IcmpPredicate

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
    
    fun createBasicBlock(name: String, function: Function): BasicBlock {
        return BasicBlock(name, function)
    }
    
    // Terminator methods
    fun buildRet(value: Value?): ReturnInst {
        val ret = ReturnInst("ret", value?.type ?: VoidType, value)
        return insertInstruction(ret) as ReturnInst
    }
    
    fun buildRetVoid(): ReturnInst {
        return buildRet(null)
    }
    
    fun buildBr(target: BasicBlock): BranchInst {
        val br = BranchInst.createUnconditional("br", VoidType, target)
        return insertInstruction(br) as BranchInst
    }
    
    fun buildCondBr(condition: Value, trueTarget: BasicBlock, falseTarget: BasicBlock): BranchInst {
        val br = BranchInst.createConditional("condbr", VoidType, condition, trueTarget, falseTarget)
        return insertInstruction(br) as BranchInst
    }
    
    fun buildSwitch(condition: Value, defaultDest: BasicBlock, cases: List<Pair<Value, BasicBlock>>, name: String = ""): SwitchInst {
        val sw = SwitchInst.create(if (name.isEmpty()) "switch" else name, VoidType, condition, defaultDest, cases)
        return insertInstruction(sw) as SwitchInst
    }
    
    // Binary operations
    fun buildAdd(lhs: Value, rhs: Value, name: String = ""): AddInst {
        val add = AddInst(if (name.isEmpty()) "add" else name, lhs.type, lhs, rhs)
        return insertInstruction(add) as AddInst
    }
    
    fun buildSub(lhs: Value, rhs: Value, name: String = ""): SubInst {
        val sub = SubInst.create(if (name.isEmpty()) "sub" else name, lhs, rhs)
        return insertInstruction(sub) as SubInst
    }
    
    fun buildMul(lhs: Value, rhs: Value, name: String = ""): MulInst {
        val mul = MulInst.create(if (name.isEmpty()) "mul" else name, lhs, rhs)
        return insertInstruction(mul) as MulInst
    }
    
    fun buildAnd(lhs: Value, rhs: Value, name: String = ""): AndInst {
        val and = AndInst.create(if (name.isEmpty()) "and" else name, lhs, rhs)
        return insertInstruction(and) as AndInst
    }
    
    fun buildOr(lhs: Value, rhs: Value, name: String = ""): OrInst {
        val or = OrInst.create(if (name.isEmpty()) "or" else name, lhs, rhs)
        return insertInstruction(or) as OrInst
    }
    
    fun buildXor(lhs: Value, rhs: Value, name: String = ""): XorInst {
        val xor = XorInst.create(if (name.isEmpty()) "xor" else name, lhs, rhs)
        return insertInstruction(xor) as XorInst
    }
    
    fun buildSDiv(lhs: Value, rhs: Value, name: String = ""): SDivInst {
        val sdiv = SDivInst.create(if (name.isEmpty()) "sdiv" else name, lhs, rhs)
        return insertInstruction(sdiv) as SDivInst
    }
    
    // Cast operations
    fun buildBitcast(value: Value, destType: Type, name: String = ""): BitcastInst {
        val bitcast = BitcastInst.create(if (name.isEmpty()) "bitcast" else name, value, destType)
        return insertInstruction(bitcast) as BitcastInst
    }
    
    fun buildSExt(value: Value, destType: IntegerType, name: String = ""): SExtInst {
        val sext = SExtInst.create(if (name.isEmpty()) "sext" else name, value, destType)
        return insertInstruction(sext) as SExtInst
    }
    
    fun buildZExt(value: Value, destType: IntegerType, name: String = ""): ZExtInst {
        val zext = ZExtInst.create(if (name.isEmpty()) "zext" else name, value, destType)
        return insertInstruction(zext) as ZExtInst
    }
    
    fun buildTrunc(value: Value, destType: IntegerType, name: String = ""): TruncInst {
        val trunc = TruncInst.create(if (name.isEmpty()) "trunc" else name, value, destType)
        return insertInstruction(trunc) as TruncInst
    }
    
    // Memory operations
    fun buildAlloca(allocatedType: Type, name: String = ""): AllocaInst {
        val alloca = AllocaInst(if (name.isEmpty()) "alloca" else name, allocatedType)
        return insertInstruction(alloca) as AllocaInst
    }
    
    fun buildLoad(loadedType: Type, address: Value, name: String = ""): LoadInst {
        val load = LoadInst(if (name.isEmpty()) "load" else name, loadedType, address)
        return insertInstruction(load) as LoadInst
    }
    
    fun buildStore(value: Value, address: Value): StoreInst {
        val store = StoreInst("store", value.type, value, address)
        return insertInstruction(store) as StoreInst
    }
    
    fun buildGep(elementType: Type, address: Value, indices: List<Value>, name: String = ""): GetElementPtrInst {
        val gep = GetElementPtrInst(if (name.isEmpty()) "gep" else name, elementType, address, indices)
        return insertInstruction(gep) as GetElementPtrInst
    }
    
    // Simplified methods for untyped pointer usage
    fun buildLoad(address: Value, loadedType: Type, name: String = ""): LoadInst {
        return buildLoad(loadedType, address, name)
    }
    
    fun buildGep(address: Value, elementType: Type, indices: List<Value>, name: String = ""): GetElementPtrInst {
        return buildGep(elementType, address, indices, name)
    }
    
    // Other operations
    fun buildCall(function: Function, args: List<Value>, name: String = ""): CallInst {
        val call = CallInst.createDirectCall(if (name.isEmpty()) "call" else name, function, args)
        return insertInstruction(call) as CallInst
    }
    
    fun buildIndirectCall(funcPtr: Value, args: List<Value>, returnType: Type, name: String = ""): CallInst {
        val call = CallInst.createIndirectCall(if (name.isEmpty()) "call" else name, returnType, funcPtr, args)
        return insertInstruction(call) as CallInst
    }
    
    fun buildICmp(pred: IcmpPredicate, lhs: Value, rhs: Value, name: String = ""): ICmpInst {
        val icmp = ICmpInst.create(if (name.isEmpty()) "icmp" else name, pred, lhs, rhs)
        return insertInstruction(icmp) as ICmpInst
    }
    
    fun buildPhi(type: Type, incomingValues: List<Pair<Value, BasicBlock>>, name: String = ""): PhiNode {
        val phi = PhiNode.create(if (name.isEmpty()) "phi" else name, type, incomingValues.map { Pair(it.first, it.second) })
        return insertInstruction(phi) as PhiNode
    }
    
    // Convenience methods for common operations
    fun buildNot(value: Value, name: String = ""): XorInst {
        val negOne = when {
            value.type.isIntegerType() -> {
                BuilderUtils.getIntConstant(-1, value.type as IntegerType)
            }
            else -> throw IllegalArgumentException("Not operation only supported for integer types")
        }
        return buildXor(value, negOne, name)
    }
    
    fun buildNeg(value: Value, name: String = ""): SubInst {
        val zero = when {
            value.type.isIntegerType() -> BuilderUtils.getIntConstant(0, value.type as IntegerType)
            value.type.isFloatingPointType() -> BuilderUtils.getFloatConstant(0.0, value.type as FloatingPointType)
            else -> throw IllegalArgumentException("Negation not supported for type: ${value.type}")
        }
        return buildSub(zero, value, name)
    }
}