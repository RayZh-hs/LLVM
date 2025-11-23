# Instruction Set Documentation

Kotlin-LLVM provides a comprehensive set of instructions that map to LLVM IR instructions.

## Overview

Instructions are the building blocks of LLVM IR. Each instruction performs a specific operation and produces a value (except terminator instructions).

## Binary Operations

### Arithmetic Operations
```kotlin
// Addition
val sum = builder.insertAdd(a, b, "sum")

// Subtraction
val diff = builder.insertSub(a, b, "diff")

// Multiplication
val product = builder.insertMul(a, b, "product")

// Signed Division
val quotient = builder.insertSDiv(a, b, "quotient")

// Unsigned Remainder (Modulo)
val uremResult = builder.insertURem(a, b, "urem")

// Signed Remainder (Modulo)
val sremResult = builder.insertSRem(a, b, "srem")
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

### Convenience Operations
```kotlin
// Bitwise NOT (using XOR with all ones)
val notResult = builder.insertNot(value, "not")

// Negation (using subtraction from zero)
val negResult = builder.insertNeg(value, "neg")
```

### Modulo Operations
```kotlin
// Unsigned remainder (modulo)
// Computes the remainder of unsigned integer division
// Result has the same sign as the dividend (first operand)
val uremResult = builder.insertURem(a, b, "urem")

// Signed remainder (modulo)
// Computes the remainder of signed integer division
// Result has the same sign as the dividend (first operand)
val sremResult = builder.insertSRem(a, b, "srem")
```

**Important Notes about Modulo Operations:**
- Both `urem` and `srem` are **not commutative**: `a % b != b % a`
- Both are **not associative**: `(a % b) % c != a % (b % c)`
- Division by zero results in **undefined behavior**
- For `srem`, the result sign follows the dividend (first operand)
- For `urem`, operands are treated as unsigned values

**Example Usage:**
```kotlin
// Create a function that computes modulo operations
val functionType = FunctionType(
    returnType = IntegerType.I32,
    paramTypes = listOf(IntegerType.I32, IntegerType.I32)
)

val function = builder.createFunction("modulo_example", functionType)
val entryBlock = function.insertBasicBlock("entry")
builder.positionAtEnd(entryBlock)

val a = function.parameters[0]  // First operand
val b = function.parameters[1]  // Second operand

// Compute signed remainder
val sremResult = builder.insertSRem(a, b, "srem_result")

// Compute unsigned remainder
val uremResult = builder.insertURem(a, b, "urem_result")

// Return the signed remainder
builder.insertRet(sremResult)
```

## Memory Operations

### Allocation and Access
```kotlin
// Stack allocation
val alloca = builder.insertAlloca(IntegerType.I32, "var")

// Load from memory
val value = builder.insertLoad(IntegerType.I32, ptr, "value")

// Store to memory
builder.insertStore(value, ptr)

// Get Element Pointer
val gep = builder.insertGep(IntegerType.I32, ptr, indices, "element")
```

### Memory Operations with BuilderUtils
```kotlin
// Create zero value for initialization
val zero = BuilderUtils.createZeroValue(IntegerType.I32)

// Create null pointer
val nullPtr = BuilderUtils.getNullPointer()

// Validate pointer types
BuilderUtils.validatePointerType(ptr)
```

## Comparison Operations

### Integer Comparisons
```kotlin
// Equal
val eq = builder.insertICmp(IcmpPredicate.EQ, a, b, "eq")

// Not Equal
val ne = builder.insertICmp(IcmpPredicate.NE, a, b, "ne")

// Signed Greater Than
val sgt = builder.insertICmp(IcmpPredicate.SGT, a, b, "sgt")

// Signed Greater Than or Equal
val sge = builder.insertICmp(IcmpPredicate.SGE, a, b, "sge")

// Signed Less Than
val slt = builder.insertICmp(IcmpPredicate.SLT, a, b, "slt")

// Signed Less Than or Equal
val sle = builder.insertICmp(IcmpPredicate.SLE, a, b, "sle")

