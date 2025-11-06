package space.norb.llvm.types

import space.norb.llvm.core.Type

/**
 * Derived LLVM types including pointers, functions, arrays, and structs.
 *
 * ## LLVM IR Compliance Notice
 *
 * This implementation uses the new un-typed pointer model which complies with the
 * latest LLVM IR standard. All pointers are of a single type (similar to `void*` in C).
 * Type information is conveyed through other mechanisms rather than being embedded
 * in the pointer type itself.
 *
 * ### Current Implementation (LLVM IR Compliant)
 *
 * This PointerType object implements un-typed pointers where all pointers are of a single type.
 * This is the current LLVM IR standard.
 *
 * Key characteristics of the current implementation:
 * - All pointers are of a single type regardless of pointee type
 * - String representation is simply "ptr"
 * - Type checking and validation rely on context rather than pointer type
 * - Pointer operations like GEP use explicit type information
 */
/**
 * Pointer type implementation compliant with the latest LLVM IR standard.
 *
 * This represents the un-typed pointer model where all pointers are of a single type
 * (similar to `void*` in C). Type information is conveyed through other mechanisms.
 */
object PointerType : Type() {
    override fun toString(): String = "ptr"
    override fun isPrimitiveType(): Boolean = false
    override fun isDerivedType(): Boolean = true
    override fun isIntegerType(): Boolean = false
    override fun isFloatingPointType(): Boolean = false
    override fun isPointerType(): Boolean = true
    override fun isFunctionType(): Boolean = false
    override fun isArrayType(): Boolean = false
    override fun isStructType(): Boolean = false
    override fun getPrimitiveSizeInBits(): Int? = 64 // Assume 64-bit pointers
}

data class FunctionType(
    val returnType: Type,
    val paramTypes: List<Type>,
    val isVarArg: Boolean = false,
    val paramNames: List<String>? = null
) : Type() {
    override fun toString(): String {
        val params = paramTypes.joinToString(", ") { it.toString() }
        val varArg = if (isVarArg) {
            if (paramTypes.isEmpty()) "..." else ", ..."
        } else ""
        return "${returnType.toString()} ($params$varArg)"
    }
    override fun isPrimitiveType(): Boolean = false
    override fun isDerivedType(): Boolean = true
    override fun isIntegerType(): Boolean = false
    override fun isFloatingPointType(): Boolean = false
    override fun isPointerType(): Boolean = false
    override fun isFunctionType(): Boolean = true
    override fun isArrayType(): Boolean = false
    override fun isStructType(): Boolean = false
    override fun getPrimitiveSizeInBits(): Int? = null
    
    /**
     * Get the parameter name at the specified index.
     * Falls back to "arg$index" when no custom name is provided.
     */
    fun getParameterName(index: Int): String {
        return paramNames?.getOrNull(index) ?: "arg$index"
    }
}

data class ArrayType(val numElements: Int, val elementType: Type) : Type() {
    override fun toString(): String = "[$numElements x ${elementType.toString()}]"
    override fun isPrimitiveType(): Boolean = false
    override fun isDerivedType(): Boolean = true
    override fun isIntegerType(): Boolean = false
    override fun isFloatingPointType(): Boolean = false
    override fun isPointerType(): Boolean = false
    override fun isFunctionType(): Boolean = false
    override fun isArrayType(): Boolean = true
    override fun isStructType(): Boolean = false
    override fun getPrimitiveSizeInBits(): Int? = null
}

data class StructType(val elementTypes: List<Type>, val isPacked: Boolean = false) : Type() {
    override fun toString(): String {
        val elements = elementTypes.joinToString(", ") { it.toString() }
        return if (isPacked) "<{ $elements }>" else "{ $elements }"
    }
    override fun isPrimitiveType(): Boolean = false
    override fun isDerivedType(): Boolean = true
    override fun isIntegerType(): Boolean = false
    override fun isFloatingPointType(): Boolean = false
    override fun isPointerType(): Boolean = false
    override fun isFunctionType(): Boolean = false
    override fun isArrayType(): Boolean = false
    override fun isStructType(): Boolean = true
    override fun getPrimitiveSizeInBits(): Int? = null
}