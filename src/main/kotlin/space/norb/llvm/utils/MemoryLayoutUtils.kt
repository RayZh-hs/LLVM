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

private const val DEFAULT_POINTER_WIDTH_BITS = 64

/**
 * Computes the size of the given type in bytes, taking standard padding rules into account.
 *
 * @return The size in bytes
 * @throws IllegalArgumentException If the type cannot be sized (e.g., functions or opaque structs)
 */
fun Type.getSizeInBytes(pointerWidthBits: Int = DEFAULT_POINTER_WIDTH_BITS): Long =
    computeLayout(pointerWidthBits).sizeInBytes

/**
 * Computes both size and alignment for the receiver type.
 */
fun Type.computeLayout(pointerWidthBits: Int = DEFAULT_POINTER_WIDTH_BITS): Layout {
    require(pointerWidthBits == 32 || pointerWidthBits == 64) {
        "Only 32-bit and 64-bit pointer sizes are supported (got $pointerWidthBits)"
    }

    return when (this) {
        is IntegerType -> {
             val bytes = maxOf(1, (this.bitWidth + 7) / 8)
            val alignment = nextPowerOfTwo(bytes)
            Layout(bytes.toLong(), alignment)
        }
        is FloatingPointType.FloatType -> Layout(4, 4)
        is FloatingPointType.DoubleType -> Layout(8, 8)
        is PointerType -> pointerLayout(pointerWidthBits)
        is ArrayType -> computeArrayLayout(this, pointerWidthBits)
        is StructType.AnonymousStructType ->
            computeStructLayout(this.elementTypes, this.isPacked, pointerWidthBits)
        is StructType.NamedStructType -> {
            val elements = this.elementTypes
                ?: throw IllegalArgumentException("Cannot compute size of opaque named struct: ${this.name}")
            computeStructLayout(elements, this.isPacked, pointerWidthBits)
        }
        is FunctionType -> throw IllegalArgumentException("Cannot compute size of function type: $this")
        is VoidType, is LabelType, is MetadataType ->
            throw IllegalArgumentException("Cannot compute size of unsized type: $this")
        else -> throw IllegalArgumentException("Unsupported type for size calculation: ${this::class.simpleName}")
    }
}

// Helper function to compute the next power of two for alignment purposes
private fun nextPowerOfTwo(v: Int): Int {
    var n = v - 1
    n = n or (n ushr 1)
    n = n or (n ushr 2)
    n = n or (n ushr 4)
    n = n or (n ushr 8)
    n = n or (n ushr 16)
    return n + 1
}

private fun pointerLayout(pointerWidthBits: Int): Layout {
    val pointerBytes = pointerWidthBits / 8
    return Layout(pointerBytes.toLong(), pointerBytes)
}

private fun computeArrayLayout(arrayType: ArrayType, pointerWidthBits: Int): Layout {
    require(arrayType.numElements >= 0) { "Array element count must be non-negative" }
    val elementLayout = arrayType.elementType.computeLayout(pointerWidthBits)
    val stride = alignTo(elementLayout.sizeInBytes, elementLayout.alignment.toLong())
    return Layout(stride * arrayType.numElements, elementLayout.alignment)
}

private fun computeStructLayout(
    elementTypes: List<Type>,
    isPacked: Boolean,
    pointerWidthBits: Int
): Layout {
    if (elementTypes.isEmpty()) return Layout(0, 1)

    var offset = 0L
    var structAlignment = if (isPacked) 1 else 1

    for (elementType in elementTypes) {
        val elementLayout = elementType.computeLayout(pointerWidthBits)
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
