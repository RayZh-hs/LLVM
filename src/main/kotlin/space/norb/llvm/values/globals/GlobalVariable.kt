package space.norb.llvm.values.globals

import space.norb.llvm.core.Constant
import space.norb.llvm.core.Type
import space.norb.llvm.types.PointerType
import space.norb.llvm.types.UntypedPointerType
import space.norb.llvm.types.PointerCastingUtils
import space.norb.llvm.types.TypeUtils
import space.norb.llvm.structure.Module
import space.norb.llvm.enums.LinkageType

/**
 * Global variable in LLVM IR.
 *
 * ## LLVM IR Compliance Notice
 *
 * This implementation supports both legacy typed pointers and new un-typed pointers
 * based on the migration flag. The latest LLVM IR standard has moved to un-typed pointers
 * (similar to `void*` in C) where all pointers are of a single type.
 *
 * ### Current Implementation (Migration Support)
 *
 * GlobalVariable can represent global variables with both typed and un-typed pointer types
 * based on the migration flag in Type.useTypedPointers.
 *
 * IR output examples:
 * ```
 * ; Legacy mode (useTypedPointers = true):
 * @myGlobal = global i32 0           ; Global variable with i32* type
 * @myArray = global [10 x i32] zeroinitializer  ; Global array with [10 x i32]* type
 *
 * ; New mode (useTypedPointers = false):
 * @myGlobal = global i32 0           ; Global variable with ptr type
 * @myArray = global [10 x i32] zeroinitializer  ; Global array with ptr type
 * ```
 *
 * Key characteristics:
 * - In legacy mode: Global variables have typed pointers with explicit element type information
 * - In new mode: Global variables have un-typed pointers, element type is inferred from initializer
 * - Maintains backward compatibility during migration
 * - Element type information is preserved through the initializer or explicit type parameter
 */
class GlobalVariable private constructor(
    override val name: String,
    override val type: Type,
    val module: Module,
    val initializer: Constant? = null,
    val isConstantValue: Boolean = false,
    val linkage: LinkageType = LinkageType.EXTERNAL,
    val elementType: Type? = null
) : Constant(name, type) {
    
    /**
     * Checks if this global variable has an initializer.
     *
     * @return true if the global variable has an initializer, false otherwise
     */
    fun hasInitializer(): Boolean = initializer != null
    
    /**
     * Checks if this global variable is a constant (immutable).
     *
     * @return true if the global variable is constant, false otherwise
     */
    override fun isConstant(): Boolean = isConstantValue
    
    companion object {
        /**
         * Creates a global variable with the specified element type.
         *
         * This method supports both legacy typed pointers and new un-typed pointers
         * based on the migration flag Type.useTypedPointers.
         *
         * @param name The name of the global variable
         * @param elementType The element type of the global variable
         * @param module The module containing this global variable
         * @param initializer The optional initializer constant
         * @param isConstantValue Whether this global variable is constant
         * @param linkage The linkage type of the global variable
         * @return A global variable with the appropriate pointer type
         */
        fun create(
            name: String,
            elementType: Type,
            module: Module,
            initializer: Constant? = null,
            isConstantValue: Boolean = false,
            linkage: LinkageType = LinkageType.EXTERNAL
        ): GlobalVariable {
            val pointerType = PointerCastingUtils.getPointerTypeFor(elementType)
            
            return GlobalVariable(
                name = name,
                type = pointerType,
                module = module,
                initializer = initializer,
                isConstantValue = isConstantValue,
                linkage = linkage,
                elementType = if (Type.useTypedPointers) null else elementType
            )
        }
        
        /**
         * Creates a global variable with the specified pointer type.
         *
         * This method creates a global variable with the exact pointer type provided,
         * regardless of the migration flag. Use this for explicit type control.
         *
         * @param name The name of the global variable
         * @param pointerType The exact pointer type to use
         * @param module The module containing this global variable
         * @param initializer The optional initializer constant
         * @param isConstantValue Whether this global variable is constant
         * @param linkage The linkage type of the global variable
         * @return A global variable with the specified pointer type
         */
        fun createWithPointerType(
            name: String,
            pointerType: Type,
            module: Module,
            initializer: Constant? = null,
            isConstantValue: Boolean = false,
            linkage: LinkageType = LinkageType.EXTERNAL
        ): GlobalVariable {
            require(pointerType.isPointerType()) { "Type must be a pointer type" }
            
            val elementType = PointerCastingUtils.getPointerTypeElement(pointerType)
            
            return GlobalVariable(
                name = name,
                type = pointerType,
                module = module,
                initializer = initializer,
                isConstantValue = isConstantValue,
                linkage = linkage,
                elementType = if (Type.useTypedPointers) null else elementType
            )
        }
        
        /**
         * Creates a legacy typed global variable.
         *
         * This method always creates a global variable with a typed pointer regardless of the migration flag.
         * It's provided for backward compatibility and should only be used during migration.
         *
         * @param name The name of the global variable
         * @param elementType The element type of the global variable
         * @param module The module containing this global variable
         * @param initializer The optional initializer constant
         * @param isConstantValue Whether this global variable is constant
         * @param linkage The linkage type of the global variable
         * @return A global variable with a typed pointer
         * @deprecated Use create() with Type.useTypedPointers=true or migrate to un-typed pointers
         */
        @Deprecated("Use create() with Type.useTypedPointers=true or migrate to un-typed pointers")
        fun createTyped(
            name: String,
            elementType: Type,
            module: Module,
            initializer: Constant? = null,
            isConstantValue: Boolean = false,
            linkage: LinkageType = LinkageType.EXTERNAL
        ): GlobalVariable {
            val pointerType = PointerType(elementType)
            
            return GlobalVariable(
                name = name,
                type = pointerType,
                module = module,
                initializer = initializer,
                isConstantValue = isConstantValue,
                linkage = linkage,
                elementType = null
            )
        }
        
        /**
         * Creates an un-typed global variable.
         *
         * This method always creates a global variable with an un-typed pointer regardless of the migration flag.
         * It's provided for explicit un-typed pointer creation.
         *
         * @param name The name of the global variable
         * @param elementType The element type of the global variable
         * @param module The module containing this global variable
         * @param initializer The optional initializer constant
         * @param isConstantValue Whether this global variable is constant
         * @param linkage The linkage type of the global variable
         * @return A global variable with an un-typed pointer
         */
        fun createUntyped(
            name: String,
            elementType: Type,
            module: Module,
            initializer: Constant? = null,
            isConstantValue: Boolean = false,
            linkage: LinkageType = LinkageType.EXTERNAL
        ): GlobalVariable {
            return GlobalVariable(
                name = name,
                type = UntypedPointerType,
                module = module,
                initializer = initializer,
                isConstantValue = isConstantValue,
                linkage = linkage,
                elementType = elementType
            )
        }
    }
}