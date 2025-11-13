# Type System Documentation

The Kotlin-LLVM type system provides a type-safe way to work with LLVM IR types.

## Overview

The type system is designed to be:
- **Type-safe** - All type operations are checked at compile time
- **Focused** - Covers the core LLVM IR types implemented today (integers, floats, pointers, arrays, structs, functions)
- **Expressive** - Provides convenient constructors and utilities
- **Compatible** - Generates valid LLVM IR types

## Primitive Types

### Integer Types
```kotlin
// 1-bit integer (boolean)
val i1 = IntegerType.I1

// 8-bit integer
val i8 = IntegerType.I8

// 16-bit integer
val i16 = IntegerType.I16

// 32-bit integer
val i32 = IntegerType.I32

// 64-bit integer
val i64 = IntegerType.I64

// 128-bit integer
val i128 = IntegerType.I128

// Custom bit width integer
val customInt = IntegerType(42)
```

### Floating Point Types
```kotlin
// 32-bit single precision
val float = FloatingPointType.FloatType

// 64-bit double precision
val double = FloatingPointType.DoubleType
```

### Other Primitive Types
```kotlin
// Void type
val void = VoidType

// Label type (for basic blocks)
val label = LabelType

// Metadata type
val metadata = MetadataType
```

## Derived Types

### Pointer Types
```kotlin
// All pointers are un-typed in the latest LLVM IR standard
val pointer = PointerType

// The pointer type is the same regardless of pointee type
val intPtr = PointerType  // Points to int
val floatPtr = PointerType // Points to float
```

> **Note**: This implementation uses the new un-typed pointer model which complies with the latest LLVM IR standard. All pointers are of a single type (similar to `void*` in C). Type information is conveyed through other mechanisms rather than being embedded in the pointer type itself.

### Array Types
```kotlin
// Array of 10 integers
val intArray = ArrayType(10, IntegerType.I32)

// Array of 5 floats
val floatArray = ArrayType(5, FloatingPointType.FloatType)

// Nested array (2D array)
val matrix = ArrayType(3, ArrayType(4, IntegerType.I32))
```

### Structure Types

#### Anonymous Structures
```kotlin
// Simple anonymous structure
val personStruct = StructType.AnonymousStructType(
    elementTypes = listOf(
        IntegerType.I32,    // age
        FloatingPointType.FloatType,    // height
        PointerType         // name (un-typed pointer)
    )
)

// Packed anonymous structure
val packedStruct = StructType.AnonymousStructType(
    elementTypes = listOf(
        IntegerType.I8,
        IntegerType.I32,
        IntegerType.I8
    ),
    isPacked = true
)
```

#### Named Structures
```kotlin
// Create a named struct type in a module
val namedStruct = module.registerNamedStructType(
    name = "MyStruct",
    elementTypes = listOf(
        IntegerType.I32,
        FloatingPointType.FloatType
    )
)

// Create an opaque named struct (no elements defined yet)
val opaqueStruct = module.registerOpaqueStructType("OpaqueStruct")

// Complete an opaque struct later
module.completeOpaqueStructType(
    name = "OpaqueStruct",
    elementTypes = listOf(IntegerType.I64, PointerType)
)
```

### Function Types
```kotlin
// Function taking two ints and returning an int
val binaryOp = FunctionType(
    returnType = IntegerType.I32,
    paramTypes = listOf(IntegerType.I32, IntegerType.I32)
)

// Function with varargs
val printf = FunctionType(
    returnType = IntegerType.I32,
    paramTypes = listOf(PointerType),
    isVarArg = true
)

// Function with parameter names
val namedParams = FunctionType(
    returnType = IntegerType.I32,
    paramTypes = listOf(IntegerType.I32, IntegerType.I32),
    paramNames = listOf("a", "b")
)

// Function with no parameters
val noParams = FunctionType(VoidType, emptyList())
```

## Type Utilities

