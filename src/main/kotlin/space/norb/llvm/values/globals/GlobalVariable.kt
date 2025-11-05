package space.norb.llvm.values.globals

import space.norb.llvm.core.Constant
import space.norb.llvm.core.Type
import space.norb.llvm.types.PointerType
import space.norb.llvm.types.PointerCastingUtils
import space.norb.llvm.types.TypeUtils
import space.norb.llvm.structure.Module
import space.norb.llvm.enums.LinkageType

/**
 * Global variable in LLVM IR.
 *
 * This implementation uses un-typed pointers, complying with the latest LLVM IR standard.
 * All pointers are of a single type regardless of the element type they point to.
 *
 * IR output examples:
 * ```
 * @myGlobal = global i32 0           ; Global variable with ptr type
 * @myArray = global [10 x i32] zeroinitializer  ; Global array with ptr type
 * ```
 *
 * Key characteristics:
 * - Global variables have un-typed pointers
 * - Element type is inferred from the initializer or explicit type parameter
 * - Type information is preserved through the initializer or explicit type parameter
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
     * @return true if global variable has an initializer, false otherwise
     */
    fun hasInitializer(): Boolean = initializer != null
    
    /**
     * Checks if this global variable is a constant (immutable).
     *
     * @return true if global variable is constant, false otherwise
     */
    override fun isConstant(): Boolean = isConstantValue
    
    companion object {
        /**
         * Creates a global variable with an un-typed pointer.
         *
         * @param name The name of the global variable
         * @param module The module containing this global variable
         * @param initializer The optional initializer constant
         * @param isConstantValue Whether this global variable is constant
         * @param linkage The linkage type of the global variable
         * @return A global variable with an un-typed pointer
         */
        fun create(
            name: String,
            module: Module,
            initializer: Constant? = null,
            isConstantValue: Boolean = false,
            linkage: LinkageType = LinkageType.EXTERNAL
        ): GlobalVariable {
            val pointerType = PointerType
            
            // Infer element type from initializer if provided
            val elementType = initializer?.type
            
            return GlobalVariable(
                name = name,
                type = pointerType,
                module = module,
                initializer = initializer,
                isConstantValue = isConstantValue,
                linkage = linkage,
                elementType = elementType
            )
        }
        
        /**
         * Creates a global variable with the specified element type.
         *
         * This method creates a global variable with an explicit element type.
         * Use this when you need to specify the element type directly.
         *
         * @param name The name of the global variable
         * @param elementType The element type of the global variable
         * @param module The module containing this global variable
         * @param initializer The optional initializer constant
         * @param isConstantValue Whether this global variable is constant
         * @param linkage The linkage type of the global variable
         * @return A global variable with an un-typed pointer
         */
        fun createWithElementType(
            name: String,
            elementType: Type,
            module: Module,
            initializer: Constant? = null,
            isConstantValue: Boolean = false,
            linkage: LinkageType = LinkageType.EXTERNAL
        ): GlobalVariable {
            val pointerType = PointerType
            
            return GlobalVariable(
                name = name,
                type = pointerType,
                module = module,
                initializer = initializer,
                isConstantValue = isConstantValue,
                linkage = linkage,
                elementType = elementType
            )
        }
    }
}