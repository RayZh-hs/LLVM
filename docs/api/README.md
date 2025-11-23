# API Documentation

This section provides comprehensive documentation for all Kotlin-LLVM API components.

## Quick Start

If you're new to Kotlin-LLVM, start with these guides:

1. **[Getting Started Guide](../guides/getting-started.md)** - Set up your first project
2. **[Quick Start Example](../examples/quick-start.md)** - Build your first LLVM IR program

## Core API Components

### Type System
**[Types Documentation](types.md)** - Complete type system reference

The type system provides a type-safe way to work with LLVM IR types:

- **Primitive Types** - Void, integers, floating-point, label, metadata
- **Derived Types** - Pointers, arrays, structures, functions
- **Type Utilities** - Creation, checking, and conversion helpers
- **Module Type Management** - Named and anonymous struct types

```kotlin
// Example: Creating types
val i32 = IntegerType.I32
val float = FloatingPointType.FloatType
val ptr = PointerType
val array = ArrayType(10, i32)
val struct = StructType.AnonymousStructType(listOf(i32, float))
```

### Instructions
**[Instructions Documentation](instructions.md)** - LLVM instruction set reference

Instructions are the building blocks of LLVM IR:

- **Binary Operations** - Arithmetic (add, sub, mul, sdiv) and bitwise (and, or, xor)
- **Memory Operations** - Allocation (alloca), access (load, store), pointer arithmetic (gep)
- **Control Flow** - Terminators (return, branch, switch) and comparisons (icmp)
- **Cast Operations** - Type conversions (trunc, zext, sext, bitcast, ptrtoint)
- **Other Operations** - Function calls, phi nodes

```kotlin
// Example: Creating instructions
val sum = builder.insertAdd(a, b, "sum")
val ptr = builder.insertAlloca(IntegerType.I32, "ptr")
builder.insertStore(sum, ptr)
val loaded = builder.insertLoad(IntegerType.I32, ptr, "loaded")
```

### IR Builder
**[IR Builder Documentation](builder.md)** - Fluent API for building IR

The IR Builder provides a fluent, type-safe interface for constructing LLVM IR:

- **Builder Creation** - Setting up and managing insertion points
- **Value Creation** - Constants and special values
- **Instruction Creation** - All LLVM instructions with type safety
- **Control Flow Patterns** - Common patterns for branches and loops
- **Builder Utilities** - Helper functions and validation

```kotlin
// Example: Using the builder
val builder = IRBuilder(module)
builder.positionAtEnd(entryBlock)

val sum = builder.insertAdd(lhs, rhs, "sum") // assumes lhs/rhs are existing values
builder.insertRet(sum)
```

### Structure
**[Structure Documentation](structure.md)** - Module structure components

The structure components organize LLVM IR into a hierarchical format:

- **Modules** - Top-level containers for functions, globals, and types
- **Functions** - Executable code units with parameters and basic blocks
- **Basic Blocks** - Sequences of instructions with single entry/exit
- **Arguments** - Function parameters with type and name information
- **Global Variables** - Module-level data storage

```kotlin
// Example: Module structure
val module = Module("myModule")
val function = module.registerFunction("add", functionType)
val entryBlock = function.insertBasicBlock("entry")
val param = function.parameters[0]
```

## API Design Principles

The Kotlin-LLVM API follows these design principles:

1. **Type Safety** - All operations are type-checked at compile time
2. **Fluent Interface** - Method chaining for readable code
3. **Kotlin Idioms** - Uses Kotlin language features effectively
4. **LLVM Compatibility** - Generates valid LLVM IR compliant with latest standards
5. **Modular Design** - Clear separation of concerns between components

## Key Features

### Un-typed Pointer Model
Kotlin-LLVM uses the latest LLVM IR standard with un-typed pointers:

```kotlin
// All pointers are of the same type
val intPtr = PointerType  // Points to int
val floatPtr = PointerType // Points to float
```

### Type Deduplication
Anonymous struct types are automatically deduplicated:

