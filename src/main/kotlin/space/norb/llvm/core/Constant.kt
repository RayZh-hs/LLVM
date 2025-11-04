package space.norb.llvm.core

import space.norb.llvm.visitors.IRVisitor

/**
 * Abstract class for constant values in LLVM IR.
 *
 * Constants are immutable values that can be used as operands in instructions.
 * All constant values inherit from this class, providing a common interface
 * for different types of constants (integers, floats, pointers, etc.).
 *
 * Constants have the following properties:
 * - They are immutable and cannot be modified after creation
 * - They can be used as operands to instructions
 * - They have a specific type that determines what operations they support
 * - They may have a name for identification (though many constants are unnamed)
 *
 * This abstract class defines the contract that all constant implementations must follow.
 * Concrete implementations include:
 * - IntConstant for integer constants
 * - FloatConstant for floating-point constants
 * - NullPointerConstant for null pointer values
 * - ArrayConstant for array constants
 * - StructConstant for struct constants
 *
 * @param name The name of this constant (may be empty for unnamed constants)
 * @param type The LLVM type of this constant
 */
abstract class Constant(
    override val name: String,
    override val type: Type
) : Value {
    
    /**
     * Accepts a visitor for this constant.
     * This is part of the visitor pattern implementation for IR traversal.
     *
     * @param visitor The visitor to accept
     * @param T The return type of the visitor
     * @return Result of visiting this constant
     */
    override fun <T> accept(visitor: IRVisitor<T>): T = visitor.visitConstant(this)
    
    /**
     * Checks if this constant can be used as a null pointer value.
     *
     * @return true if this constant represents a null pointer, false otherwise
     */
    open fun isNullValue(): Boolean = false
    
    /**
     * Checks if this constant represents the value zero for its type.
     * This is different from isNullValue() as it applies to non-pointer types.
     *
     * @return true if this constant is zero, false otherwise
     */
    open fun isZeroValue(): Boolean = false
    
    /**
     * Checks if this constant is the all-ones value for its type.
     * This is primarily used for integer types.
     *
     * @return true if this constant is all ones, false otherwise
     */
    open fun isAllOnesValue(): Boolean = false
    
    /**
     * Checks if this constant is one.
     * This is primarily used for integer types.
     *
     * @return true if this constant is one, false otherwise
     */
    open fun isOneValue(): Boolean = false
    
    /**
     * Checks if this constant is negative one.
     * This is primarily used for integer types.
     *
     * @return true if this constant is negative one, false otherwise
     */
    open fun isNegativeOneValue(): Boolean = false
    
    /**
     * Checks if this constant is the minimum value for its type.
     * For integers, this would be the most negative value.
     * For floating-point, this would be the most negative finite value.
     *
     * @return true if this constant is the minimum value, false otherwise
     */
    open fun isMinValue(): Boolean = false
    
    /**
     * Checks if this constant is the maximum value for its type.
     *
     * @return true if this constant is the maximum value, false otherwise
     */
    open fun isMaxValue(): Boolean = false
    
    /**
     * Gets the unique identifier for this constant.
     * In LLVM IR, constants typically don't have names unless explicitly specified.
     *
     * @return Unique identifier string
     */
    override fun getIdentifier(): String {
        return if (name.isNotEmpty()) name else "const_${hashCode()}"
    }
    
    // Placeholder methods for Phase 1 - to be implemented in later phases
    
    /**
     * Checks if this constant can be losslessly converted to the specified type.
     * TODO: Implement in Phase 2 when type conversion is supported
     *
     * @param targetType The type to check conversion to
     * @return true if conversion is possible without loss, false otherwise
     */
    open fun canLosslesslyConvertTo(targetType: Type): Boolean {
        TODO("Phase 1 placeholder - to be implemented in Phase 2")
    }
    
    /**
     * Attempts to convert this constant to the specified type.
     * TODO: Implement in Phase 2 when type conversion is supported
     *
     * @param targetType The type to convert to
     * @return A new constant of the target type, or null if conversion is not possible
     */
    open fun convertTo(targetType: Type): Constant? {
        TODO("Phase 1 placeholder - to be implemented in Phase 2")
    }
    
    /**
     * Gets the bit representation of this constant.
     * TODO: Implement in Phase 2 when bit-level operations are supported
     *
     * @return The bit representation as a string, or null if not applicable
     */
    open fun getBitRepresentation(): String? {
        TODO("Phase 1 placeholder - to be implemented in Phase 2")
    }
    
    /**
     * Checks if this constant has the same value as another constant.
     * This is different from equals() as it only compares the actual values,
     * not the names or other metadata.
     *
     * @param other The other constant to compare with
     * @return true if the constants have the same value, false otherwise
     */
    open fun hasSameValueAs(other: Constant): Boolean {
        TODO("Phase 1 placeholder - to be implemented in Phase 2")
    }
}