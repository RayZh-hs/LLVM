# Structures Example

This example demonstrates how to work with complex data structures using named struct types, memory allocation, and field access via GEP (GetElementPtr). The full source can be found in [src/main/kotlin/space/norb/llvm/examples/StructExample.kt](../../src/main/kotlin/space/norb/llvm/examples/StructExample.kt).

## Defining a Named Struct

Named structs allow you to define custom types that can be reused throughout your module.

```kotlin
val pointType = module.registerNamedStructType(
    name = "Point",
    elementTypes = listOf(
        BuilderUtils.getIntType(32),    // x coordinate
        BuilderUtils.getIntType(32),    // y coordinate
    ),
)
```

In this case, we define a `Point` struct with two `i32` fields.

## Memory Management

To manipulate structures, we often allocate space for them on the stack.

```kotlin
val ptr = builder.insertAlloca(pointType, "pointAlloca")
```

## Accessing Fields with GEP

The `getelementptr` instruction is used to calculate the addresses of struct fields.

```kotlin
// Offset into the allocation is 0, then offset into the struct is 0 for 'x'
val xFieldPtr = builder.insertGep(
    pointType, 
    ptr, 
    listOf(BuilderUtils.getIntConstant(0L, 32), BuilderUtils.getIntConstant(0L, 32)), 
    "xFieldPtr"
)

// Offset into the allocation is 0, then offset into the struct is 1 for 'y'
val yFieldPtr = builder.insertGep(
    pointType, 
    ptr, 
    listOf(BuilderUtils.getIntConstant(0L, 32), BuilderUtils.getIntConstant(1L, 32)), 
    "yFieldPtr"
)
```

## Storing and Loading

Once we have the pointers to the fields, we can store values into them or load the entire structure.

```kotlin
builder.insertStore(xParam, xFieldPtr)
builder.insertStore(yParam, yFieldPtr)

val loadedPoint = builder.insertLoad(pointType, ptr, "loadedPoint")
builder.insertRet(loadedPoint)
```

## Running the Example

Execute the example via Gradle:

```bash
./gradlew -PmainClass=space.norb.llvm.examples.StructExampleKt run
```

The output will contain the named struct definition and the `PointFactory` function implementation.