### Type Checking
```kotlin
// Check if type is integer
val isInt = IntegerType.I32.isIntegerType()

// Check if type is floating point
val isFloat = FloatingPointType.FloatType.isFloatingPointType()

// Check if type is pointer
val isPtr = PointerType.isPointerType()

// Check if type is array
val isArray = ArrayType(10, IntegerType.I32).isArrayType()

// Check if type is struct
val isStruct = StructType.AnonymousStructType(listOf(IntegerType.I32)).isStructType()

// Get primitive size in bits
val intSize = IntegerType.I32.getPrimitiveSizeInBits() // Returns 32
val floatSize = FloatingPointType.FloatType.getPrimitiveSizeInBits() // Returns 32
```

### Type Creation with BuilderUtils
```kotlin
// Create integer types
val i8 = BuilderUtils.getIntType(8)
val i32 = BuilderUtils.getIntType(32)

// Create primitive types
val void = BuilderUtils.getVoidType()
val ptr = BuilderUtils.getPointerType()
val float = BuilderUtils.getFloatType()
val double = BuilderUtils.getDoubleType()

// Create function types
val funcType = BuilderUtils.getFunctionType(
    returnType = IntegerType.I32,
    paramTypes = listOf(IntegerType.I32, IntegerType.I32)
)
```

## Type Compatibility

### Implicit Conversions
The Kotlin-LLVM type layer does not apply implicit conversions. Operands must already share the same type; insert an explicit cast when you need to adapt values for an instruction.

### Explicit Conversions
Use cast instructions for explicit type conversions:
- `trunc` - Truncate integer to smaller type
- `zext` - Zero extend integer to larger type
- `sext` - Sign extend integer to larger type
- `bitcast` - Bitwise cast between types of the same size

Additional LLVM cast opcodes such as `sitofp`, `fptosi`, `uitofp`, and `fptoui` are not yet implemented in this module.

## Type System Extensions

### Custom Types
You can define custom types for your domain:
```kotlin
// Custom type for complex numbers
val complexType = StructType.AnonymousStructType(
    elementTypes = listOf(
        FloatingPointType.FloatType, // real
        FloatingPointType.FloatType  // imag
    )
)

// Register as named type in module
val namedComplexType = module.registerNamedStructType(
    name = "Complex",
    elementTypes = listOf(
        FloatingPointType.FloatType,
        FloatingPointType.FloatType
    )
)
```

### Type Aliases
Create convenient aliases for complex types:
```kotlin
// Type alias for common patterns
typealias IntPtr = PointerType
typealias StringPtr = PointerType
typealias VoidFunc = FunctionType

// Usage
val funcType: VoidFunc = FunctionType(VoidType, emptyList())
```

## Module Type Management

### Named Struct Type Management
```kotlin
// Register a named struct type
val personType = module.registerNamedStructType(
    name = "Person",
    elementTypes = listOf(IntegerType.I32, PointerType)
)

// Check if a named struct type exists
val hasPerson = module.hasNamedStructType("Person")

// Get a named struct type
val retrievedType = module.getNamedStructType("Person")

// Get all named struct types
val allNamedTypes = module.getAllNamedStructTypes()

// Get all anonymous struct types
val allAnonymousTypes = module.getAllAnonymousStructTypes()

// Get all struct types (named + anonymous)
val allStructTypes = module.getAllStructTypes()
```

### Anonymous Struct Type Deduplication
```kotlin
// Get or create an anonymous struct type (ensures deduplication)
val struct1 = module.getOrCreateAnonymousStructType(
    elementTypes = listOf(IntegerType.I32, IntegerType.I64)
)

val struct2 = module.getOrCreateAnonymousStructType(
    elementTypes = listOf(IntegerType.I32, IntegerType.I64)
)

// struct1 === struct2 (same instance due to deduplication)
```

## Best Practices

1. **Use named types** for better readability and maintainability
2. **Prefer type-safe constructors** over raw type creation
3. **Document custom types** with clear semantics
4. **Use type utilities** for common operations
5. **Consider alignment** when defining structures
6. **Register named structs** in modules for proper organization
7. **Use un-typed pointers** correctly with the latest LLVM IR standard
8. **Leverage deduplication** for anonymous struct types