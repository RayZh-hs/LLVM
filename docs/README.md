# Kotlin-LLVM Documentation

Welcome to the Kotlin-LLVM documentation. This project provides a Kotlin library for programmatically generating LLVM IR (Intermediate Representation) code.

## Documentation Structure

- **[API Documentation](api/)** - Detailed API reference for all components
- **[Examples](examples/)** - Code examples and tutorials
- **[Guides](guides/)** - User guides and step-by-step tutorials
- **[Architecture](architecture/)** - Architectural documentation and design decisions

## Quick Links

- [Getting Started](guides/getting-started.md)
- [Quick Start Example](examples/quick-start.md)
- [API Overview](api/README.md)

## Project Overview

Kotlin-LLVM is a Kotlin codebase that offers type-safe building blocks for constructing LLVM IR. It combines LLVM's optimization pipeline with strongly typed Kotlin APIs.

### Key Features

- Type-safe wrappers for LLVM core concepts (modules, functions, blocks, values)
- Explicit support for the implemented instruction set (arithmetic, comparisons, casts, memory, control flow)
- Utilities for the un-typed pointer model used by modern LLVM IR
- IR printer for generating textual LLVM IR from Kotlin data structures

## Getting Help

- Check the [guides](guides/) for step-by-step tutorials
- Browse the [examples](examples/) for practical implementations
- Refer to the [API documentation](api/) for detailed reference
- Read the [architecture documentation](architecture/) for design insights