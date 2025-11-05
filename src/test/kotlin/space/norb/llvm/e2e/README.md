# LLVM IR End-to-End Test Framework

This directory contains the end-to-end test framework for comparing generated LLVM IR with expected IR strings. The framework is designed to provide comprehensive testing of IR generation while keeping the existing unit tests intact.

## Directory Structure

```
src/test/kotlin/space/norb/llvm/e2e/
├── README.md                           # This file
├── framework/                          # Test framework components
│   ├── IRTestFramework.kt             # Main test framework
│   ├── IRResourceLoader.kt            # Resource loading utilities
│   └── IRNormalizer.kt                # IR string normalization
└── SimpleArithmeticTest.kt            # Example test case

src/test/resources/space/norb/llvm/e2e/
└── SimpleArithmeticTest_add.ll        # Expected IR for test case
```

## Framework Components

### IRTestFramework

The main component that provides a simple interface for end-to-end testing of IR generation. It integrates with the existing IRBuilder and IRPrinter classes to:

1. Execute IR builder functions
2. Generate IR output using IRPrinter
3. Compare with expected IR from resource files

### IRResourceLoader

Utility class for loading expected IR resource files from the test resources directory. It handles loading files relative to the test class location.

### IRNormalizer

Utility class for normalizing LLVM IR strings to handle formatting differences, including:
- Whitespace differences (extra spaces, tabs, newlines)
- Comment variations
- Temporary name differences (%1, %2, etc.)
- Ordering of independent instructions

## How to Use the Framework

### Basic Test Structure

```kotlin
class YourTest {
    @Test
    fun testYourFunction() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testYourFunction",
            testClass = YourTest::class.java,
            resourceName = "YourTest_expected.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
                // Build your IR here
            }
        )
    }
}
```

### Key Parameters

- `testName`: Name of the test (used for error reporting)
- `testClass`: The test class used as a reference point for relative paths
- `resourceName`: The name of the resource file containing expected IR
- `setupModule`: Function to create and configure the test module
- `buildIR`: Function that builds IR using the provided module and builder
- `normalizeType`: Type of normalization to apply (default: NORMALIZE)

### Normalization Types

- `NONE`: No normalization - compare strings as-is
- `NORMALIZE`: Standard normalization (whitespace, comments, etc.)
- `FUNCTION`: Function-specific normalization
- `MODULE`: Module-level normalization

## How to Add New Test Cases

### 1. Create the Test Class

Create a new test class in the `src/test/kotlin/space/norb/llvm/e2e/` directory:

```kotlin
package space.norb.llvm.e2e

import space.norb.llvm.e2e.framework.IRTestFramework
import space.norb.llvm.structure.Module
import kotlin.test.Test

class YourNewTest {
    
    @Test
    fun testYourFeature() {
        IRTestFramework.testIRBuilderRelativeToClass(
            testName = "testYourFeature",
            testClass = YourNewTest::class.java,
            resourceName = "YourNewTest_feature.ll",
            setupModule = { Module("test_module") },
            buildIR = { module, builder ->
                // Build your IR here
            }
        )
    }
}
```

### 2. Create the Expected IR Resource

Create a corresponding `.ll` file in the `src/test/resources/space/norb/llvm/e2e/` directory with the same name as specified in the test:

```
src/test/resources/space/norb/llvm/e2e/YourNewTest_feature.ll
```

The resource file should contain the expected LLVM IR output:

```llvm
define i32 @your_function(i32 %arg0, i32 %arg1) {
entry:
  %result = add i32 %arg0, %arg1
  ret i32 %result
}
```

### 3. Naming Convention

Follow these naming conventions for consistency:

- Test classes: `[FeatureName]Test.kt`
- Resource files: `[ClassName]_[testName].ll`
- Test methods: `test[FeatureName]`

## Example: Simple Arithmetic Test

Here's a complete example from the existing `SimpleArithmeticTest.kt`:

```kotlin
@Test
fun testAddFunction() {
    IRTestFramework.testIRBuilderRelativeToClass(
        testName = "testAddFunction",
        testClass = SimpleArithmeticTest::class.java,
        resourceName = "SimpleArithmeticTest_add.ll",
        setupModule = { Module("test_module") },
        buildIR = { module, builder ->
            // Create function type: i32 (i32, i32)
            val functionType = FunctionType(
                returnType = IntegerType.I32,
                paramTypes = listOf(IntegerType.I32, IntegerType.I32)
            )
            
            // Create the function
            val function = builder.createFunction("add", functionType)
            module.functions.add(function)
            
            // Create entry block
            val entryBlock = builder.createBasicBlock("entry", function)
            function.basicBlocks.add(entryBlock)
            
            if (function.entryBlock == null) {
                function.entryBlock = entryBlock
            }
            
            builder.positionAtEnd(entryBlock)
            
            // Get function arguments
            val arg0 = function.parameters[0]
            val arg1 = function.parameters[1]
            
            // Create add instruction
            val result = builder.buildAdd(arg0, arg1, "result")
            
            // Return the result
            builder.buildRet(result)
        }
    )
}
```

With the corresponding resource file `SimpleArithmeticTest_add.ll`:

```llvm
define i32 @add(i32 %arg0, i32 %arg1) {
entry:
  %result = add i32 %arg0, %arg1
  ret i32 %result
}
```

## Best Practices

1. **Keep tests focused**: Each test should verify a specific IR generation feature
2. **Use descriptive names**: Make test names and resource names self-explanatory
3. **Organize by feature**: Group related tests in the same class
4. **Include edge cases**: Test boundary conditions and error scenarios
5. **Document complex tests**: Add comments for non-obvious IR constructions

## Running the Tests

Run the e2e tests using Gradle:

```bash
./gradlew test --tests "*e2e*"
```

Or run a specific test class:

```bash
./gradlew test --tests "space.norb.llvm.e2e.SimpleArithmeticTest"
```

## Troubleshooting

### Common Issues

1. **Resource not found**: Ensure the resource file is in the correct directory and the name matches exactly
2. **IR comparison failed**: Check for formatting differences or semantic errors in the generated IR
3. **Normalization issues**: Try different normalization types if the comparison is too strict

### Debug Tips

1. Use the detailed error output to identify differences
2. Check the actual vs. expected IR in the test failure message
3. Verify that the IR builder is constructing the correct structure
4. Ensure all necessary components (functions, basic blocks, etc.) are properly added to their parents