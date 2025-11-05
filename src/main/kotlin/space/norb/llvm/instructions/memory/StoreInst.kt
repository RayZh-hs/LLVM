package space.norb.llvm.instructions.memory

import space.norb.llvm.core.Value
import space.norb.llvm.core.Type
import space.norb.llvm.instructions.base.MemoryInst
import space.norb.llvm.visitors.IRVisitor
import space.norb.llvm.types.UntypedPointerType

/**
 * Store value to memory instruction.
 *
 * ## LLVM IR Compliance Notice
 *
 * **IMPORTANT**: This instruction implementation uses the legacy typed pointer model
 * which does NOT comply with the latest LLVM IR standard. The current LLVM IR standard
 * has moved to un-typed pointers (similar to `void*` in C) where all pointers are of a single type.
 *
 * ### Current Implementation (Legacy Model)
 *
 * This StoreInst stores a value to a typed pointer where the pointer type includes
 * explicit pointee type information. This is the legacy LLVM IR model that has been deprecated.
 *
 * Current IR output example:
 * ```
 * store i32 42, i32* %ptr     ; Stores to typed pointer i32*
 * store float 3.14, float* %fptr ; Stores to typed pointer float*
 * ```
 *
 * ### Target Implementation (LLVM IR Compliant)
 *
 * The target implementation should store a value to an un-typed pointer:
 * ```
 * store i32 42, ptr %ptr      ; Stores to un-typed pointer ptr
 * store float 3.14, ptr %fptr  ; Stores to un-typed pointer ptr
 * ```
 *
 * Key differences in target implementation:
 * - All store instructions operate on "ptr" type regardless of element type
 * - Value type is explicitly specified in the store instruction
 * - Pointer type no longer conveys element type information
 * - Type safety must be ensured through explicit type checking
 *
 * ### Migration Path
 *
 * For migration details and implementation plan, see:
 * @see docs/ptr-migration-todo.md
 *
 * The migration will:
 * - Update this instruction to work with un-typed pointer operands
 * - Ensure value type is explicitly specified and validated
 * - Update all dependent code that expects typed pointer operands
 * - Ensure all generated IR complies with latest LLVM IR standard
 */
class StoreInst(
    name: String,
    storedType: Type,
    value: Value,
    pointer: Value
) : MemoryInst(name, storedType, listOf(value, pointer)) {
    
    /**
     * The type of value being stored to memory.
     * This is explicitly specified since un-typed pointers don't convey this information.
     */
    val storedType: Type = storedType
    
    /**
     * The value to be stored.
     * The type of this value should match storedType.
     */
    val value: Value = value
    
    /**
     * The pointer operand to which to store the value.
     * In un-typed mode, this should be an UntypedPointerType.
     * In typed mode (legacy), this can be a typed pointer.
     */
    val pointer: Value = pointer
    
    /**
     * The expected pointer type for this store operation.
     * In un-typed mode, this is UntypedPointerType.
     * In typed mode (legacy), this is a typed pointer to the stored type.
     */
    val expectedPointerType: Type
        get() = if (Type.useTypedPointers) Type.getPointerType(storedType) else UntypedPointerType
    
    override fun <T> accept(visitor: IRVisitor<T>): T = visitor.visitStoreInst(this)
}