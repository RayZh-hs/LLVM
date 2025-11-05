package space.norb.llvm.types

import space.norb.llvm.core.Type

/**
 * Derived LLVM types including pointers, functions, arrays, and structs.
 *
 * ## LLVM IR Compliance Notice
 *
 * **IMPORTANT**: This implementation uses the legacy typed pointer model which does NOT
 * comply with the latest LLVM IR standard. The current LLVM IR standard has moved to
 * un-typed pointers (similar to `void*` in C) where all pointers are of a single type.
 *
 * ### Current Implementation (Legacy Model)
 *
 * This PointerType class implements typed pointers where each pointer contains explicit
 * pointee type information. This is the legacy LLVM IR model that has been deprecated.
 *
 * Key characteristics of the current implementation:
 * - Each pointer contains explicit pointee type information via `pointeeType` property
 * - String representation includes pointee type (e.g., "i32*", "float*")
 * - Type checking and validation rely on pointee type information
 * - Pointer operations like GEP use pointee type for calculations
 *
 * ### Target Implementation (LLVM IR Compliant)
 *
 * The target implementation should use un-typed pointers:
 * ```kotlin
 * object UntypedPointerType : Type() {
 *     override fun toString(): String = "ptr"
 *     // ... other methods
 * }
 * ```
 *
 * ### Migration Path
 *
 * For migration details and implementation plan, see:
 * @see docs/ptr-migration-todo.md
 *
 * The migration will:
 * - Replace this PointerType with UntypedPointerType
 * - Update all pointer operations to work with un-typed pointers
 * - Add explicit type information where needed through other mechanisms
 * - Maintain backward compatibility during transition
 */
/**
 * Untyped pointer type implementation compliant with the latest LLVM IR standard.
 *
 * This represents the new un-typed pointer model where all pointers are of a single type
 * (similar to `void*` in C). Type information is conveyed through other mechanisms.
 */
object UntypedPointerType : Type() {
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

/**
 * Legacy typed pointer implementation (deprecated).
 *
 * **IMPORTANT**: This implementation uses the legacy typed pointer model which does NOT
 * comply with the latest LLVM IR standard. The current LLVM IR standard has moved to
 * un-typed pointers (similar to `void*` in C) where all pointers are of a single type.
 *
 * This class is maintained for backward compatibility during migration.
 * Use UntypedPointerType for new code.
 *
 * @deprecated Use UntypedPointerType instead
 */
@Deprecated("Use UntypedPointerType instead for LLVM IR compliance")
data class PointerType(val pointeeType: Type) : Type() {
    override fun toString(): String = "${pointeeType.toString()}*"
    override fun isPrimitiveType(): Boolean = false
    override fun isDerivedType(): Boolean = true
    override fun isIntegerType(): Boolean = false
    override fun isFloatingPointType(): Boolean = false
    override fun isPointerType(): Boolean = true
    override fun isFunctionType(): Boolean = false
    override fun isArrayType(): Boolean = false
    override fun isStructType(): Boolean = false
    override fun getPrimitiveSizeInBits(): Int? = null
}

data class FunctionType(
    val returnType: Type,
    val paramTypes: List<Type>,
    val isVarArg: Boolean = false
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