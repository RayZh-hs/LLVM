package space.norb.llvm.instructions.memory

import space.norb.llvm.core.Value
import space.norb.llvm.core.Type
import space.norb.llvm.instructions.base.MemoryInst
import space.norb.llvm.visitors.IRVisitor
import space.norb.llvm.types.UntypedPointerType
import space.norb.llvm.types.ArrayType
import space.norb.llvm.types.StructType

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
    elementType: Type,
    pointer: Value,
    indices: List<Value>
) : MemoryInst(name,
    if (Type.useTypedPointers) Type.getPointerType(elementType) else UntypedPointerType,
    listOf(pointer) + indices) {
    
    /**
     * The element type for GEP calculations.
     * This is explicitly specified since un-typed pointers don't convey this information.
     * This represents the type of the base element that the pointer points to.
     */
    val elementType: Type = elementType
    
    /**
     * The base pointer operand for GEP calculation.
     * In un-typed mode, this should be an UntypedPointerType.
     * In typed mode (legacy), this can be a typed pointer.
     */
    val pointer: Value = pointer
    
    /**
     * The list of indices for GEP calculation.
     * Each index is used to navigate through the element type structure.
     */
    val indices: List<Value> = indices
    
    /**
     * The result pointer type of this GEP operation.
     * In un-typed mode, this is UntypedPointerType.
     * In typed mode (legacy), this is a typed pointer to the final element type.
     */
    val resultType: Type
        get() = if (Type.useTypedPointers) Type.getPointerType(elementType) else UntypedPointerType
    
    /**
     * Calculates the final element type after applying all indices.
     * This is useful for type checking and validation.
     * In un-typed mode, this calculation must be done manually.
     */
    fun getFinalElementType(): Type {
        var currentType = elementType
        
        // Skip the first index (it's for the pointer itself in GEP)
        val typeIndices = if (indices.isNotEmpty()) indices.drop(1) else indices
        
        for (index in typeIndices) {
            when {
                currentType.isArrayType() -> {
                    // For arrays, the index selects an element
                    currentType = (currentType as ArrayType).elementType
                }
                currentType.isStructType() -> {
                    // For structs, we need the index value to determine the field
                    // This is a simplified implementation - in practice, we'd need
                    // to evaluate the constant index value
                    currentType = (currentType as StructType).elementTypes.firstOrNull()
                        ?: currentType
                }
                // For primitive types, further indexing is not valid
                // This is a simplified check - real implementation would be more robust
            }
        }
        
        return currentType
    }
    
    override fun <T> accept(visitor: IRVisitor<T>): T = visitor.visitGetElementPtrInst(this)
}