# Architecture Overview

This document describes the components that currently exist in the Kotlin-LLVM codebase and how they fit together. The focus is on the code found in `src/main/kotlin` rather than aspirational features.

## High-Level Layout

The implementation is organised into a set of Kotlin packages:

- `space.norb.llvm.core` – fundamental abstractions (`Type`, `Value`, `Constant`)
- `space.norb.llvm.types` – primitive and derived type definitions plus helpers
- `space.norb.llvm.structure` – structural objects (`Module`, `Function`, `BasicBlock`, `Argument`)
- `space.norb.llvm.instructions` – instruction hierarchy (binary, memory, cast, other, terminators)
- `space.norb.llvm.builder` – `IRBuilder` and `BuilderUtils` helpers for assembling IR
- `space.norb.llvm.values` – concrete value implementations (constants, globals, metadata)
- `space.norb.llvm.visitors` – utilities that walk the IR (`IRPrinter`, `IRValidator`)

These packages map directly to the classes referenced throughout the documentation and tests.

## Type System

Primitive and derived types live under `types/`:

- `PrimitiveTypes.kt` exposes `VoidType`, `LabelType`, `MetadataType`, and `IntegerType`/`FloatingPointType` specialisations.
- `DerivedTypes.kt` defines `PointerType`, `FunctionType`, `ArrayType`, and the `StructType` hierarchy.
- `TypeUtils.kt` provides small predicates and utilities (first-class checks, bit widths, pointer helpers).

All pointers use LLVM’s modern un-typed pointer model: there is a single `PointerType` object, so the pointee type is not encoded in the pointer itself. Functions that need element type information (e.g., `GetElementPtrInst`) receive it explicitly.

## Structural Layer

- `Module` collects functions, global variables, metadata, and struct definitions. It exposes helpers such as `registerFunction`, `registerNamedStructType`, `getOrCreateAnonymousStructType`, and `toIRString()`.
- `Function` owns `Argument` instances and `BasicBlock`s. `insertBasicBlock` is the primary factory for new blocks.
- `BasicBlock` keeps an ordered list of instructions and an optional terminator.

Modules enforce deduplication of anonymous struct types and ensure opaque/named struct handling matches the functions in `StructType.NamedStructType`.

## Instruction Set

Instructions are split by category inside `instructions/`:

- `binary/` contains `AddInst`, `SubInst`, `MulInst`, `SDivInst`, `AndInst`, `OrInst`, `XorInst`.
- `memory/` covers `AllocaInst`, `LoadInst`, `StoreInst`, `GetElementPtrInst`.
- `casts/` implements `TruncInst`, `ZExtInst`, `SExtInst`, `BitcastInst`.
- `other/` currently includes `CallInst`, `ICmpInst`, `PhiNode`.
- `terminators/` exposes `ReturnInst`, `BranchInst`, `SwitchInst`.

Every instruction inherits from the base classes in `instructions/base` (`Instruction`, `BinaryInst`, `MemoryInst`, etc.) and participates in the visitor interfaces consumed by the printer and validator.

## Building IR

`IRBuilder` orchestrates instruction construction. It is initialised with a `Module` and maintains an explicit insertion point. Clients move the insertion point with `positionAtEnd`/`positionBefore` and create instructions via methods such as `insertAdd`, `insertICmp`, `insertAlloca`, `insertRet`, and `insertCall`.

`BuilderUtils` supplements the builder with value and type helpers (integer constants, float constants, null pointers, zero/one/all-ones factories, type predicates, and bitcast/common-type utilities).

## Values

Values are implemented under `values/`:

- `values/constants` provides numeric constants, null pointers, and other literal values.
- `values/globals` contains `GlobalVariable`, which always exposes the un-typed `PointerType` while keeping the element type for context.
- `values/Metadata.kt` represents named metadata attachments.

All value classes extend `Value` (or `Constant`) so they can participate in instruction operands uniformly.

## Visitors and Output

- `IRPrinter` walks a module and emits textual LLVM IR. It understands the un-typed pointer model and formats functions, blocks, globals, and instructions accordingly.
- `IRValidator` performs structural checks used by the test suite (correct terminators, operand counts, etc.).

The printer is what powers `Module.toIRString()`.

## Data Flow When Building IR

1. Create a `Module` and register `Function`s within it.
2. Use `Function.insertBasicBlock` to create blocks and an `IRBuilder` to position within them.
3. Emit instructions with the builder, supplying explicit types where required (arrays, struct definitions, GEP element types, etc.).
4. Print the resulting module to LLVM IR using `module.toIRString()`.

## Current Limitations

- Only the instructions backed by concrete classes in `instructions/` are supported; there is no general instruction plugin system.
- Pointer element types are not preserved on the `PointerType` itself (by design for LLVM 15+), so code that needs layout information must carry the element type separately.
- The project does not yet expose JIT compilation, optimisation passes, or a Kotlin DSL layer – examples and tests construct IR via the classes described above.
