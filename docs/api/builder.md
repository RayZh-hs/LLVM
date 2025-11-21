# IR Builder Documentation

The IR Builder provides a fluent, type-safe API for constructing LLVM IR.

## Overview

The IR Builder is the primary interface for creating LLVM IR in Kotlin-LLVM. It provides:
- **Fluent API** - Method chaining for readable code
- **Type Safety** - Compile-time type checking
- **Context Management** - Automatic handling of insertion points
- **Convenience Methods** - Simplified common operations

## Basic Usage

### Creating a Builder
```kotlin
// Create a builder for a module
val builder = IRBuilder(module)

// The builder starts with no insertion point
// You must set the insertion point before adding instructions
```

### Managing Insertion Points
```kotlin
// Set insertion point to end of basic block
builder.positionAtEnd(basicBlock)

// Set insertion point before a specific instruction
builder.positionBefore(instruction)

// Clear insertion point
builder.clearInsertionPoint()
```

### Comment Attachments
```kotlin
// Emit a comment in the instruction stream
builder.insertComment("setup locals and store defaults")
```
- Comments are printed in IR prefixed with `;` and keep their position relative to instructions.
- Multi-line comments are supported; each line is emitted as its own `;` line.

## Function Creation

### Creating Functions
```kotlin
// Recommended: register directly on the module (function is stored automatically)
val functionType = FunctionType(
    returnType = IntegerType.I32,
    paramTypes = listOf(IntegerType.I32, IntegerType.I32),
    paramNames = listOf("a", "b")
)
val addFunction = module.registerFunction("add", functionType)

// If you need to construct the Function manually, remember to add it to the module
val helperFunction = builder.createFunction("helper", functionType)
module.functions.add(helperFunction)
```

### Basic Block Creation
```kotlin
// Create basic block in function (recommended approach)
val entryBlock = function.insertBasicBlock("entry")

// Create basic block using builder (deprecated)
val block = builder.insertBasicBlock("block", function)
```

## Value Creation

### Constants with BuilderUtils
```kotlin
// Integer constants
val zero = BuilderUtils.getIntConstant(0, IntegerType.I32)
val fortyTwo = BuilderUtils.getIntConstant(42, IntegerType.I32)
val bigInt = BuilderUtils.getIntConstant(Long.MAX_VALUE, IntegerType.I64)

// Floating point constants
val pi = BuilderUtils.getFloatConstant(3.14159f)
val e = BuilderUtils.getFloatConstant(2.71828)

// Null pointer
val nullPtr = BuilderUtils.getNullPointer()
val typedNullPtr = BuilderUtils.getNullPointer(IntegerType.I32)
```

### Special Values
```kotlin
// Zero value for any type
val intZero = BuilderUtils.createZeroValue(IntegerType.I32)
val floatZero = BuilderUtils.createZeroValue(FloatingPointType.FloatType)
val ptrZero = BuilderUtils.createZeroValue(PointerType)

// One value for numeric types
val intOne = BuilderUtils.createOneValue(IntegerType.I32)
val floatOne = BuilderUtils.createOneValue(FloatingPointType.FloatType)

// All-ones value
val allOnes = BuilderUtils.createAllOnesValue(IntegerType.I32)
```

## Arithmetic Operations

### Basic Arithmetic
```kotlin
// Addition
val sum = builder.insertAdd(a, b, "sum")

// Subtraction
val diff = builder.insertSub(a, b, "diff")

// Multiplication
val product = builder.insertMul(a, b, "product")

// Signed Division
val quotient = builder.insertSDiv(a, b, "quotient")
```

### Bitwise Operations
```kotlin
// Bitwise AND
val andResult = builder.insertAnd(a, b, "and")

// Bitwise OR
val orResult = builder.insertOr(a, b, "or")

// Bitwise XOR
val xorResult = builder.insertXor(a, b, "xor")
```

### Convenience Methods
```kotlin
// Negation (subtraction from zero)
val neg = builder.insertNeg(value, "neg")

// Bitwise NOT (XOR with all ones)
val not = builder.insertNot(value, "not")
```

## Memory Operations

### Stack Allocation
```kotlin
// Allocate space for a variable
val localVar = builder.insertAlloca(IntegerType.I32, "localVar")

// Allocate array
val arrayType = ArrayType(10, IntegerType.I32)
val array = builder.insertAlloca(arrayType, "array")
```

### Memory Access
```kotlin
// Load value
val value = builder.insertLoad(IntegerType.I32, ptr, "value")

// Store value
builder.insertStore(value, ptr)
```

### Pointer Arithmetic
```kotlin
// Get element pointer
val indices = listOf(BuilderUtils.getIntConstant(0, IntegerType.I32))
val elementPtr = builder.insertGep(IntegerType.I32, ptr, indices, "element")
```

## Control Flow

### Conditional Branching
```kotlin
// If-then-else pattern
val condition = builder.insertICmp(IcmpPredicate.NE, value, zero, "condition")
val thenBlock = function.insertBasicBlock("then")
val elseBlock = function.insertBasicBlock("else")
val mergeBlock = function.insertBasicBlock("merge")

builder.insertCondBr(condition, thenBlock, elseBlock)

// Then block
builder.positionAtEnd(thenBlock)
// ... then logic ...
builder.insertBr(mergeBlock)

// Else block
builder.positionAtEnd(elseBlock)
// ... else logic ...
builder.insertBr(mergeBlock)

// Merge block
builder.positionAtEnd(mergeBlock)
```

