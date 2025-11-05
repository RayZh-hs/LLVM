package space.norb.llvm.instructions.memory

import space.norb.llvm.core.Value
import space.norb.llvm.core.Type
import space.norb.llvm.instructions.base.MemoryInst
import space.norb.llvm.visitors.IRVisitor

/**
 * Get element pointer calculation instruction.
 *
 * ## LLVM IR Compliance Notice
 *
 * **IMPORTANT**: This instruction implementation uses the legacy typed pointer model
 * which does NOT comply with the latest LLVM IR standard. The current LLVM IR standard
 * has moved to un-typed pointers (similar to `void*` in C) where all pointers are of a single type.
 *
 * ### Current Implementation (Legacy Model)
 *
 * This GetElementPtrInst operates on typed pointers where the pointer type includes
 * explicit pointee type information. This is the legacy LLVM IR model that has been deprecated.
 *
 * Current IR output example:
 * ```
 * %gep = getelementptr [10 x i32], [10 x i32]* %array, i64 0, i64 5  ; Uses typed pointer
 * %fgep = getelementptr float, float* %fptr, i64 3                 ; Uses typed pointer
 * ```
 *
 * ### Target Implementation (LLVM IR Compliant)
 *
 * The target implementation should operate on un-typed pointers:
 * ```
 * %gep = getelementptr [10 x i32], ptr %array, i64 0, i64 5  ; Uses un-typed pointer
 * %fgep = getelementptr float, ptr %fptr, i64 3                 ; Uses un-typed pointer
 * ```
 *
 * Key differences in target implementation:
 * - All GEP instructions operate on "ptr" type regardless of element type
 * - Element type is explicitly specified as the first parameter
 * - Pointer type no longer conveys element type information
 * - Type safety must be ensured through explicit type checking and validation
 * - Index calculations must be updated to work with un-typed pointers
 *
 * ### Migration Path
 *
 * For migration details and implementation plan, see:
 * @see docs/ptr-migration-todo.md
 *
 * The migration will:
 * - Update this instruction to work with un-typed pointer operands
 * - Ensure element type is explicitly specified and validated
 * - Update index calculation logic for un-typed pointer context
 * - Update all dependent code that expects typed pointer operands
 * - Ensure all generated IR complies with latest LLVM IR standard
 */
class GetElementPtrInst(
    name: String,
    type: Type,
    pointer: Value,
    indices: List<Value>
) : MemoryInst(name, type, listOf(pointer) + indices) {
    override fun <T> accept(visitor: IRVisitor<T>): T = visitor.visitGetElementPtrInst(this)
}