// Unsigned Greater Than
val ugt = builder.insertICmp(IcmpPredicate.UGT, a, b, "ugt")

// Unsigned Greater Than or Equal
val uge = builder.insertICmp(IcmpPredicate.UGE, a, b, "uge")

// Unsigned Less Than
val ult = builder.insertICmp(IcmpPredicate.ULT, a, b, "ult")

// Unsigned Less Than or Equal
val ule = builder.insertICmp(IcmpPredicate.ULE, a, b, "ule")
```

## Cast Operations

### Integer Casts
```kotlin
// Truncate integer
val truncated = builder.insertTrunc(value, IntegerType.I8, "truncated")

// Zero extend integer
val zext = builder.insertZExt(value, IntegerType.I64, "zext")

// Sign extend integer
val sext = builder.insertSExt(value, IntegerType.I64, "sext")
```

### Pointer Casts
```kotlin
// Bitcast between types
val bitcast = builder.insertBitcast(ptr, PointerType, "bitcast")
```

## Other Operations

### Phi Nodes
```kotlin
// Phi node for values from different basic blocks
val incomingValues = listOf(
    Pair(value1, block1),
    Pair(value2, block2)
)
val phi = builder.insertPhi(IntegerType.I32, incomingValues, "merge")
```

### Function Calls
```kotlin
// Direct function call
val result = builder.insertCall(
    function = targetFunction,
    args = listOf(arg1, arg2, arg3),
    name = "result"
)

// Indirect function call
val indirectResult = builder.insertIndirectCall(
    funcPtr = functionPtr,
    args = listOf(arg1, arg2),
    returnType = IntegerType.I32,
    name = "result"
)
```

## Terminator Instructions

### Return Instructions
```kotlin
// Return void
builder.insertRetVoid()

// Return value
builder.insertRet(value)
```

### Branch Instructions
```kotlin
// Unconditional branch
builder.insertBr(targetBlock)

// Conditional branch
builder.insertCondBr(condition, trueBlock, falseBlock)
```

### Switch Instruction
```kotlin
// Switch statement
val cases = listOf(
    Pair(constant1, case1Block),
    Pair(constant2, case2Block),
    Pair(constant3, case3Block)
)
builder.insertSwitch(value, defaultBlock, cases, "switch")
```

## Instruction Properties

### Naming
Instructions can be named for easier reference:
```kotlin
val result = builder.insertAdd(a, b, "result")  // Named "result"
```

If no name is provided, a unique name is automatically generated:
```kotlin
val autoNamed = builder.insertAdd(a, b)  // Auto-generated name
```

### Type Validation
The builder automatically validates instruction operands:
```kotlin
// This will throw an exception if types don't match
try {
    val result = builder.insertAdd(intValue, floatValue) // Different types
} catch (e: IllegalArgumentException) {
    println("Type mismatch: ${e.message}")
}
```

### Manual Validation
You can manually validate operands before creating instructions:
```kotlin
// Validate binary operands
BuilderUtils.validateBinaryOperands(lhs, rhs)

// Validate pointer type
BuilderUtils.validatePointerType(ptr)

// Validate integer type
BuilderUtils.validateIntegerType(value)