### Loops
```kotlin
// For loop pattern
val initBlock = function.insertBasicBlock("init")
val condBlock = function.insertBasicBlock("cond")
val bodyBlock = function.insertBasicBlock("body")
val incBlock = function.insertBasicBlock("inc")
val endBlock = function.insertBasicBlock("end")

builder.insertBr(initBlock)

// Initialization
builder.positionAtEnd(initBlock)
val counter = builder.insertAlloca(IntegerType.I32, "counter")
builder.insertStore(BuilderUtils.getIntConstant(0, IntegerType.I32), counter)
builder.insertBr(condBlock)

// Condition
builder.positionAtEnd(condBlock)
val current = builder.insertLoad(IntegerType.I32, counter, "current")
val condition = builder.insertICmp(IcmpPredicate.SLT, current, limit, "condition")
builder.insertCondBr(condition, bodyBlock, endBlock)

// Body
builder.positionAtEnd(bodyBlock)
// ... loop body logic ...
builder.insertBr(incBlock)

// Increment
builder.positionAtEnd(incBlock)
val next = builder.insertAdd(current, BuilderUtils.getIntConstant(1, IntegerType.I32), "next")
builder.insertStore(next, counter)
builder.insertBr(condBlock)

// End
builder.positionAtEnd(endBlock)
```

## Function Calls

### Direct Calls
```kotlin
// Call function with arguments
val result = builder.insertCall(
    function = targetFunction,
    args = listOf(arg1, arg2, arg3),
    name = "result"
)

// Call void function
builder.insertCall(targetFunction, listOf(arg1, arg2))
```

### Indirect Calls
```kotlin
// Call through function pointer
val result = builder.insertIndirectCall(
    funcPtr = functionPtr,
    args = listOf(arg1, arg2),
    returnType = IntegerType.I32,
    name = "result"
)
```

## Type Conversions

### Explicit Casts
```kotlin
// Truncate
val truncated = builder.insertTrunc(value, IntegerType.I8, "truncated")

// Zero extend
val zext = builder.insertZExt(value, IntegerType.I64, "zext")

// Sign extend
val sext = builder.insertSExt(value, IntegerType.I64, "sext")

// Bitcast
val bitcast = builder.insertBitcast(value, PointerType, "bitcast")
```

## Builder Utilities

### Type Utilities
```kotlin
// Create types
val intType = BuilderUtils.getIntType(32)
val voidType = BuilderUtils.getVoidType()
val ptrType = BuilderUtils.getPointerType()
val floatType = BuilderUtils.getFloatType()
val doubleType = BuilderUtils.getDoubleType()

// Create function types
val funcType = BuilderUtils.getFunctionType(
    returnType = IntegerType.I32,
    paramTypes = listOf(IntegerType.I32, IntegerType.I32)
)
```

### Type Checking
```kotlin
// Check types
val isInt = BuilderUtils.isIntegerType(type)
val isFloat = BuilderUtils.isFloatingPointType(type)
val isPtr = BuilderUtils.isPointerType(type)
val isFunc = BuilderUtils.isFunctionType(type)
```

### Type Conversion Utilities
```kotlin
// Get common type for two types
val commonType = BuilderUtils.getCommonType(type1, type2)

// Compare type sizes
val isLarger = BuilderUtils.isTypeLargerThan(type1, type2)
```

### Validation Utilities
```kotlin
// Validate operands
BuilderUtils.validateBinaryOperands(lhs, rhs)
BuilderUtils.validatePointerType(pointer)
BuilderUtils.validateIntegerType(value)
BuilderUtils.validateFloatingPointType(value)
```

## Builder Context

### Insertion Point Management
```kotlin
// Keep track of the block you want to return to manually
val savedBlock = entryBlock

builder.positionAtEnd(thenBlock)
// ... emit "then" instructions ...

builder.positionAtEnd(savedBlock)
// ... continue in the original block ...
```

### Working with Multiple Blocks
```kotlin
// Create multiple blocks
val entry = function.insertBasicBlock("entry")
val then = function.insertBasicBlock("then")
val else = function.insertBasicBlock("else")
val merge = function.insertBasicBlock("merge")

// Work in different blocks
builder.positionAtEnd(entry)
// ... entry code ...

builder.positionAtEnd(then)
// ... then code ...

builder.positionAtEnd(else)
// ... else code ...

builder.positionAtEnd(merge)
// ... merge code ...
```

## Best Practices

1. **Use descriptive names** for all values and instructions
2. **Group related operations** together in the same block
3. **Use convenience methods** when available
4. **Manage insertion points** explicitly for complex control flow
5. **Validate operands** before creating instructions when needed
6. **Use BuilderUtils** for common operations and type creation
7. **Document complex expressions** with comments
8. **Keep blocks focused** on single responsibilities
9. **Use consistent naming conventions** across your code
10. **Handle type mismatches** gracefully with proper error handling
