package space.norb.llvm.core

/**
 * Abstract class representing all LLVM types.
 *
 * This is the core abstraction for all LLVM types in the IR system.
 * All LLVM types inherit from this abstract class, providing a common
 * interface for type operations and properties.
 *
 * Primitive types are defined in types/PrimitiveTypes.kt
 * Derived types are defined in types/DerivedTypes.kt
 */
abstract class Type {
    
    /**
     * Returns a string representation of this type in LLVM IR format.
     *
     * @return The LLVM IR string representation of this type
     */
    abstract override fun toString(): String
    
    /**
     * Checks if this type is a primitive type (void, integer, floating-point).
     *
     * @return true if this is a primitive type, false otherwise
     */
    abstract fun isPrimitiveType(): Boolean
    
    /**
     * Checks if this type is a derived type (pointer, function, array, struct).
     *
     * @return true if this is a derived type, false otherwise
     */
    abstract fun isDerivedType(): Boolean
    
    /**
     * Checks if this type can be used in integer operations.
     *
     * @return true if this type supports integer operations, false otherwise
     */
    abstract fun isIntegerType(): Boolean
    
    /**
     * Checks if this type can be used in floating-point operations.
     *
     * @return true if this type supports floating-point operations, false otherwise
     */
    abstract fun isFloatingPointType(): Boolean
    
    /**
     * Checks if this type is a pointer type.
     *
     * @return true if this is a pointer type, false otherwise
     */
    abstract fun isPointerType(): Boolean
    
    /**
     * Checks if this type is a function type.
     *
     * @return true if this is a function type, false otherwise
     */
    abstract fun isFunctionType(): Boolean
    
    /**
     * Checks if this type is an array type.
     *
     * @return true if this is an array type, false otherwise
     */
    abstract fun isArrayType(): Boolean
    
    /**
     * Checks if this type is a struct type.
     *
     * @return true if this is a struct type, false otherwise
     */
    abstract fun isStructType(): Boolean
    
    /**
     * Returns the size of this type in bits, if known.
     *
     * @return The size in bits, or null if not applicable/unknown
     */
    abstract fun getPrimitiveSizeInBits(): Int?
    
    /**
     * Abstract class representing all primitive LLVM types.
     * These are the basic building blocks of the LLVM type system.
     */
    abstract class PrimitiveType : Type() {
        override fun isPrimitiveType(): Boolean = true
        override fun isDerivedType(): Boolean = false
    }
    
    /**
     * Abstract class representing all derived LLVM types.
     * These types are constructed from other types.
     */
    abstract class DerivedType : Type() {
        override fun isPrimitiveType(): Boolean = false
        override fun isDerivedType(): Boolean = true
    }
    
    companion object {
        /**
         * Creates a void type instance.
         *
         * @return The void type
         */
        fun getVoidType(): Type {
            TODO("Phase 1 placeholder - to be implemented")
        }
        
        /**
         * Creates an integer type with the specified bit width.
         *
         * @param bitWidth The number of bits for the integer type
         * @return An integer type with the specified bit width
         */
        fun getIntegerType(bitWidth: Int): Type {
            TODO("Phase 1 placeholder - to be implemented")
        }
        
        /**
         * Creates a floating-point type.
         *
         * @param isDouble Whether to create a double precision type (true) or single precision (false)
         * @return A floating-point type
         */
        fun getFloatingPointType(isDouble: Boolean = false): Type {
            TODO("Phase 1 placeholder - to be implemented")
        }
        
        /**
         * Creates a pointer type pointing to the specified element type.
         *
         * @param pointeeType The type this pointer points to
         * @return A pointer type
         */
        fun getPointerType(pointeeType: Type): Type {
            TODO("Phase 1 placeholder - to be implemented")
        }
        
        /**
         * Creates a function type with the specified return type and parameters.
         *
         * @param returnType The return type of the function
         * @param paramTypes The parameter types of the function
         * @param isVarArg Whether the function is variadic
         * @return A function type
         */
        fun getFunctionType(returnType: Type, paramTypes: List<Type>, isVarArg: Boolean = false): Type {
            TODO("Phase 1 placeholder - to be implemented")
        }
        
        /**
         * Creates an array type with the specified element type and number of elements.
         *
         * @param elementType The type of array elements
         * @param numElements The number of elements in the array
         * @return An array type
         */
        fun getArrayType(elementType: Type, numElements: Int): Type {
            TODO("Phase 1 placeholder - to be implemented")
        }
        
        /**
         * Creates a struct type with the specified element types.
         *
         * @param elementTypes The types of struct elements
         * @param isPacked Whether the struct is packed
         * @return A struct type
         */
        fun getStructType(elementTypes: List<Type>, isPacked: Boolean = false): Type {
            TODO("Phase 1 placeholder - to be implemented")
        }
    }
}