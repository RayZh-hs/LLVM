package space.norb.llvm.utils

import space.norb.llvm.core.Type
import space.norb.llvm.types.ArrayType
import space.norb.llvm.types.FloatingPointType
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.IntegerType
import space.norb.llvm.types.LabelType
import space.norb.llvm.types.MetadataType
import space.norb.llvm.types.PointerType
import space.norb.llvm.types.StructType
import space.norb.llvm.types.VoidType

/**
 * Utility helpers for computing byte sizes of LLVM types with alignment-aware layouts.
 *
 * The calculation mirrors a simplified data layout where each type is aligned to its natural
 * size (in bytes). Structs honour field alignment unless marked as packed, and the total struct
 * size is rounded up to the struct alignment to make arrays of structs correctly aligned.
 */
data class Layout(val sizeInBytes: Long, val alignment: Int)

/**
 * Computes the size of the given type in bytes, taking standard padding rules into account.
 *
 * @return The size in bytes
 * @throws IllegalArgumentException If the type cannot be sized (e.g., functions or opaque structs)
 */
fun Type.getSizeInBytes(): Long = computeLayout().sizeInBytes

/**
 * Computes both size and alignment for the receiver type.
 */
fun Type.computeLayout(): Layout {
    return when (this) {
        is IntegerType -> {
            val bytes = maxOf(1, (this.bitWidth + 7) / 8)
            Layout(bytes.toLong(), bytes)
        }
        is FloatingPointType.FloatType -> Layout(4, 4)
        is FloatingPointType.DoubleType -> Layout(8, 8)
        is PointerType -> Layout(8, 8) // 64-bit pointers
        is ArrayType -> computeArrayLayout(this)
        is StructType.AnonymousStructType -> computeStructLayout(this.elementTypes, this.isPacked)
        is StructType.NamedStructType -> {
            val elements = this.elementTypes
                ?: throw IllegalArgumentException("Cannot compute size of opaque named struct: ${this.name}")
            computeStructLayout(elements, this.isPacked)
        }
        is FunctionType -> throw IllegalArgumentException("Cannot compute size of function type: $this")
        is VoidType, is LabelType, is MetadataType ->
            throw IllegalArgumentException("Cannot compute size of unsized type: $this")
        else -> throw IllegalArgumentException("Unsupported type for size calculation: ${this::class.simpleName}")
    }
}

private fun computeArrayLayout(arrayType: ArrayType): Layout {
    require(arrayType.numElements >= 0) { "Array element count must be non-negative" }
    val elementLayout = arrayType.elementType.computeLayout()
    val stride = alignTo(elementLayout.sizeInBytes, elementLayout.alignment.toLong())
    return Layout(stride * arrayType.numElements, elementLayout.alignment)
}

private fun computeStructLayout(elementTypes: List<Type>, isPacked: Boolean): Layout {
    if (elementTypes.isEmpty()) return Layout(0, 1)

    var offset = 0L
    var structAlignment = if (isPacked) 1 else 1

    for (elementType in elementTypes) {
        val elementLayout = elementType.computeLayout()
        val elementAlignment = if (isPacked) 1 else elementLayout.alignment

        offset = alignTo(offset, elementAlignment.toLong())
        offset += elementLayout.sizeInBytes
        structAlignment = maxOf(structAlignment, elementAlignment)
    }

    val finalAlignment = if (isPacked) 1 else structAlignment
    val finalSize = if (isPacked) offset else alignTo(offset, finalAlignment.toLong())
    return Layout(finalSize, finalAlignment)
}

private fun alignTo(value: Long, alignment: Long): Long {
    if (alignment <= 1) return value
    val remainder = value % alignment
    return if (remainder == 0L) value else value + (alignment - remainder)
}
