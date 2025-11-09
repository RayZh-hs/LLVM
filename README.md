<div align="center">
  <img src="https://raw.githubusercontent.com/RayZh-hs/LLVM/main/public/Kotlin-LLVM.png" alt="Kotlin-LLVM Logo" width="200"/>
  <h1>Kotlin-LLVM</h1>
  <p align="center">
  Community-driven LLVM IR Generation Framework for Kotlin.
  </p>
  <p align="center">
    <a href="https://kotlinlang.org">
      <img src="https://img.shields.io/badge/Kotlin-2.2.0-blue.svg" alt="Kotlin Version"/>
    </a>
    <a href="https://llvm.org">
      <img src="https://img.shields.io/badge/LLVM%20IR-Untyped%20Pointers-orange.svg" alt="LLVM IR"/>
    </a>
    <a href="LICENSE">
      <img src="https://img.shields.io/badge/License-MIT-yellow.svg" alt="License"/>
    </a>
  </p>
</div>


## Overview

Kotlin-LLVM is a comprehensive framework for generating LLVM Intermediate Representation (IR) using Kotlin. It provides a type-safe, fluent API for constructing LLVM IR programs with full compliance to the latest LLVM IR standard using untyped pointers.

## Features

- **🔧 Type-Safe IR Construction**: Leverages Kotlin's type system to prevent errors during IR construction
- **🏗️ Fluent Builder Pattern**: Intuitive API for building complex LLVM IR programs
- **📋 Modern LLVM IR Compliance**: Uses the latest untyped pointer model compatible with modern LLVM toolchains
- **🎯 Comprehensive Instruction Set**: Support for all major LLVM instruction categories
- **🔍 Visitor Pattern**: Built-in support for IR analysis, transformation, and printing
- **✅ Extensive Testing**: Comprehensive test suite with end-to-end validation

## Quick Start

### Installation

Add the dependency to your `build.gradle.kts`:

```kotlin
dependencies {
    implementation("space.norb:llvm:1.0-SNAPSHOT")
}
```

### Basic Usage

```kotlin
import space.norb.llvm.*
import space.norb.llvm.types.*

// Create a module
val module = Module("example")

// Create an IR builder
val builder = IRBuilder(module)

// Define a simple function that adds two integers
val addFunction = builder.createFunction(
    "add",
    FunctionType(IntegerType(32), listOf(IntegerType(32), IntegerType(32)))
)

// Create basic blocks
val entryBlock = builder.insertBasicBlock("entry", addFunction)

// Position builder at the end of entry block
builder.positionAtEnd(entryBlock)

// Get function parameters
val a = addFunction.parameters[0]
val b = addFunction.parameters[1]

// Add the two parameters
val result = builder.insertAdd(a, b, "result")

// Return the result
builder.insertRet(result)

// Print the generated IR
val printer = IRPrinter()
println(printer.print(module))
```

This generates the following LLVM IR:

```llvm
define i32 @add(i32 %arg0, i32 %arg1) {
entry:
  %result = add i32 %arg0, %arg1
  ret i32 %result
}
```

## Project Structure

```
src/
├── main/kotlin/space/norb/llvm/
│   ├── core/               # Core abstractions (Value, Type, User, Constant)
│   ├── types/              # Type system implementations
│   ├── values/             # Value implementations (constants, globals)
│   ├── structure/          # Structural components (Module, Function, BasicBlock)
│   ├── instructions/       # Instruction hierarchy
│   │   ├── base/           # Base instruction classes
│   │   ├── terminators/    # Terminator instructions
│   │   ├── binary/         # Binary operations
│   │   ├── memory/         # Memory operations
│   │   ├── casts/          # Type casting operations
│   │   └── other/          # Other instructions (calls, comparisons, phi)
│   ├── builder/            # IR construction utilities
│   ├── visitors/           # Visitor pattern implementations
│   └── enums/              # Enumerations and constants
└── test/kotlin/            # Comprehensive test suite
```

## Supported LLVM Features

### Types
- ✅ Primitive types (void, integers, floating-point)
- ✅ Derived types (pointers, arrays, structs, functions)
- ✅ Untyped pointers (compliant with latest LLVM IR)

### Instructions
- ✅ **Terminators**: ret, br, switch
- ✅ **Binary Operations**: add, sub, mul, sdiv, and, or, xor
- ✅ **Memory Operations**: alloca, load, store, getelementptr
- ✅ **Cast Operations**: trunc, zext, sext, bitcast
- ✅ **Other Operations**: call, icmp, phi

### Structural Components
- ✅ Modules with functions and global variables
- ✅ Functions with parameters and basic blocks
- ✅ Basic blocks with instruction sequences
- ✅ Global variables with various linkage types

## Documentation

- [📖 Detailed Design Outline](docs/detailed-outline.md) - Comprehensive class layout and design
- [🗺️ Implementation Roadmap](docs/roadmap.md) - Phased implementation plan
- [📋 Project Outline](docs/outline.md) - High-level design philosophy
- [🔄 Migration Guide](docs/ptr-migration-todo.md) - Pointer model migration details

## Building and Testing

### Prerequisites
- JDK 21 or later
- Kotlin 2.2.0

### Build

```bash
./gradlew build
```

### Run Tests

```bash
./gradlew test
```

### Generate Documentation

```bash
./gradlew dokkaHtml
```

## Examples

### Function with Control Flow

```kotlin
// Create a function that returns the absolute value of an integer
val absFunction = builder.createFunction(
    "abs",
    FunctionType(IntegerType(32), listOf(IntegerType(32)))
)

val entryBlock = builder.insertBasicBlock("entry", absFunction)
val positiveBlock = builder.insertBasicBlock("positive", absFunction)
val negativeBlock = builder.insertBasicBlock("negative", absFunction)

// Entry block
builder.positionAtEnd(entryBlock)
val param = absFunction.parameters[0]
val isNegative = builder.insertICmp(IcmpPredicate.SLT, param, BuilderUtils.getIntConstant(0, IntegerType(32)), "isneg")
builder.insertCondBr(isNegative, negativeBlock, positiveBlock)

// Positive block
builder.positionAtEnd(positiveBlock)
builder.insertRet(param)

// Negative block
builder.positionAtEnd(negativeBlock)
val negated = builder.insertNeg(param, "neg")
builder.insertRet(negated)
```

### Working with Arrays and Structs

```kotlin
// Define a struct type
val personType = StructType(listOf(
    IntegerType(32),  // age
    PointerType()     // name (string pointer)
))

// Create a global array of persons
val personArrayType = ArrayType(10, personType)
val globalPeople = GlobalVariable(
    "people",
    PointerType(),
    module,
    null, // initializer
    false, // isConstant
    LinkageType.INTERNAL
)
module.globalVariables.add(globalPeople)

// Access array element
val index = BuilderUtils.getIntConstant(2, IntegerType(32))
val elementPtr = builder.insertGep(
    personType,
    globalPeople,
    listOf(BuilderUtils.getIntConstant(0, IntegerType(32)), index),
    "element_ptr"
)
```

## Contributing

We welcome contributions! Please see our [Contributing Guide](CONTRIBUTING.md) for details.

### Development Setup

1. Clone the repository
2. Import into IntelliJ IDEA or use the command line
3. Run `./gradlew build` to ensure everything builds
4. Make your changes
5. Add tests for new functionality
6. Run `./gradlew test` to ensure all tests pass
7. Submit a pull request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
