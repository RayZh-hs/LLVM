# IR Builder Quick Reference

This document provides a quick reference for commonly used IR Builder methods when writing end-to-end tests.

## Creating Constants

```kotlin
// Integer constants
val zero = BuilderUtils.getIntConstant(0, IntegerType.I32)
val one = BuilderUtils.getIntConstant(1, IntegerType.I32)
val ten = BuilderUtils.getIntConstant(10, IntegerType.I32)

// Using bit width directly
val const64 = BuilderUtils.getIntConstant(42, 64)  // Creates i64 constant
```

## Creating Functions and Basic Blocks

```kotlin
// Create function type
val functionType = FunctionType(
    returnType = IntegerType.I32,
    paramTypes = listOf(IntegerType.I32, IntegerType.I32)
)

// Create function
val function = builder.createFunction("function_name", functionType)
module.functions.add(function)

// Create basic blocks
val entryBlock = function.insertBasicBlock("entry", setAsEntrypoint = true)
val thenBlock = function.insertBasicBlock("then")
val elseBlock = function.insertBasicBlock("else")

// Note: The first block added to a function automatically becomes the entrypoint
// unless setAsEntrypoint is explicitly set to false

// Position builder at end of block
builder.positionAtEnd(entryBlock)
```

## Binary Operations

```kotlin
// Arithmetic operations
val addResult = builder.insertAdd(lhs, rhs, "result")
val subResult = builder.insertSub(lhs, rhs, "result")
val mulResult = builder.insertMul(lhs, rhs, "result")
val divResult = builder.insertSDiv(lhs, rhs, "result")

// Bitwise operations
val andResult = builder.insertAnd(lhs, rhs, "result")
val orResult = builder.insertOr(lhs, rhs, "result")
val xorResult = builder.insertXor(lhs, rhs, "result")
```

## Comparison Operations

```kotlin
// Integer comparisons
val eq = builder.insertICmp(IcmpPredicate.EQ, lhs, rhs, "eq")
val ne = builder.insertICmp(IcmpPredicate.NE, lhs, rhs, "ne")
val slt = builder.insertICmp(IcmpPredicate.SLT, lhs, rhs, "slt")
val sle = builder.insertICmp(IcmpPredicate.SLE, lhs, rhs, "sle")
val sgt = builder.insertICmp(IcmpPredicate.SGT, lhs, rhs, "sgt")
val sge = builder.insertICmp(IcmpPredicate.SGE, lhs, rhs, "sge")
```

## Control Flow

```kotlin
// Unconditional branch
builder.insertBr(targetBlock)

// Conditional branch
builder.insertCondBr(condition, trueBlock, falseBlock)

// Switch statement
val switchInst = builder.insertSwitch(
    condition, 
    defaultBlock, 
    listOf(
        Pair(BuilderUtils.getIntConstant(1, IntegerType.I32), case1Block),
        Pair(BuilderUtils.getIntConstant(2, IntegerType.I32), case2Block)
    )
)

// Return statements
builder.insertRet(value)           // Return with value
builder.insertRetVoid()            // Return void
```

## Memory Operations

```kotlin
// Allocate memory on stack
val alloca = builder.insertAlloca(IntegerType.I32, "var")

// Store value to memory
builder.insertStore(value, pointer)

// Load value from memory
val loaded = builder.insertLoad(IntegerType.I32, pointer, "loaded")

// Get element pointer (array indexing)
val index = BuilderUtils.getIntConstant(2, IntegerType.I32)
val gep = builder.insertGep(IntegerType.I32, arrayPtr, listOf(index), "elem_ptr")
```

## Phi Nodes

```kotlin
// Create phi node with incoming values
val phi = builder.insertPhi(
    IntegerType.I32, 
    listOf(
        Pair(value1, block1),
        Pair(value2, block2),
        Pair(value3, block3)
    ), 
    "result"
)
```

## Cast Operations

```kotlin
// Bit cast
val bitcast = builder.insertBitcast(value, destType, "bitcast")

// Sign extend
val sext = builder.insertSExt(value, destType, "sext")

// Zero extend
val zext = builder.insertZExt(value, destType, "zext")

// Truncate
val trunc = builder.insertTrunc(value, destType, "trunc")
```

## Function Calls

```kotlin
// Direct function call
val callResult = builder.insertCall(
    targetFunction, 
    listOf(arg1, arg2, arg3), 
    "call_result"
)

// Indirect function call
val indirectCall = builder.insertIndirectCall(
    functionPointer, 
    listOf(arg1, arg2), 
    returnType, 
    "indirect_result"
)
```

## Common Patterns

### If-Else Pattern

```kotlin
// Create blocks
val entryBlock = function.insertBasicBlock("entry", setAsEntrypoint = true)
val thenBlock = function.insertBasicBlock("then")
val elseBlock = function.insertBasicBlock("else")
val mergeBlock = function.insertBasicBlock("merge")

// Entry block
builder.positionAtEnd(entryBlock)
val condition = builder.insertICmp(IcmpPredicate.NE, conditionValue, zero, "condition")
builder.insertCondBr(condition, thenBlock, elseBlock)

// Then block
builder.positionAtEnd(thenBlock)
val thenResult = builder.insertAdd(a, b, "then_result")
builder.insertBr(mergeBlock)

// Else block
builder.positionAtEnd(elseBlock)
val elseResult = builder.insertSub(a, b, "else_result")
builder.insertBr(mergeBlock)

// Merge block
builder.positionAtEnd(mergeBlock)
val result = builder.insertPhi(
    IntegerType.I32, 
    listOf(
        Pair(thenResult, thenBlock),
        Pair(elseResult, elseBlock)
    ), 
    "result"
)
builder.insertRet(result)
```

### Simple Function Pattern

```kotlin
// Create function
val functionType = FunctionType(IntegerType.I32, listOf(IntegerType.I32, IntegerType.I32))
val function = builder.createFunction("add", functionType)
module.functions.add(function)

// Create entry block
val entryBlock = function.insertBasicBlock("entry", setAsEntrypoint = true)

// Build function body
builder.positionAtEnd(entryBlock)
val arg0 = function.parameters[0]
val arg1 = function.parameters[1]
val result = builder.insertAdd(arg0, arg1, "result")
builder.insertRet(result)