package space.norb.llvm.instructions.memory

import space.norb.llvm.core.Value
import space.norb.llvm.core.Type
import space.norb.llvm.instructions.base.MemoryInst
import space.norb.llvm.visitors.IRVisitor
import space.norb.llvm.types.PointerType
import space.norb.llvm.types.ArrayType
import space.norb.llvm.types.StructType

/**
 * Get element pointer calculation instruction.
 *
 * This instruction calculates a pointer to a subelement of an aggregate data structure.
 * It operates on un-typed pointers, complying with the latest LLVM IR standard.
 * All pointers are of a single type regardless of the element type they point to.
 *
 * IR output example:
 * ```
 * %gep = getelementptr [10 x i32], ptr %array, i64 0, i64 5  ; Uses un-typed pointer
 * %fgep = getelementptr float, ptr %fptr, i64 3                 ; Uses un-typed pointer
 * ```
 *
 * Key characteristics:
 * - All GEP instructions operate on "ptr" type regardless of element type
 * - Element type is explicitly specified as the first parameter
 * - Pointer type no longer conveys element type information
 * - Type safety is ensured through explicit type checking and validation
 */
class GetElementPtrInst(
    name: String,
    elementType: Type,
    pointer: Value,
    indices: List<Value>
) : MemoryInst(name,
    PointerType,
    listOf(pointer) + indices) {
    
    /**
     * The element type for GEP calculations.
     * This is explicitly specified since un-typed pointers don't convey this information.
     * This represents the type of the base element that the pointer points to.
     */
    val elementType: Type = elementType
    
    /**
     * The base pointer operand for GEP calculation.
     * In un-typed mode, this should be a PointerType.
     */
    val pointer: Value = pointer
    
    /**
     * The list of indices for GEP calculation.
     * Each index is used to navigate through the element type structure.
     */
    val indices: List<Value> = indices
    
    /**
     * The result pointer type of this GEP operation.
     * In un-typed mode, this is PointerType.
     */
    val resultType: Type
        get() = PointerType
    
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
                // This is a simplified check - a real implementation would be more robust
            }
        }
        
        return currentType
    }
    
    override fun <T> accept(visitor: IRVisitor<T>): T = visitor.visitGetElementPtrInst(this)
}