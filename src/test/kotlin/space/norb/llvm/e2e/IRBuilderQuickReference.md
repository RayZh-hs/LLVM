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
val entryBlock = builder.createBasicBlock("entry", function)
val thenBlock = builder.createBasicBlock("then", function)
val elseBlock = builder.createBasicBlock("else", function)

// Add blocks to function
function.basicBlocks.addAll(listOf(entryBlock, thenBlock, elseBlock))

// Set entry block
if (function.entryBlock == null) {
    function.entryBlock = entryBlock
}

// Position builder at end of block
builder.positionAtEnd(entryBlock)
```

## Binary Operations

```kotlin
// Arithmetic operations
val addResult = builder.buildAdd(lhs, rhs, "result")
val subResult = builder.buildSub(lhs, rhs, "result")
val mulResult = builder.buildMul(lhs, rhs, "result")
val divResult = builder.buildSDiv(lhs, rhs, "result")

// Bitwise operations
val andResult = builder.buildAnd(lhs, rhs, "result")
val orResult = builder.buildOr(lhs, rhs, "result")
val xorResult = builder.buildXor(lhs, rhs, "result")
```

## Comparison Operations

```kotlin
// Integer comparisons
val eq = builder.buildICmp(IcmpPredicate.EQ, lhs, rhs, "eq")
val ne = builder.buildICmp(IcmpPredicate.NE, lhs, rhs, "ne")
val slt = builder.buildICmp(IcmpPredicate.SLT, lhs, rhs, "slt")
val sle = builder.buildICmp(IcmpPredicate.SLE, lhs, rhs, "sle")
val sgt = builder.buildICmp(IcmpPredicate.SGT, lhs, rhs, "sgt")
val sge = builder.buildICmp(IcmpPredicate.SGE, lhs, rhs, "sge")
```

## Control Flow

```kotlin
// Unconditional branch
builder.buildBr(targetBlock)

// Conditional branch
builder.buildCondBr(condition, trueBlock, falseBlock)

// Switch statement
val switchInst = builder.buildSwitch(
    condition, 
    defaultBlock, 
    listOf(
        Pair(BuilderUtils.getIntConstant(1, IntegerType.I32), case1Block),
        Pair(BuilderUtils.getIntConstant(2, IntegerType.I32), case2Block)
    )
)

// Return statements
builder.buildRet(value)           // Return with value
builder.buildRetVoid()            // Return void
```

## Memory Operations

```kotlin
// Allocate memory on stack
val alloca = builder.buildAlloca(IntegerType.I32, "var")

// Store value to memory
builder.buildStore(value, pointer)

// Load value from memory
val loaded = builder.buildLoad(IntegerType.I32, pointer, "loaded")

// Get element pointer (array indexing)
val index = BuilderUtils.getIntConstant(2, IntegerType.I32)
val gep = builder.buildGep(IntegerType.I32, arrayPtr, listOf(index), "elem_ptr")
```

## Phi Nodes

```kotlin
// Create phi node with incoming values
val phi = builder.buildPhi(
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
val bitcast = builder.buildBitcast(value, destType, "bitcast")

// Sign extend
val sext = builder.buildSExt(value, destType, "sext")

// Zero extend
val zext = builder.buildZExt(value, destType, "zext")

// Truncate
val trunc = builder.buildTrunc(value, destType, "trunc")
```

## Function Calls

```kotlin
// Direct function call
val callResult = builder.buildCall(
    targetFunction, 
    listOf(arg1, arg2, arg3), 
    "call_result"
)

// Indirect function call
val indirectCall = builder.buildIndirectCall(
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
val entryBlock = builder.createBasicBlock("entry", function)
val thenBlock = builder.createBasicBlock("then", function)
val elseBlock = builder.createBasicBlock("else", function)
val mergeBlock = builder.createBasicBlock("merge", function)

// Entry block
builder.positionAtEnd(entryBlock)
val condition = builder.buildICmp(IcmpPredicate.NE, conditionValue, zero, "condition")
builder.buildCondBr(condition, thenBlock, elseBlock)

// Then block
builder.positionAtEnd(thenBlock)
val thenResult = builder.buildAdd(a, b, "then_result")
builder.buildBr(mergeBlock)

// Else block
builder.positionAtEnd(elseBlock)
val elseResult = builder.buildSub(a, b, "else_result")
builder.buildBr(mergeBlock)

// Merge block
builder.positionAtEnd(mergeBlock)
val result = builder.buildPhi(
    IntegerType.I32, 
    listOf(
        Pair(thenResult, thenBlock),
        Pair(elseResult, elseBlock)
    ), 
    "result"
)
builder.buildRet(result)
```

### Simple Function Pattern

```kotlin
// Create function
val functionType = FunctionType(IntegerType.I32, listOf(IntegerType.I32, IntegerType.I32))
val function = builder.createFunction("add", functionType)
module.functions.add(function)

// Create entry block
val entryBlock = builder.createBasicBlock("entry", function)
function.basicBlocks.add(entryBlock)
function.entryBlock = entryBlock

// Build function body
builder.positionAtEnd(entryBlock)
val arg0 = function.parameters[0]
val arg1 = function.parameters[1]
val result = builder.buildAdd(arg0, arg1, "result")
builder.buildRet(result)