```kotlin
val struct1 = module.getOrCreateAnonymousStructType(listOf(IntegerType.I32))
val struct2 = module.getOrCreateAnonymousStructType(listOf(IntegerType.I32))
// struct1 === struct2 (same instance)
```

### Fluent Builder Pattern
The IR Builder provides a clean, fluent API:

```kotlin
val result = builder.insertAdd(
    builder.insertLoad(IntegerType.I32, ptr1, "a"),
    builder.insertLoad(IntegerType.I32, ptr2, "b"),
    "sum"
)
```

### Function Linkage
Functions can be registered or declared with explicit linkage metadata so you can match LLVM’s visibility semantics without writing raw IR:

```kotlin
val helper = module.registerFunction(
    name = "helper",
    returnType = IntegerType.I32,
    paramTypes = listOf(IntegerType.I32),
    linkage = LinkageType.INTERNAL
)

val printf = module.declareExternalFunction(
    name = "printf",
    returnType = IntegerType.I32,
    parameterTypes = listOf(PointerType),
    isVarArg = true,
    linkage = LinkageType.EXTERNAL
)
```

See [structure.md#function-linkage](structure.md#function-linkage) for the full table of supported linkage kinds and when to use each one.

## Common Patterns

### Function Creation
```kotlin
val functionType = FunctionType(
    returnType = IntegerType.I32,
    paramTypes = listOf(IntegerType.I32, IntegerType.I32),
    paramNames = listOf("a", "b")
)
val function = module.registerFunction("add", functionType)
```

### Control Flow
```kotlin
val condition = builder.insertICmp(IcmpPredicate.NE, value, zero, "cond")
builder.insertCondBr(condition, thenBlock, elseBlock)
```

### Memory Operations
```kotlin
val ptr = builder.insertAlloca(IntegerType.I32, "var")
builder.insertStore(value, ptr)
val loaded = builder.insertLoad(IntegerType.I32, ptr, "loaded")
```

## Additional Resources

### Examples
- **[Quick Start](../examples/quick-start.md)** - Basic usage example

### Guides
- **[Getting Started](../guides/getting-started.md)** - Setup and installation
- **[Testing](../guides/testing.md)** - Testing your LLVM IR code

### Architecture
- **[Overview](../architecture/overview.md)** - High-level architecture
- **[Design Principles](../architecture/README.md)** - Design decisions

## API Reference by Category

### Type Creation
- [`IntegerType`](src/main/kotlin/space/norb/llvm/types/PrimitiveTypes.kt) - Integer types
- [`FloatingPointType`](src/main/kotlin/space/norb/llvm/types/PrimitiveTypes.kt) - Floating-point types
- [`ArrayType`](src/main/kotlin/space/norb/llvm/types/DerivedTypes.kt) - Array types
- [`StructType`](src/main/kotlin/space/norb/llvm/types/DerivedTypes.kt) - Structure types
- [`FunctionType`](src/main/kotlin/space/norb/llvm/types/DerivedTypes.kt) - Function types

### Instruction Creation
- [`IRBuilder`](src/main/kotlin/space/norb/llvm/builder/IRBuilder.kt) - Main builder class
- [`BuilderUtils`](src/main/kotlin/space/norb/llvm/builder/BuilderUtils.kt) - Builder utilities

### Structure Components
- [`Module`](src/main/kotlin/space/norb/llvm/structure/Module.kt) - Module class
- [`Function`](src/main/kotlin/space/norb/llvm/structure/Function.kt) - Function class
- [`BasicBlock`](src/main/kotlin/space/norb/llvm/structure/BasicBlock.kt) - Basic block class
- [`Argument`](src/main/kotlin/space/norb/llvm/structure/Argument.kt) - Argument class

## Contributing to Documentation

Found an error or want to improve the documentation? Please:

1. Check the [CONTRIBUTING.md](../../CONTRIBUTING.md) guide
2. Submit an issue or pull request
3. Follow the established documentation style

## License

This documentation is part of the Kotlin-LLVM project. See the [LICENSE](../../LICENSE) file for details.
