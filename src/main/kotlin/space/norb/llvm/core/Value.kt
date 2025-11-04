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
    
    // Placeholder methods for Phase 1 - to be implemented in later phases
    /**
     * Gets the parent module containing this value.
     * TODO: Implement in Phase 2 when module relationships are established
     */
    fun getParent(): Any? = null
    
    /**
     * Checks if this value has any uses.
     * TODO: Implement in Phase 2 when use-def chains are established
     */
    fun hasUses(): Boolean = false
    
    /**
     * Gets all uses of this value.
     * TODO: Implement in Phase 2 when use-def chains are established
     */
    fun getUses(): List<User> = emptyList()
}