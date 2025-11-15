# Hello World Example

This example is the quickest way to see Kotlin-LLVM stitch together a function, a variadic declaration, and a global string literal. The full source lives in `src/main/kotlin/space/norb/llvm/examples/HelloWorldExample.kt` and can be executed like any other Kotlin program.

## Build the Module

```kotlin
val module = Module("HelloWorldModule")
val builder = IRBuilder(module)
```

We will use explicit declaration for you to follow along easily. In this step, we create a module and an IR builder to help us insert instructions.

## Declare `printf`

```kotlin
val printf = module.registerFunction(
    name = "printf",
    type = FunctionType(
        returnType = IntegerType.I32,
        paramTypes = listOf(PointerType),
        isVarArg = true
    ),
    linkage = LinkageType.EXTERNAL,
)
```

Using `registerFunction` with `isVarArg = true` models the C `printf` signature. The linkage stays `EXTERNAL` so that the call is resolved when you link against libc.

Unlike IIVM C++ Infrastructure where the IR Builder does almost every operation, in this Kotlin binding we leverage Extension functions to do most of the work, making the code easier to read. Remember that idiomatically, IR Builder here is only used to insert new instructions, as we will see later.

## Create the String Literal

```kotlin
val literal = "Hello, World!\n\u0000"
val literalType = ArrayType(literal.length, IntegerType.I8)
val literalVar = module.registerGlobalVariable(
    name = "str.literal",
    initialValue = ArrayConstant(
        elements = literal.map { ch ->
            IntConstant(ch.code.toLong(), IntegerType.I8)
        },
        type = literalType,
    )
)
```

Globals are stored as raw `ptr` in LLVM IR, so the constant array keeps track of the element type for you.

## Setting up the `main` function

```kotlin
val main = module.registerFunction(
    name = "main",
    returnType = IntegerType.I32,
    parameterTypes = emptyList(),
)
val entry = main.insertBasicBlock("entry", setAsEntrypoint = true)
builder.positionAtEnd(entry)

val literalPtr = builder.insertGep(
    elementType = literalType,
    address = literalVar,
    indices = listOf(0.asConst(32), 0.asConst(32)),
    name = "str.ptr"
)
builder.insertCall(printf, listOf(literalPtr))
builder.insertRet(0.asConst(32))
```

The helper `asConst(32)` (defined in `space.norb.llvm.utils`) creates the integer constants used for the GEP indices and function return.

# Printing the IR

```kotlin
println(module.emitIR())
```

We call `emitIR` on the module to get the final IR as a string. This is a shorthand that wraps around the IR Printer that walks the module and generates a textual representation.

## Let's Run It

Execute the example from your IDE or via Gradle:

```bash
./gradlew -PmainClass=space.norb.llvm.examples.HelloWorldExampleKt run
```

The emitted IR contains the `printf` declaration, the string literal, and the `main` function that calls it. Link the IR with your preferred LLVM toolchain if you want to run it outside of Kotlin, using `main` as the entry point.
