# Testing Guide

The Kotlin-LLVM project ships with a comprehensive test suite built on JUnit 5. This guide explains how the existing tests are structured and how to add new ones that exercise the current APIs.

## Test Categories

| Location | Focus |
| --- | --- |
| `src/test/kotlin/space/norb/llvm/core` | Type utilities, first-class/single-value/aggregate classification, and compatibility helpers |
| `src/test/kotlin/space/norb/llvm/instructions` | Instruction construction semantics and validation |
| `src/test/resources/space/norb/llvm/e2e` | Extracted end-to-end scenarios that build full modules and compare printed IR |
| `src/test/kotlin/space/norb/llvm/values` | Constant and metadata behaviour |

Integration-style tests often combine Kotlin builders with expected `.ll` fixtures so that both semantic checks and textual IR verification occur.

## Creating a Unit Test

Tests use JUnit 5 (`@Test`, `@Nested`, `@DisplayName`) together with assertions from `org.junit.jupiter.api.Assertions`.

```kotlin
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import space.norb.llvm.builder.IRBuilder
import space.norb.llvm.structure.Module
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.IntegerType

class AddFunctionTest {
    @Test
    fun `builder emits add instruction`() {
        val module = Module("test")
        val functionType = FunctionType(IntegerType.I32, listOf(IntegerType.I32, IntegerType.I32))
        val function = module.registerFunction("add", functionType)
        val entry = function.insertBasicBlock("entry", setAsEntrypoint = true)

        val builder = IRBuilder(module)
        builder.positionAtEnd(entry)

        val sum = builder.insertAdd(function.parameters[0], function.parameters[1], "sum")
        builder.insertRet(sum)

        val ir = module.toIRString()
        assertEquals(true, ir.contains("%sum = add i32"))
    }
}
```

This pattern mirrors the snippets found in `TypeCompatibilityTest` and the extracted e2e fixtures.

## Validating IR Structure

`space.norb.llvm.visitors.IRValidator` walks a module and checks structural invariants (terminators, operand counts, phi node inputs, etc.). In tests you can use it directly:

```kotlin
import org.junit.jupiter.api.Assertions.assertTrue
import space.norb.llvm.visitors.IRValidator

val validator = IRValidator()
assertTrue(validator.visitModule(module), "Module failed validation")
```

## End-to-End Comparisons

The e2e resources under `src/test/resources/space/norb/llvm/e2e` package common IR patterns into Kotlin builders plus `.ll` expectations. Each scenario:

1. Builds a module with `Module`, `IRBuilder`, and the instruction classes
2. Prints the module with `module.toIRString()`
3. Normalises the IR (to account for auto-generated names) before comparing it with an expected file

Use these fixtures as templates when you need to add a regression test for a complex IR sequence.

## Running Tests

```bash
./gradlew test              # Run everything
./gradlew test --tests "*TypeCompatibilityTest"   # Filter by class or pattern
```

Gradle exposes the full set of JUnit filters; invoke `./gradlew help --task test` for more options. Tests can also be executed from your IDE.

## Tips for New Tests

- Prefer working from the same APIs used by production code (`Module.registerFunction`, `Function.insertBasicBlock`, `IRBuilder` helpers).
- Keep manual string comparisons focused on the segments you care about; let `IRValidator` enforce structural correctness.
- When adding new instruction classes, accompany them with unit tests and, if applicable, an e2e scenario that exercises `IRPrinter` output.
- Match the code style already present in the tests (JUnit 5 annotations, descriptive display names).

Accurate tests double as usage examples, so keep them up to date whenever the public API changes.
