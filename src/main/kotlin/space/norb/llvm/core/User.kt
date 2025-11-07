package space.norb.llvm.core

/**
 * Abstract class for values that use other values as operands.
 *
 * This is a fundamental abstraction in LLVM IR for entities that reference
 * other values. Instructions are the primary users in LLVM IR, but other
 * entities like global variables can also be users.
 *
 * Users maintain a list of operands they reference, providing the foundation
 * for use-def chains and value dependency tracking in the LLVM IR system.
 *
 * This class defines the contract for all user values, including:
 * - Operand access and manipulation methods
 * - Use-def chain management (placeholder for Phase 2)
 * - Value replacement capabilities (placeholder for Phase 2)
 *
 * @param name The name of this user value
 * @param type The LLVM type of this user value
 * @param operands The list of values this user uses as operands
 */
abstract class User(
    override val name: String,
    override val type: Type,
    operands: List<Value>
) : Value {
    
    // Convert the immutable list to a mutable list for internal manipulation
    protected val operands: MutableList<Value> = operands.toMutableList()
    
    /**
     * Gets the number of operands this user has.
     *
     * @return The number of operands
     */
    fun getNumOperands(): Int = operands.size
    
    /**
     * Gets the operand at the specified index.
     *
     * @param index The index of the operand to retrieve
     * @return The operand at the specified index
     * @throws IndexOutOfBoundsException if index is out of bounds
     */
    fun getOperand(index: Int): Value {
        return operands[index]
    }
    
    /**
     * Gets all operands of this user as an immutable list.
     *
     * @return An immutable list of all operands
     */
    fun getOperandsList(): List<Value> = operands.toList()
    
    
    /**
     * Sets the operand at the specified index to a new value.
     *
     * @param index The index of the operand to replace
     * @param value The new value to set as the operand
     * @throws IndexOutOfBoundsException if index is out of bounds
     */
    fun setOperand(index: Int, value: Value) {
        operands[index] = value
    }
    
    /**
     * Adds an operand to the end of the operand list.
     *
     * @param value The value to add as an operand
     */
    protected fun addOperand(value: Value) {
        operands.add(value)
    }
    
    /**
     * Removes the operand at the specified index.
     *
     * @param index The index of the operand to remove
     * @return The removed operand
     * @throws IndexOutOfBoundsException if index is out of bounds
     */
    protected fun removeOperand(index: Int): Value {
        return operands.removeAt(index)
    }
    
    /**
     * Checks if this user has any operands.
     *
     * @return true if this user has at least one operand, false otherwise
     */
    fun hasOperands(): Boolean = operands.isNotEmpty()
    
    /**
     * Gets the index of the specified operand value.
     *
     * @param value The operand value to find
     * @return The index of the operand, or -1 if not found
     */
    fun getOperandIndex(value: Value): Int {
        return operands.indexOf(value)
    }
    
    // Placeholder methods for Phase 2 - to be implemented when use-def chains are established
    
    /**
     * Replaces all uses of a value with another value.
     * TODO: Deferred - requires use-def chain infrastructure.
     * Dependencies: Value.useList tracking, User operand registration,
     * and bidirectional use-def relationship management.
     *
     * @param oldValue The value to replace
     * @param newValue The value to replace with
     */
    fun replaceUsesOfWith(oldValue: Value, newValue: Value) {
        // Phase 1 implementation: iterate through operands and replace matching values
        for (i in operands.indices) {
            if (operands[i] == oldValue) {
                operands[i] = newValue
            }
        }
    }
    
    /**
     * Gets all users of this value.
     * TODO: Deferred - requires use-def chain infrastructure.
     * Dependencies: Value.useList tracking, User operand registration,
     * and bidirectional use-def relationship management.
     *
     * @return List of all users that use this value
     */
    fun getUsers(): List<User> {
        // Phase 1 implementation: return empty list since use-def chains aren't established
        return emptyList()
    }
    
    /**
     * Checks if this value is used by any other value.
     * TODO: Deferred - requires use-def chain infrastructure.
     * Dependencies: Value.useList tracking, User operand registration,
     * and bidirectional use-def relationship management.
     *
     * @return true if this value has any users, false otherwise
     */
    fun hasUsers(): Boolean {
        // Phase 1 implementation: return false since use-def chains aren't established
        return false
    }
}