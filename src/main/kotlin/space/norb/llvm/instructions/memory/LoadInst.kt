package space.norb.llvm.instructions.memory

import space.norb.llvm.core.Value
import space.norb.llvm.core.Type
import space.norb.llvm.instructions.base.MemoryInst
import space.norb.llvm.visitors.IRVisitor
import space.norb.llvm.types.UntypedPointerType

/**
 * Load value from memory instruction.
 *
 * ## LLVM IR Compliance Notice
 *
 * **IMPORTANT**: This instruction implementation uses the legacy typed pointer model
 * which does NOT comply with the latest LLVM IR standard. The current LLVM IR standard
 * has moved to un-typed pointers (similar to `void*` in C) where all pointers are of a single type.
 *
 * ### Current Implementation (Legacy Model)
 *
 * This LoadInst loads a value from a typed pointer where the pointer type includes
 * explicit pointee type information. This is the legacy LLVM IR model that has been deprecated.
 *
 * Current IR output example:
 * ```
 * %val = load i32, i32* %ptr     ; Loads from typed pointer i32*
 * %fval = load float, float* %fptr ; Loads from typed pointer float*
 * ```
 *
 * ### Target Implementation (LLVM IR Compliant)
 *
 * The target implementation should load a value from an un-typed pointer:
 * ```
 * %val = load i32, ptr %ptr      ; Loads from un-typed pointer ptr
 * %fval = load float, ptr %fptr  ; Loads from un-typed pointer ptr
 * ```
 *
 * Key differences in target implementation:
 * - All load instructions operate on "ptr" type regardless of element type
 * - Result type is explicitly specified in the load instruction
 * - Pointer type no longer conveys element type information
 * - Type safety must be ensured through other mechanisms
 *
 * ### Migration Path
 *
 * For migration details and implementation plan, see:
 * @see docs/ptr-migration-todo.md
 *
 * The migration will:
 * - Update this instruction to work with un-typed pointer operands
 * - Ensure result type is explicitly specified and validated
 * - Update all dependent code that expects typed pointer operands
 * - Ensure all generated IR complies with latest LLVM IR standard
 */
class LoadInst(
    name: String,
    loadedType: Type,
    pointer: Value
) : MemoryInst(name, loadedType, listOf(pointer)) {
    
    /**
     * The type of value being loaded from memory.
     * This is explicitly specified since un-typed pointers don't convey this information.
     */
    val loadedType: Type = loadedType
    
    /**
     * The pointer operand from which to load the value.
     * In un-typed mode, this should be an UntypedPointerType.
     * In typed mode (legacy), this can be a typed pointer.
     */
    val pointer: Value = pointer
    
    /**
     * The expected pointer type for this load operation.
     * In un-typed mode, this is UntypedPointerType.
     * In typed mode (legacy), this is a typed pointer to the loaded type.
     */
    val expectedPointerType: Type
        get() = if (Type.useTypedPointers) Type.getPointerType(loadedType) else UntypedPointerType
    
    override fun <T> accept(visitor: IRVisitor<T>): T = visitor.visitLoadInst(this)
}