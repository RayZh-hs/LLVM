package space.norb.llvm.types

import space.norb.llvm.core.Type

/**
 * Derived LLVM types including pointers, functions, arrays, and structs.
 */
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
        val varArg = if (isVarArg) ", ..." else ""
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
        val packed = if (isPacked) "<" else "{"
        val elements = elementTypes.joinToString(", ") { it.toString() }
        val close = if (isPacked) ">" else "}"
        return "$packed$elements$close"
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