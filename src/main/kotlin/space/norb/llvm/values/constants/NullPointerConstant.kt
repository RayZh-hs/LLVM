package space.norb.llvm.values.constants

import space.norb.llvm.core.Constant
import space.norb.llvm.core.Type
import space.norb.llvm.types.PointerType
import space.norb.llvm.types.UntypedPointerType

/**
 * Null pointer constant in LLVM IR.
 *
 * ## LLVM IR Compliance Notice
 *
 * This implementation supports both legacy typed pointers and new un-typed pointers
 * based on the migration flag. The latest LLVM IR standard has moved to un-typed pointers
 * (similar to `void*` in C) where all pointers are of a single type.
 *
 * ### Current Implementation (Migration Support)
 *
 * This NullPointerConstant can represent null values for both typed and un-typed pointer types
 * based on the migration flag in Type.useTypedPointers.
 *
 * IR output examples:
 * ```
 * ; Legacy mode (useTypedPointers = true):
 * i32* null        ; Null pointer for i32* type
 * float* null      ; Null pointer for float* type
 * %struct.MyType* null ; Null pointer for struct pointer type
 *
 * ; New mode (useTypedPointers = false):
 * ptr null         ; Null pointer for un-typed pointer type
 * ptr null         ; Same null pointer for all pointer types
 * ptr null         ; Same null pointer for all pointer types
 * ```
 *
 * Key characteristics:
 * - In legacy mode: Creates typed null pointers with explicit pointee type information
 * - In new mode: Creates un-typed null pointers that work with any pointer type
 * - Type information is preserved through context in new mode
 * - Maintains backward compatibility during migration
 *
 * ### Migration Path
 *
 * For migration details and implementation plan, see:
 * @see docs/ptr-migration-todo.md
 *
 * The migration provides:
 * - Seamless transition from typed to un-typed null pointers
 * - Backward compatibility for existing code
 * - LLVM IR compliant output in new mode
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
         * This method supports both legacy typed pointers and new un-typed pointers
         * based on the migration flag Type.useTypedPointers.
         *
         * @param elementType The element type this pointer points to (ignored in un-typed mode)
         * @return A null pointer constant of the appropriate type
         */
        fun create(elementType: Type): NullPointerConstant {
            return if (Type.useTypedPointers) {
                // Legacy mode: create typed null pointer
                NullPointerConstant(PointerType(elementType))
            } else {
                // New mode: create un-typed null pointer
                NullPointerConstant(UntypedPointerType)
            }
        }
        
        /**
         * Creates a null pointer constant with the specified pointer type.
         *
         * This method creates a null pointer with the exact pointer type provided,
         * regardless of the migration flag. Use this for explicit type control.
         *
         * @param pointerType The exact pointer type to use
         * @return A null pointer constant with the specified type
         */
        fun createWithPointerType(pointerType: Type): NullPointerConstant {
            require(pointerType.isPointerType()) { "Type must be a pointer type" }
            return NullPointerConstant(pointerType)
        }
        
        /**
         * Creates a legacy typed null pointer constant.
         *
         * This method always creates a typed null pointer regardless of the migration flag.
         * It's provided for backward compatibility and should only be used during migration.
         *
         * @param elementType The element type this pointer points to
         * @return A typed null pointer constant
         * @deprecated Use create() with Type.useTypedPointers=true or migrate to un-typed pointers
         */
        @Deprecated("Use create() with Type.useTypedPointers=true or migrate to un-typed pointers")
        fun createTyped(elementType: Type): NullPointerConstant {
            return NullPointerConstant(PointerType(elementType))
        }
        
        /**
         * Creates an un-typed null pointer constant.
         *
         * This method always creates an un-typed null pointer regardless of the migration flag.
         * It's provided for explicit un-typed pointer creation.
         *
         * @return An un-typed null pointer constant
         */
        fun createUntyped(): NullPointerConstant {
            return NullPointerConstant(UntypedPointerType)
        }
    }
}