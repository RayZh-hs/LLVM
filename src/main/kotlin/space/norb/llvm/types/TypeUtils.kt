package space.norb.llvm.types

import space.norb.llvm.core.Type

/**
 * Utility functions and commonly used type constants for working with LLVM types.
 */
object TypeUtils {
    // Common integer type constants
    val I1: Type = IntegerType(1)
    val I8: Type = IntegerType(8)
    val I16: Type = IntegerType(16)
    val I32: Type = IntegerType(32)
    val I64: Type = IntegerType(64)
    val I128: Type = IntegerType(128)
    
    // Common floating-point type constants
    val FLOAT: Type = FloatingPointType.FloatType
    val DOUBLE: Type = FloatingPointType.DoubleType
    
    // Common primitive type constants
    val VOID: Type = VoidType
    val LABEL: Type = LabelType
    val METADATA: Type = MetadataType
    
    // ==================== Type Compatibility Checks ====================
    
    /**
     * Checks if a type is a first-class type.
     * First-class types can be used as operands to most instructions.
     *
     * @param type The type to check
     * @return true if the type is first-class, false otherwise
     */
    fun isFirstClassType(type: Type): Boolean {
        return when (type) {
            is VoidType, is LabelType, is MetadataType -> false
            else -> true
        }
    }
    
    /**
     * Checks if a type is a single value type.
     * Single value types represent exactly one value.
     *
     * @param type The type to check
     * @return true if the type is a single value type, false otherwise
     */
    fun isSingleValueType(type: Type): Boolean {
        return when (type) {
            is IntegerType, is FloatingPointType, is PointerType, UntypedPointerType -> true
            else -> false
        }
    }
    
    /**
     * Checks if a type is an aggregate type.
     * Aggregate types contain multiple elements.
     *
     * @param type The type to check
     * @return true if the type is an aggregate type, false otherwise
     */
    fun isAggregateType(type: Type): Boolean {
        return when (type) {
            is ArrayType, is StructType -> true
            else -> false
        }
    }
    
    /**
     * Checks if a type is an integer type.
     *
     * @param type The type to check
     * @return true if the type is an integer type, false otherwise
     */
    fun isIntegerType(type: Type): Boolean = type.isIntegerType()
    
    /**
     * Checks if a type is a floating-point type.
     *
     * @param type The type to check
     * @return true if the type is a floating-point type, false otherwise
     */
    fun isFloatingPointType(type: Type): Boolean = type.isFloatingPointType()
    
    /**
     * Checks if a type is a pointer type.
     *
     * @param type The type to check
     * @return true if the type is a pointer type, false otherwise
     */
    fun isPointerTy(type: Type): Boolean = type.isPointerType()
    
    // ==================== Type Size and Alignment Utilities ====================
    
    /**
     * Returns the size in bits for primitive types.
     *
     * @param type The type to get the size for
     * @return The size in bits, or null if not applicable
     */
    fun getPrimitiveSizeInBits(type: Type): Int? = type.getPrimitiveSizeInBits()
    
    /**
     * Returns the size in bits for scalar types.
     * Scalar types include integers, floating-point types, and pointers.
     *
     * @param type The type to get the size for
     * @return The size in bits, or null if not a scalar type
     */
    fun getScalarSizeInBits(type: Type): Int? {
        return when (type) {
            is IntegerType -> type.bitWidth
            is FloatingPointType -> type.getPrimitiveSizeInBits()
            is PointerType -> 64 // Assume 64-bit pointers
            UntypedPointerType -> 64 // Assume 64-bit pointers
            else -> null
        }
    }
    
    // ==================== Type Relationship Utilities ====================
    
    /**
     * Returns the element type for array, pointer, and vector types.
     *
     * @param type The type to get the element type for
     * @return The element type, or null if not applicable
     */
    fun getElementType(type: Type): Type? {
        return when (type) {
            is ArrayType -> type.elementType
            is PointerType -> type.pointeeType
            UntypedPointerType -> null // Un-typed pointers don't have element type
            else -> null
        }
    }
    