// Validate floating point type
BuilderUtils.validateFloatingPointType(value)
```

## Instruction Hierarchy

### Base Classes
All instructions inherit from specific base classes:

1. **[`Instruction`](src/main/kotlin/space/norb/llvm/instructions/base/Instruction.kt)** - Base class for all instructions
2. **[`BinaryInst`](src/main/kotlin/space/norb/llvm/instructions/base/BinaryInst.kt)** - Base class for binary operations
3. **[`TerminatorInst`](src/main/kotlin/space/norb/llvm/instructions/base/TerminatorInst.kt)** - Base class for terminator instructions
4. **[`MemoryInst`](src/main/kotlin/space/norb/llvm/instructions/base/MemoryInst.kt)** - Base class for memory operations
5. **[`CastInst`](src/main/kotlin/space/norb/llvm/instructions/base/CastInst.kt)** - Base class for cast operations
6. **[`OtherInst`](src/main/kotlin/space/norb/llvm/instructions/base/OtherInst.kt)** - Base class for other operations

### Instruction Categories

#### Binary Instructions
- [`AddInst`](src/main/kotlin/space/norb/llvm/instructions/binary/AddInst.kt) - Addition
- [`SubInst`](src/main/kotlin/space/norb/llvm/instructions/binary/SubInst.kt) - Subtraction
- [`MulInst`](src/main/kotlin/space/norb/llvm/instructions/binary/MulInst.kt) - Multiplication
- [`SDivInst`](src/main/kotlin/space/norb/llvm/instructions/binary/SDivInst.kt) - Signed division
- [`URemInst`](src/main/kotlin/space/norb/llvm/instructions/binary/URemInst.kt) - Unsigned remainder (modulo)
- [`SRemInst`](src/main/kotlin/space/norb/llvm/instructions/binary/SRemInst.kt) - Signed remainder (modulo)
- [`AndInst`](src/main/kotlin/space/norb/llvm/instructions/binary/AndInst.kt) - Bitwise AND
- [`OrInst`](src/main/kotlin/space/norb/llvm/instructions/binary/OrInst.kt) - Bitwise OR
- [`XorInst`](src/main/kotlin/space/norb/llvm/instructions/binary/XorInst.kt) - Bitwise XOR

#### Terminator Instructions
- [`ReturnInst`](src/main/kotlin/space/norb/llvm/instructions/terminators/ReturnInst.kt) - Function return
- [`BranchInst`](src/main/kotlin/space/norb/llvm/instructions/terminators/BranchInst.kt) - Conditional/unconditional branch
- [`SwitchInst`](src/main/kotlin/space/norb/llvm/instructions/terminators/SwitchInst.kt) - Multi-way branch

#### Memory Instructions
- [`AllocaInst`](src/main/kotlin/space/norb/llvm/instructions/memory/AllocaInst.kt) - Stack allocation
- [`LoadInst`](src/main/kotlin/space/norb/llvm/instructions/memory/LoadInst.kt) - Load from memory
- [`StoreInst`](src/main/kotlin/space/norb/llvm/instructions/memory/StoreInst.kt) - Store to memory
- [`GetElementPtrInst`](src/main/kotlin/space/norb/llvm/instructions/memory/GetElementPtrInst.kt) - Pointer arithmetic

#### Cast Instructions
- [`BitcastInst`](src/main/kotlin/space/norb/llvm/instructions/casts/BitcastInst.kt) - Bitwise cast
- [`TruncInst`](src/main/kotlin/space/norb/llvm/instructions/casts/TruncInst.kt) - Integer truncation
- [`ZExtInst`](src/main/kotlin/space/norb/llvm/instructions/casts/ZExtInst.kt) - Zero extension
- [`SExtInst`](src/main/kotlin/space/norb/llvm/instructions/casts/SExtInst.kt) - Sign extension

#### Other Instructions
- [`CallInst`](src/main/kotlin/space/norb/llvm/instructions/other/CallInst.kt) - Function call
- [`ICmpInst`](src/main/kotlin/space/norb/llvm/instructions/other/ICmpInst.kt) - Integer comparison
- [`PhiNode`](src/main/kotlin/space/norb/llvm/instructions/other/PhiNode.kt) - PHI node
- [`CommentAttachment`](src/main/kotlin/space/norb/llvm/instructions/other/CommentAttachment.kt) - Emits an inline IR comment (no runtime semantics)

## Best Practices

1. **Use descriptive names** for instructions
2. **Group related instructions** together
3. **Use appropriate comparison predicates** for your use case
4. **Validate operands** before creating instructions when needed
5. **Use convenience methods** for common operations
6. **Document complex instruction sequences** with comments
7. **Handle type mismatches** gracefully with try-catch blocks
8. **Use the builder pattern** for clean, readable code
