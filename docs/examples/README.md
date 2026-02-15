# Code Examples and Tutorials

This section contains practical examples and tutorials for using Kotlin-LLVM.

## Available Examples

- **[Quick Start](quick-start.md)** – walk-through that mirrors the APIs available in `space.norb.llvm`
- **[Hello World](hello-world.md)** – demonstrates globals, variadic declarations, and function calls
- **[Absolute Value](absolute-value.md)** – shows conditional logic, branching, and comparison
- **[Structures](structures.md)** – demonstrates named struct types and pointer arithmetic with GEP
- **[Modulo Operations](modulo.md)** – demonstrates signed and unsigned remainder instructions
- **[Array Constants](array-constants.md)** – shows global array initialization and element access
- **Function Linkage (below)** – illustrates controlling function visibility with the new linkage support

The repository’s automated tests (see `src/test`) contain additional focused snippets—for instance, the extracted end-to-end tests under `src/test/resources/space/norb/llvm/e2e/` demonstrate how to build call sites, arithmetic kernels, and casting sequences with the public API. Those sources are a good complement while we expand the written examples.

## Running Examples

Examples are regular Kotlin programs. Run them the same way you run the rest of the project—for example:

1. Clone or open the repository
2. Execute `./gradlew test` to make sure everything compiles
3. Run the example file from your IDE or with `./gradlew run` after wiring it into a small `main`

For environment setup, see the [Getting Started Guide](../guides/getting-started.md).

## Function Linkage Reference

Functions now accept explicit linkage metadata—the same variants exposed in LLVM (EXTERNAL, INTERNAL, PRIVATE, WEAK, DLL_IMPORT, etc.). Use this when you need helpers that stay local to a module or declarations that resolve at link time:

```kotlin
// Internal helper keeps implementation details inside this module
val helper = module.registerFunction(
    name = "helper",
    returnType = TypeUtils.I32,
    paramTypes = listOf(TypeUtils.I32),
    linkage = LinkageType.INTERNAL
)

// External declaration remains a `declare` statement in IR
val printf = module.declareExternalFunction(
    name = "printf",
    returnType = TypeUtils.I32,
    parameterTypes = listOf(PointerType),
    isVarArg = true
)
```

Definitions default to `EXTERNAL`; pass a different `LinkageType` only when you need to override that behavior. See [`docs/api/structure.md`](../api/structure.md#function-linkage) for the complete table of supported linkage kinds.
