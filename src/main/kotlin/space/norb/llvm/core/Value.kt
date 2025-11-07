package space.norb.llvm.core

/**
 * Core interface for all LLVM values.
 *
 * This is the fundamental abstraction in the LLVM IR system. All LLVM entities
 * that can be used as operands (constants, instructions, functions, etc.) implement
 * this interface. It provides the basic contract that all values must follow.
 *
 * Values in LLVM have:
 * - A type that determines what kind of value it is
 * - A name for identification (may be empty for unnamed values)
 * - Methods for querying properties and relationships
 *
 * This interface is designed to be extended by:
 * - [Constant] - Immutable constant values
 * - [User] - Values that use other values as operands (like instructions)
 * - [Function] - Function definitions
 * - [Argument] - Function arguments
 * - [GlobalVariable] - Global variables
 */
interface Value {
    /**
     * The name of this value. May be empty for unnamed values.
     * In LLVM IR, values can be named (e.g., %x, %result) or unnamed.
     */
    val name: String
    
    /**
     * The LLVM type of this value.
     * Every value in LLVM has a specific type that determines what operations
     * can be performed on it.
     */
    val type: Type
    
    /**
     * Checks if this value is a constant.
     * Constants are immutable values that can be used as operands.
     *
     * @return true if this value is a constant, false otherwise
     */
    fun isConstant(): Boolean = this is Constant
    
    /**
     * Checks if this value is a user (uses other values as operands).
     * Instructions are the primary users in LLVM IR.
     *
     * @return true if this value uses other values, false otherwise
     */
    fun isUser(): Boolean = this is User
    
    /**
     * Gets the unique identifier for this value.
     * In LLVM IR, this would be the name with appropriate prefix.
     *
     * @return Unique identifier string
     */
    fun getIdentifier(): String = if (name.isNotEmpty()) name else "unnamed"
    
    /**
     * Accepts a visitor for this value.
     * This is part of the visitor pattern implementation for IR traversal.
     *
     * @param visitor The visitor to accept
     * @param T The return type of the visitor
     * @return Result of visiting this value
     */
    fun <T> accept(visitor: space.norb.llvm.visitors.IRVisitor<T>): T
    
    /**
     * Gets the parent containing this value.
     * The parent could be a Module, Function, or BasicBlock depending on the value type:
     * - Functions belong to Modules
     * - BasicBlocks belong to Functions
     * - Instructions belong to BasicBlocks
     * - Arguments belong to Functions
     * - GlobalVariables belong to Modules
     * - Constants typically don't have parents unless they are global initializers
     */
    fun getParent(): Any? {
        // Base implementation - concrete classes should override
        // The parent could be a Module, Function, or BasicBlock depending on the value type
        return null
    }
    
    /**
     * Checks if this value has any uses.
     * TODO: Deferred - requires use-def chain infrastructure.
     * Dependencies: Value.useList tracking, User operand registration,
     * and bidirectional use-def relationship management.
     */
    fun hasUses(): Boolean {
        // Base implementation - concrete classes should override
        // This should return true if there are any users of this value
        return false
    }
    
    /**
     * Gets all uses of this value.
     * TODO: Deferred - requires use-def chain infrastructure.
     * Dependencies: Value.useList tracking, User operand registration,
     * and bidirectional use-def relationship management.
     */
    fun getUses(): List<User> {
        // Base implementation - concrete classes should override
        // This should return a list of all users that use this value
        return emptyList()
    }
}