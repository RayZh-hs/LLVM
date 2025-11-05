package space.norb.llvm.values.constants

import space.norb.llvm.core.Constant
import space.norb.llvm.types.PointerType

/**
 * Null pointer constant in LLVM IR.
 *
 * ## LLVM IR Compliance Notice
 *
 * **IMPORTANT**: This null pointer implementation uses the legacy typed pointer model
 * which does NOT comply with the latest LLVM IR standard. The current LLVM IR standard
 * has moved to un-typed pointers (similar to `void*` in C) where all pointers are of a single type.
 *
 * ### Current Implementation (Legacy Model)
 *
 * This NullPointerConstant represents a null value for a specific typed pointer type
 * where the pointer type includes explicit pointee type information. This is the legacy
 * LLVM IR model that has been deprecated.
 *
 * Current IR output example:
 * ```
 * i32* null        ; Null pointer for i32* type
 * float* null      ; Null pointer for float* type
 * %struct.MyType* null ; Null pointer for struct pointer type
 * ```
 *
 * ### Target Implementation (LLVM IR Compliant)
 *
 * The target implementation should represent null values for un-typed pointers:
 * ```
 * ptr null         ; Null pointer for un-typed pointer type
 * ptr null         ; Same null pointer for all pointer types
 * ptr null         ; Same null pointer for all pointer types
 * ```
 *
 * Key differences in target implementation:
 * - All null pointers have the same "ptr" type regardless of pointee type
 * - Type information is conveyed through context, not the null constant itself
 * - Null pointer constants can be used with any pointer type through type casting
 * - Simplified null pointer representation and handling
 *
 * ### Migration Path
 *
 * For migration details and implementation plan, see:
 * @see docs/ptr-migration-todo.md
 *
 * The migration will:
 * - Update this class to represent un-typed null pointer constants
 * - Remove dependency on specific PointerType
 * - Update all dependent code that expects typed null pointers
 * - Ensure all generated IR complies with latest LLVM IR standard
 */
class NullPointerConstant(
    override val type: PointerType
) : Constant("null", type)