    /**
     * Returns the number of parameters for function types.
     *
     * @param type The type to get the parameter count for
     * @return The number of parameters, or null if not a function type
     */
    fun getFunctionNumParams(type: Type): Int? {
        return when (type) {
            is FunctionType -> type.paramTypes.size
            else -> null
        }
    }
    
    /**
     * Returns the parameter type at the specified index for function types.
     *
     * @param type The function type to get the parameter from
     * @param index The index of the parameter
     * @return The parameter type at the index, or null if not applicable
     */
    fun getFunctionParamType(type: Type, index: Int): Type? {
        return when (type) {
            is FunctionType -> {
                if (index >= 0 && index < type.paramTypes.size) {
                    type.paramTypes[index]
                } else null
            }
            else -> null
        }
    }
    
    // ==================== Type Casting Utilities ====================
    
    /**
     * Checks if two types can be bitcast without loss of information.
     *
     * @param typeA The first type
     * @param typeB The second type
     * @return true if the types can be bitcast without loss, false otherwise
     */
    fun canLosslesslyBitCastTo(typeA: Type, typeB: Type): Boolean {
        // Same types can always be bitcast
        if (typeA == typeB) return true
        
        // Pointers to pointers of any type can be bitcast
        if ((typeA is PointerType || typeA == UntypedPointerType) &&
            (typeB is PointerType || typeB == UntypedPointerType)) return true
        
        val sizeA = getPrimitiveSizeInBits(typeA)
        val sizeB = getPrimitiveSizeInBits(typeB)
        
        // Both types must have a size to be bitcastable
        if (sizeA == null || sizeB == null) return false
        
        // Types must have the same size to be bitcastable
        if (sizeA != sizeB) return false
        
        // Integer types of same size can be bitcast
        if (typeA.isIntegerType() && typeB.isIntegerType()) return true
        
        // Floating-point types of same size can be bitcast
        if (typeA.isFloatingPointType() && typeB.isFloatingPointType()) return true
        
        // Integer and floating-point types of same size can be bitcast
        if ((typeA.isIntegerType() && typeB.isFloatingPointType()) ||
            (typeA.isFloatingPointType() && typeB.isIntegerType())) return true
        
        return false
    }
    
    /**
     * Finds a common type that can represent both input types.
     * This is useful for operations that need a unified type.
     *
     * @param typeA The first type
     * @param typeB The second type
     * @return A common type, or null if no common type exists
     */
    fun getCommonType(typeA: Type, typeB: Type): Type? {
        // If types are the same, return that type
        if (typeA == typeB) return typeA
        
        // For pointer types, check migration flag first
        if (typeA.isPointerType() && typeB.isPointerType()) {
            return if (Type.useTypedPointers) {
                // Legacy mode: return i8*
                PointerType(TypeUtils.I8)
            } else {
                // New mode: return un-typed pointer
                UntypedPointerType
            }
        }
        
        // If one can be losslessly bitcast to the other, return the target
        if (canLosslesslyBitCastTo(typeA, typeB)) return typeB
        if (canLosslesslyBitCastTo(typeB, typeA)) return typeA
        
        // For integer types, return the larger one
        if (typeA.isIntegerType() && typeB.isIntegerType()) {
            val sizeA = getPrimitiveSizeInBits(typeA) ?: return null
            val sizeB = getPrimitiveSizeInBits(typeB) ?: return null
            return if (sizeA >= sizeB) typeA else typeB
        }
        
        // For floating-point types, return the larger one
        if (typeA.isFloatingPointType() && typeB.isFloatingPointType()) {
            val sizeA = getPrimitiveSizeInBits(typeA) ?: return null
            val sizeB = getPrimitiveSizeInBits(typeB) ?: return null
            return if (sizeA >= sizeB) typeA else typeB
        }
        
        // No common type found
        return null
    }
}