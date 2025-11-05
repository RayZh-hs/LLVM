package space.norb.llvm.types

import space.norb.llvm.core.Type
import space.norb.llvm.values.constants.NullPointerConstant

/**
 * Utility functions for pointer type casting and conversion operations.
 *
 * This utility provides functions to help with casting between typed and un-typed pointers
 * during the migration phase. These utilities work with the migration flag to ensure
 * proper behavior in both legacy and new modes.
 */
object PointerCastingUtils {
    
    /**
     * Checks if a pointer can be cast to another pointer type.
     *
     * In the new un-typed pointer model, all pointers can be cast to each other.
     * In the legacy typed pointer model, casting rules are more restrictive.
     *
     * @param sourceType The source pointer type
     * @param targetType The target pointer type
     * @return true if the cast is allowed, false otherwise
     */
    fun canCastPointerTo(sourceType: Type, targetType: Type): Boolean {
        require(sourceType.isPointerType()) { "Source type must be a pointer type" }
        require(targetType.isPointerType()) { "Target type must be a pointer type" }
        
        return if (Type.useTypedPointers) {
            // Legacy mode: more restrictive casting rules
            // In legacy mode, we allow casting between any pointer types for compatibility
            true
        } else {
            // New mode: all un-typed pointers can be cast to any pointer type
            true
        }
    }
    
    /**
     * Creates a bitcast instruction between pointer types.
     *
     * This is a utility function that would typically be used by IRBuilder
     * to create bitcast instructions for pointer type conversions.
     *
     * @param sourceType The source pointer type
     * @param targetType The target pointer type
     * @return A pair indicating if the cast is possible and the resulting type
     */
    fun createPointerBitcast(sourceType: Type, targetType: Type): Pair<Boolean, Type> {
        if (!canCastPointerTo(sourceType, targetType)) {
            return Pair(false, sourceType)
        }
        
        return if (Type.useTypedPointers) {
            // Legacy mode: return the target typed pointer
            Pair(true, targetType)
        } else {
            // New mode: all pointers are un-typed
            Pair(true, UntypedPointerType)
        }
    }
    
    /**
     * Converts a typed pointer to an un-typed pointer.
     *
     * This utility helps migrate from typed to un-typed pointers.
     * If already in un-typed mode, returns the input type.
     *
     * @param typedPointerType The typed pointer type to convert
     * @return The un-typed pointer type
     */
    fun toUntypedPointer(typedPointerType: Type): Type {
        require(typedPointerType.isPointerType()) { "Type must be a pointer type" }
        
        return if (Type.useTypedPointers) {
            // Legacy mode: convert to un-typed
            UntypedPointerType
        } else {
            // Already in un-typed mode
            typedPointerType
        }
    }
    
    /**
     * Converts an un-typed pointer to a typed pointer.
     *
     * This utility helps maintain compatibility during migration.
     * If already in typed mode, returns the input type.
     *
     * @param untypedPointerType The un-typed pointer type
     * @param elementType The element type for the typed pointer
     * @return The typed pointer type
     */
    fun toTypedPointer(untypedPointerType: Type, elementType: Type): Type {
        require(untypedPointerType.isPointerType()) { "Type must be a pointer type" }
        
        return if (Type.useTypedPointers) {
            // Legacy mode: convert to typed
            PointerType(elementType)
        } else {
            // Already in un-typed mode
            untypedPointerType
        }
    }
    
    /**
     * Gets the appropriate pointer type for a given element type.
     *
     * This utility centralizes pointer type creation logic and respects the migration flag.
     *
     * @param elementType The element type
     * @return The appropriate pointer type (typed or un-typed based on migration flag)
     */
    fun getPointerTypeFor(elementType: Type): Type {
        return Type.getPointerType(elementType)
    }
    
    /**
     * Creates a null pointer constant for the specified element type.
     *
     * This utility centralizes null pointer creation and respects the migration flag.
     *
     * @param elementType The element type
     * @return A null pointer constant of the appropriate type
     */
    fun createNullPointer(elementType: Type): NullPointerConstant {
        return NullPointerConstant.create(elementType)
    }
    
    /**
     * Creates a null pointer constant with explicit pointer type.
     *
     * This utility creates a null pointer with the exact pointer type provided,
     * regardless of the migration flag.
     *
     * @param pointerType The exact pointer type to use
     * @return A null pointer constant with the specified type
     */
    fun createNullPointerWithExactType(pointerType: Type): NullPointerConstant {
        return NullPointerConstant.createWithPointerType(pointerType)
    }
    
    /**
     * Checks if two pointer types are equivalent for the purposes of operations.
     *
     * In un-typed mode, all pointer types are equivalent.
     * In typed mode, pointer types are equivalent only if they have the same pointee type.
     *
     * @param typeA The first pointer type
     * @param typeB The second pointer type
     * @return true if the types are equivalent for operations
     */
    fun arePointersEquivalent(typeA: Type, typeB: Type): Boolean {
        require(typeA.isPointerType()) { "Type A must be a pointer type" }
        require(typeB.isPointerType()) { "Type B must be a pointer type" }
        
        return if (Type.useTypedPointers) {
            // Legacy mode: check if pointee types are the same
            when {
                typeA is PointerType && typeB is PointerType -> typeA.pointeeType == typeB.pointeeType
                else -> typeA == typeB
            }
        } else {
            // New mode: all un-typed pointers are equivalent
            true
        }
    }
    
