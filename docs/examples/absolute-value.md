# Absolute Value Example

This example demonstrates how to implement conditional logic using branches, integer comparisons, and arithmetic operations. The full source can be found in [src/main/kotlin/space/norb/llvm/examples/AbsExample.kt](../../src/main/kotlin/space/norb/llvm/examples/AbsExample.kt).

## Module Setup

```kotlin
val mod = Module("SimpleModule")
val builder = IRBuilder(mod)
```

## Creating the Function

We create a function `abs` that takes one 32-bit integer and returns its absolute value.

```kotlin
val func = builder.createFunction(
    name = "abs",
    returnType = TypeUtils.I32,
    paramTypes = listOf(TypeUtils.I32),
    isVarArg = false
)
val param = func.parameters[0]
```

Note: `createFunction` creates the function object but doesn't automatically add it to the module. You must add it to `mod.functions` manually later.

## Implementing Logic with Basic Blocks

We define three blocks: `entry`, `trueBlock`, and `falseBlock`.

### Comparison and Branching

In the `entry` block, we compare the input with zero.

```kotlin
val entryBlock = func.insertBasicBlock("entry")
builder.positionAtEnd(entryBlock)

val cmp = builder.insertICmp(
    pred = IcmpPredicate.SGT, 
    lhs = param, 
    rhs = IntConstant(0L, TypeUtils.I32 as IntegerType)
)

builder.insertCondBr(
    condition = cmp,
    trueTarget = trueBlock,
    falseTarget = falseBlock
)
```

### The "True" Case

If the input is greater than 0, we simply return it.

```kotlin
val trueBlock = func.insertBasicBlock("trueBlock")
builder.positionAtEnd(trueBlock)
builder.insertRet(param)
```

### The "False" Case (Negation)

If the input is 0 or less, we negate it and return the result.

```kotlin
val falseBlock = func.insertBasicBlock("falseBlock")
builder.positionAtEnd(falseBlock)
val negVal = builder.insertNeg(param, name = "neg")
builder.insertRet(negVal)
```

## Finalizing the Function

Finally, we ensure the function's block list is correctly set and add the function to the module.

```kotlin
func.setBasicBlocks(listOf(entryBlock, trueBlock, falseBlock))
mod.functions.add(func)
```

## Running the Example

Execute the example via Gradle:

```bash
./gradlew -PmainClass=space.norb.llvm.examples.AbsExampleKt run
```

The emitted IR will show the control flow graph for the `abs` function.
