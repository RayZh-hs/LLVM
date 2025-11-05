package space.norb.llvm.values.constants

import space.norb.llvm.core.Constant
import space.norb.llvm.core.Type
import space.norb.llvm.types.PointerType

/**
 * Null pointer constant in LLVM IR.
 *
 * This implementation uses un-typed pointers in compliance with the latest LLVM IR standard.
 * All pointers are of a single type (similar to `void*` in C).
 *
 * IR output examples:
 * ```
 * ptr null         ; Null pointer for un-typed pointer type
 * ptr null         ; Same null pointer for all pointer types
 * ptr null         ; Same null pointer for all pointer types
 * ```
 *
 * Key characteristics:
 * - Creates un-typed null pointers that work with any pointer type
 * - Type information is preserved through context
 * - LLVM IR compliant output
 */
class NullPointerConstant private constructor(
    override val type: Type
) : Constant("null", type) {
    
    /**
     * Checks if this null pointer constant represents a null value.
     * Always returns true for NullPointerConstant instances.
     *
     * @return true
     */
    override fun isNullValue(): Boolean = true
    
    companion object {
        /**
         * Creates a null pointer constant for the specified element type.
         *
         * This method creates an un-typed null pointer.
         *
         * @param elementType The element type this pointer points to (ignored in un-typed mode)
         * @return A null pointer constant of the un-typed pointer type
         */
        fun create(elementType: Type): NullPointerConstant {
            // Create un-typed null pointer
            return NullPointerConstant(PointerType)
        }
        
        /**
         * Creates a null pointer constant with the specified pointer type.
         *
         * This method creates a null pointer with the exact pointer type provided.
         *
         * @param pointerType The exact pointer type to use
         * @return A null pointer constant with the specified type
         */
        fun createWithPointerType(pointerType: Type): NullPointerConstant {
            require(pointerType.isPointerType()) { "Type must be a pointer type" }
            return NullPointerConstant(pointerType)
        }
        
        /**
         * Creates an un-typed null pointer constant.
         *
         * This method creates an un-typed null pointer.
         *
         * @return An un-typed null pointer constant
         */
        fun createUntyped(): NullPointerConstant {
            return NullPointerConstant(PointerType)
        }
    }
}