    /**
     * Gets the element type for a pointer, handling both typed and un-typed pointers.
     *
     * For typed pointers, returns the pointee type.
     * For un-typed pointers, returns null as there's no element type information.
     *
     * @param pointerType The pointer type
     * @return The element type, or null for un-typed pointers
     */
    fun getPointerTypeElement(pointerType: Type): Type? {
        require(pointerType.isPointerType()) { "Type must be a pointer type" }
        
        return when (pointerType) {
            is PointerType -> pointerType.pointeeType
            UntypedPointerType -> null
            else -> null
        }
    }
    
    /**
     * Creates a pointer type that can be used in GEP operations.
     *
     * For GEP operations, we need to ensure we have the right pointer type
     * based on the migration mode and the element type being accessed.
     *
     * @param elementType The element type being accessed
     * @return The appropriate pointer type for GEP operations
     */
    fun createGEPPointerType(elementType: Type): Type {
        return if (Type.useTypedPointers) {
            // Legacy mode: create typed pointer
            PointerType(elementType)
        } else {
            // New mode: use un-typed pointer
            UntypedPointerType
        }
    }
    
    /**
     * Checks if a pointer type can be used in operations that require element type information.
     *
     * In the new un-typed pointer model, some operations need explicit type information
     * that was previously embedded in the pointer type itself.
     *
     * @param pointerType The pointer type to check
     * @return true if the pointer type contains element type information
     */
    fun hasElementTypeInformation(pointerType: Type): Boolean {
        require(pointerType.isPointerType()) { "Type must be a pointer type" }
        
        return when (pointerType) {
            is PointerType -> true // Typed pointers contain element type information
            UntypedPointerType -> false // Un-typed pointers don't contain element type information
            else -> false
        }
    }
    
    /**
     * Creates a pointer type with explicit element type information for operations that need it.
     *
     * This utility is useful for operations like GEP where element type information
     * is required for calculations, but we want to respect the migration flag.
     *
     * @param elementType The element type to embed in the pointer
     * @return A pointer type with the appropriate level of type information
     */
    fun createTypedPointerForOperation(elementType: Type): Type {
        return if (Type.useTypedPointers) {
            // Legacy mode: create typed pointer with element type
            PointerType(elementType)
        } else {
            // New mode: create un-typed pointer
            // Element type information must be provided separately for operations
            UntypedPointerType
        }
    }
    
    /**
     * Validates if a pointer operation can be performed with the given types.
     *
     * This method helps ensure that pointer operations are valid in both
     * typed and un-typed pointer modes.
     *
     * @param pointerType The pointer type being used
     * @param elementType The element type required for the operation (can be null for un-typed)
     * @return true if the operation is valid, false otherwise
     */
    fun validatePointerOperation(pointerType: Type, elementType: Type? = null): Boolean {
        if (!pointerType.isPointerType()) return false
        
        return when (pointerType) {
            is PointerType -> {
                // For typed pointers, check if element type matches if provided
                elementType == null || pointerType.pointeeType == elementType
            }
            UntypedPointerType -> {
                // For un-typed pointers, any element type is acceptable
                // The operation must handle type information separately
                true
            }
            else -> false
        }
    }
    
    /**
     * Gets the appropriate pointer type for return values from functions.
     *
     * This utility helps maintain consistency in function return types
     * across the migration from typed to un-typed pointers.
     *
     * @param elementType The element type the function should return a pointer to
     * @return The appropriate pointer type for function returns
     */
    fun getFunctionReturnPointerType(elementType: Type): Type {
        return if (Type.useTypedPointers) {
            // Legacy mode: return typed pointer
            PointerType(elementType)
        } else {
            // New mode: return un-typed pointer
            UntypedPointerType
        }
    }
    
    /**
     * Checks if two pointer types can be used interchangeably in function signatures.
     *
     * This is important for function type compatibility during migration.
     *
     * @param typeA The first pointer type
     * @param typeB The second pointer type
     * @return true if the types are compatible for function signatures
     */
    fun areFunctionPointerTypesCompatible(typeA: Type, typeB: Type): Boolean {
        require(typeA.isPointerType()) { "Type A must be a pointer type" }
        require(typeB.isPointerType()) { "Type B must be a pointer type" }
        
        return if (Type.useTypedPointers) {
            // Legacy mode: check if pointee types are the same
            when {
                typeA is PointerType && typeB is PointerType -> typeA.pointeeType == typeB.pointeeType
                else -> typeA == typeB
            }
        } else {
            // New mode: all un-typed pointers are compatible for function signatures
            true
        }
    }
}