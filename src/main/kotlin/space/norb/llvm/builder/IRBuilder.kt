package space.norb.llvm.builder

import space.norb.llvm.structure.Module
import space.norb.llvm.structure.BasicBlock
import space.norb.llvm.structure.Function
import space.norb.llvm.core.Value
import space.norb.llvm.core.Type
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.VoidType
import space.norb.llvm.types.IntegerType
import space.norb.llvm.instructions.base.Instruction
import space.norb.llvm.instructions.terminators.ReturnInst
import space.norb.llvm.instructions.terminators.BranchInst
import space.norb.llvm.instructions.binary.AddInst
import space.norb.llvm.instructions.binary.SubInst
import space.norb.llvm.instructions.binary.MulInst
import space.norb.llvm.instructions.memory.AllocaInst
import space.norb.llvm.instructions.memory.LoadInst
import space.norb.llvm.instructions.memory.StoreInst
import space.norb.llvm.instructions.memory.GetElementPtrInst
import space.norb.llvm.instructions.other.CallInst
import space.norb.llvm.instructions.other.ICmpInst
import space.norb.llvm.enums.IcmpPredicate

/**
 * Builder for constructing LLVM IR.
 */
class IRBuilder(val module: Module) {
    private var currentBlock: BasicBlock? = null
    private var insertionPoint: MutableListIterator<Instruction>? = null
    
    // TODO: Implement positioning methods
    fun positionAtEnd(block: BasicBlock) { /* implementation */ }
    fun positionBefore(instruction: Instruction) { /* implementation */ }
    fun clearInsertionPoint() { /* implementation */ }
    
    // TODO: Implement IR construction methods
    fun createFunction(name: String, type: FunctionType): Function {
        return Function(name, type, module)
    }
    
    fun createBasicBlock(name: String, function: Function): BasicBlock {
        return BasicBlock(name, function)
    }
    
    // TODO: Implement instruction building methods
    // Terminator methods
    fun buildRet(value: Value?): ReturnInst {
        return ReturnInst("ret", value?.type ?: VoidType, value)
    }
    
    fun buildBr(target: BasicBlock): BranchInst {
        return BranchInst("br", VoidType, target)
    }
    
    fun buildCondBr(condition: Value, trueTarget: BasicBlock, falseTarget: BasicBlock): BranchInst {
        return BranchInst("condbr", VoidType, condition, trueTarget, falseTarget)
    }
    
    // Binary operations
    fun buildAdd(lhs: Value, rhs: Value, name: String = ""): AddInst {
        return AddInst(if (name.isEmpty()) "add" else name, lhs.type, lhs, rhs)
    }
    
    fun buildSub(lhs: Value, rhs: Value, name: String = ""): SubInst {
        return SubInst(if (name.isEmpty()) "sub" else name, lhs.type, lhs, rhs)
    }
    
    fun buildMul(lhs: Value, rhs: Value, name: String = ""): MulInst {
        return MulInst(if (name.isEmpty()) "mul" else name, lhs.type, lhs, rhs)
    }
    
    // Memory operations
    fun buildAlloca(allocatedType: Type, name: String = ""): AllocaInst {
        return AllocaInst(if (name.isEmpty()) "alloca" else name, allocatedType)
    }
    
    fun buildLoad(loadedType: Type, address: Value, name: String = ""): LoadInst {
        return LoadInst(if (name.isEmpty()) "load" else name, loadedType, address)
    }
    
    fun buildStore(value: Value, address: Value): StoreInst {
        return StoreInst("store", value.type, value, address)
    }
    
    fun buildGep(elementType: Type, address: Value, indices: List<Value>, name: String = ""): GetElementPtrInst {
        return GetElementPtrInst(if (name.isEmpty()) "gep" else name, elementType, address, indices)
    }
    
    // Simplified methods for untyped pointer usage
    fun buildLoad(address: Value, loadedType: Type, name: String = ""): LoadInst {
        return LoadInst(if (name.isEmpty()) "load" else name, loadedType, address)
    }
    
    fun buildGep(address: Value, elementType: Type, indices: List<Value>, name: String = ""): GetElementPtrInst {
        return GetElementPtrInst(if (name.isEmpty()) "gep" else name, elementType, address, indices)
    }
    
    // Other operations
    fun buildCall(function: Function, args: List<Value>, name: String = ""): CallInst {
        return CallInst(if (name.isEmpty()) "call" else name, function.type.returnType, function, args)
    }
    
    fun buildICmp(pred: IcmpPredicate, lhs: Value, rhs: Value, name: String = ""): ICmpInst {
        return ICmpInst(if (name.isEmpty()) "icmp" else name, IntegerType(1), pred, lhs, rhs)
    }
}