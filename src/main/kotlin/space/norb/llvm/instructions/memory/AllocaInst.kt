package space.norb.llvm.instructions.memory

import space.norb.llvm.core.Value
import space.norb.llvm.core.Type
import space.norb.llvm.instructions.base.MemoryInst
import space.norb.llvm.visitors.IRVisitor
import space.norb.llvm.types.UntypedPointerType

/**
 * Stack memory allocation instruction.
 *
 * ## LLVM IR Compliance Notice
 *
 * **IMPORTANT**: This instruction implementation uses the legacy typed pointer model
 * which does NOT comply with the latest LLVM IR standard. The current LLVM IR standard
 * has moved to un-typed pointers (similar to `void*` in C) where all pointers are of a single type.
 *
 * ### Current Implementation (Legacy Model)
 *
 * This AllocaInst allocates memory and returns a typed pointer where the pointer type
 * includes explicit pointee type information. This is the legacy LLVM IR model that has been deprecated.
 *
 * Current IR output example:
 * ```
 * %ptr = alloca i32        ; Returns i32* (typed pointer)
 * %fptr = alloca float     ; Returns float* (typed pointer)
 * ```
 *
 * ### Target Implementation (LLVM IR Compliant)
 *
 * The target implementation should allocate memory and return an un-typed pointer:
 * ```
 * %ptr = alloca i32        ; Returns ptr (un-typed pointer)
 * %fptr = alloca float     ; Returns ptr (un-typed pointer)
 * ```
 *
 * Key differences in target implementation:
 * - All alloca instructions return "ptr" type regardless of element type
 * - Element type information is conveyed through the alloca type parameter
 * - Type information may need to be tracked separately for later operations
 *
 * ### Migration Path
 *
 * For migration details and implementation plan, see:
 * @see docs/ptr-migration-todo.md
 *
 * The migration will:
 * - Update this instruction to return un-typed pointer type
 * - Modify type parameter handling to maintain element type information
 * - Update all dependent code that expects typed pointers from alloca
 * - Ensure all generated IR complies with latest LLVM IR standard
 */
class AllocaInst(
    name: String,
    allocatedType: Type,
    arraySize: Value? = null
) : MemoryInst(name,
    if (Type.useTypedPointers) Type.getPointerType(allocatedType) else UntypedPointerType,
    if (arraySize != null) listOf(arraySize) else emptyList()) {
    
    /**
     * The type of memory being allocated.
     * This is preserved for type information even when using un-typed pointers.
     */
    val allocatedType: Type = allocatedType
    
    /**
     * The pointer type returned by this alloca instruction.
     * In un-typed mode, this will be UntypedPointerType.
     * In typed mode (legacy), this will be a typed pointer.
     */
    val resultType: Type
        get() = if (Type.useTypedPointers) Type.getPointerType(allocatedType) else UntypedPointerType
    
    override fun <T> accept(visitor: IRVisitor<T>): T = visitor.visitAllocaInst(this)
}