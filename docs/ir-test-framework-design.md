# IR Test Framework Design

## Overview

This document outlines the design of a comprehensive test framework for comparing generated LLVM IR with expected IR strings. The framework is designed to provide end-to-end testing of the IR generation capacity while keeping the existing unit tests intact.

## Design Goals

1. **Non-intrusive**: Add new e2e tests without modifying existing unit tests
2. **Simple**: Keep the test suite design simple and maintainable
3. **Comprehensive**: Cover all major IR constructs and their combinations
4. **Robust**: Handle formatting differences and provide clear error reporting
5. **Extensible**: Easy to add new test cases and categories

## Architecture

### Core Components

The test framework consists of the following core components:
- IRTestbench: Loads resource (kt function + expected IR string) and runs a kotlin function that invokes an IR Builder, then compares the generated IR with expected IR.
- IRResourceLoader: Loads expected data resource files. Used by IRTestbench.
- IRNormalizer: Normalizes IR strings to handle formatting differences. Used by IRTestbench.

### File Organization

```
src/test/kotlin/space/norb/llvm/e2e/
└── framework/
    ├── IRTestFramework.kt
    ├── IRNormalizer.kt
    └── IRResourceLoader.kt
```

Match each kotlin declaration file with a corresponding llvm-ir expected output file. Keep them organized in directories.
