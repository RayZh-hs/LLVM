# Code Examples and Tutorials

This section contains practical examples and tutorials for using Kotlin-LLVM.

## Available Example

- **[Quick Start](quick-start.md)** – walk-through that mirrors the APIs available in `space.norb.llvm`

The repository’s automated tests (see `src/test`) contain additional focused snippets—for instance, the extracted end-to-end tests under `src/test/resources/space/norb/llvm/e2e/` demonstrate how to build call sites, arithmetic kernels, and casting sequences with the public API. Those sources are a good complement while we expand the written examples.

## Running Examples

Examples are regular Kotlin programs. Run them the same way you run the rest of the project—for example:

1. Clone or open the repository
2. Execute `./gradlew test` to make sure everything compiles
3. Run the example file from your IDE or with `./gradlew run` after wiring it into a small `main`

For environment setup, see the [Getting Started Guide](../guides/getting-started.md).