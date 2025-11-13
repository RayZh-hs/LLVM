# Getting Started Guide

Use this guide to get the Kotlin-LLVM project running locally and to produce your first fragment of LLVM IR with the APIs provided in this repository.

## Prerequisites

- JDK 17 or newer (the Gradle wrapper uses 17 by default)
- The Gradle wrapper that ships with the repository (`./gradlew`)
- LLVM toolchain (optional) if you want to assemble or execute the generated IR

## Clone and Build

```bash
git clone https://github.com/RayZh-hs/LLVM.git
cd LLVM
./gradlew test
```

Running the test task compiles the sources and executes the unit and integration suites under `src/test`. This is the quickest way to confirm the checkout is healthy.

## Exploring the Codebase

The implementation lives under `src/main/kotlin/space/norb/llvm/`. The most relevant entry points are:

- `structure/Module.kt` – the container for functions and globals
- `builder/IRBuilder.kt` – helper for emitting instructions
- `types/` – primitive and derived type definitions
- `instructions/` – concrete instruction classes grouped by category

The tests and extracted end-to-end examples in `src/test` are a practical reference while you experiment.

## Your First Module

Create a small Kotlin file anywhere in the repo (for example `src/test/kotlin/playground/QuickStart.kt`) and paste the following snippet. It mirrors the example that ships in `docs/examples/quick-start.md`:

```kotlin
import space.norb.llvm.builder.IRBuilder
import space.norb.llvm.structure.Module
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.IntegerType

fun main() {
    val module = Module("quick_start")
    module.targetTriple = "x86_64-pc-linux-gnu"
    module.dataLayout = "e-m:e-i64:64-f80:128-n8:16:32:64-S128"

    val functionType = FunctionType(
        returnType = IntegerType.I32,
        paramTypes = listOf(IntegerType.I32, IntegerType.I32)
    )
    val function = module.registerFunction("add", functionType)
    val entry = function.insertBasicBlock("entry", setAsEntrypoint = true)

    val builder = IRBuilder(module)
    builder.positionAtEnd(entry)

    val sum = builder.insertAdd(function.parameters[0], function.parameters[1], "sum")
    builder.insertRet(sum)

    println(module.toIRString())
}
```

Compile and run the file from your IDE or via Gradle:

```bash
./gradlew -PmainClass=playground.QuickStartKt run
```

(Use your own package name if you placed the file elsewhere.) The program prints the LLVM IR for a simple `add` function.

## Running the Test Suite

The repository ships with a comprehensive test suite. The most frequently used commands are:

```bash
./gradlew test              # Run all unit and integration tests
./gradlew :build            # Compile main and test sources and package artefacts
./gradlew clean test        # Clean and re-run the suite from scratch
```

Individual tests can be executed from your IDE. Look at `src/test/kotlin/space/norb/llvm/core/TypeCompatibilityTest.kt` or the end-to-end cases under `src/test/resources` for realistic IR construction patterns.

## What Next?

- Read the [API documentation](../api/README.md) for details on types, instructions, and builder helpers
- Browse the [Quick Start example](../examples/quick-start.md) for more patterns
- Inspect the architecture guide in `docs/architecture/overview.md` to understand how the modules fit together
- If you need to emit IR beyond the supported instruction set, extend the `instructions/` package and keep the docs in sync

By keeping experimentation inside this repository you benefit from the existing build tooling and tests, and you can contribute improvements back upstream.
