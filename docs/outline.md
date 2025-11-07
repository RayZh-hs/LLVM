This document outlines a comprehensive design for an LLVM IR generation system in Kotlin.

### LLVM IR Compliance Status

**✅ Migration Completed:** This implementation now follows the **latest LLVM IR untyped pointer model** and complies with the current LLVM IR standard.

- **Current Implementation:** Untyped pointers where all pointers are simply `ptr` regardless of pointee type
- **Migration Status:** Migration from typed pointers to untyped pointers has been completed
- **Compatibility:** Generated LLVM IR is compatible with modern LLVM toolchains

### Core Design Philosophy

The design is centered around a hierarchical and compositional structure that mirrors the LLVM IR itself. The core principles are:

*   **Strong Typing:** Leveraging Kotlin's type system to represent LLVM's types, preventing errors during IR construction.
*   **Immutability:** Once constructed, IR objects (like Instructions and Functions) should be largely immutable to ensure consistency during subsequent processing, such as optimization or code generation. The `IRBuilder` will manage the mutable construction phase.
*   **Clear Ownership:** The hierarchy establishes unambiguous ownership: `Module` contains `Function`s, which contain `BasicBlock`s, which in turn contain `Instruction`s.
*   **Extensibility:** Using `sealed class` and `interface` hierarchies allows for the easy addition of new instructions, types, or values without modifying the core builder logic.
*   **Builder Pattern:** A dedicated `IRBuilder` class will provide a fluent and intuitive API for constructing the IR, managing the insertion point, and handling the details of object creation.

---

### I. The `Value` and `Type` System: The Foundation

Everything in LLVM that can be used as an operand is a `Value`. Every `Value` has a `Type`. This forms the base of our class hierarchy.

#### 1. The `Type` Hierarchy

A `sealed class` is ideal for representing the fixed set of primary LLVM types. This enables exhaustive `when` checks in the compiler.

```
sealed class Type

// Primitive Types
object VoidType : Type()
object LabelType : Type()
object MetadataType : Type()
data class IntegerType(val bitWidth: Int) : Type()
sealed class FloatingPointType : Type() {
    object FloatType : FloatingPointType()
    object DoubleType : FloatingPointType()
}

// Derived Types (Current Untyped Pointer Model)
data class PointerType(val addressSpace: Int = 0) : Type()  // ✅ Current: Untyped pointers
data class FunctionType(val returnType: Type, val paramTypes: List<Type>, val isVarArg: Boolean = false) : Type()
data class ArrayType(val numElements: Int, val elementType: Type) : Type()
data class StructType(val elementTypes: List<Type>, val isPacked: Boolean = false) : Type()

// Note: The PointerType implementation now follows the current LLVM IR model
// with untyped pointers (simply "ptr") regardless of the pointee type.
// Migration from typed pointers has been completed.
```

#### 2. The `Value` Hierarchy

This hierarchy represents all entities that can be computed or referenced.

`interface Value`
*   **Properties:** `name: String`, `type: Type`
*   **Description:** The root of the value hierarchy. Any class implementing `Value` can be an operand for an instruction.

`abstract class User : Value`
*   **Properties:** `operands: List<Value>`
*   **Description:** A `Value` that uses other `Value`s. This is the base for instructions and complex constants.

`abstract class Constant : Value`
*   **Description:** A `Value` that is constant at compile time.
*   **Concrete Subclasses:**
    *   `IntConstant(val value: Long, override val type: IntegerType)`
    *   `FloatConstant(val value: Double, override val type: FloatingPointType)`
    *   `NullPointerConstant(override val type: PointerType)`
    *   `GlobalVariable(...)` (see below)

---

### II. Structural Components of the IR

These classes define the high-level structure of a program.

#### 1. `Module`

The top-level container for an entire compilation unit.

`class Module(val name: String)`
*   **Properties:**
    *   `functions: MutableList<Function>`
    *   `globalVariables: MutableList<GlobalVariable>`
    *   `namedMetadata: MutableMap<String, Metadata>`
    *   `targetTriple: String?`
    *   `dataLayout: String?`
*   **Description:** Holds all functions, global variables, and module-level metadata required for compilation and linking.

#### 2. `GlobalVariable`

Represents a variable at the global scope.

`class GlobalVariable(...) : Constant`
*   **Properties:**
    *   `module: Module` (parent)
    *   `initializer: Constant?`
    *   `isConstant: Boolean`
    *   `linkage: LinkageType` (Enum: `PUBLIC`, `PRIVATE`, etc.)
*   **Description:** A pointer to a statically allocated memory location.

#### 3. `Function`

A single, callable unit of code.

`class Function(...) : Value`
*   **Properties:**
    *   `module: Module` (parent)
    *   `returnType: Type`
    *   `parameters: List<Argument>`
    *   `basicBlocks: MutableList<BasicBlock>`
    *   `entryBlock: BasicBlock?`
*   **Description:** Contains a list of parameters and a body composed of `BasicBlock`s.

#### 4. `Argument`

Represents a parameter passed to a function.

`class Argument(...) : Value`
*   **Properties:**
    *   `function: Function` (parent)
    *   `index: Int`
*   **Description:** A placeholder for a value that will be provided when the function is called.

#### 5. `BasicBlock`

A sequence of instructions with a single entry point and a single exit point.

`class BasicBlock(override val name: String, val function: Function)`
*   **Properties:**
    *   `instructions: MutableList<Instruction>`
    *   `terminator: TerminatorInst?`
*   **Description:** The fundamental building block of the Control Flow Graph (CFG). All basic blocks must end with a "terminator" instruction.

---

### III. The `Instruction` Hierarchy

Instructions perform the actual work of the program. They are `Value`s themselves, meaning the result of one instruction can be the operand of another (the essence of SSA form).

`abstract class Instruction(override val name: String, ...) : User`
*   **Properties:** `parent: BasicBlock`

#### Inheritance Strategy: Sealed Classes for Categories

This design promotes organization and type safety.

**1. `TerminatorInst` (Sealed Class)**
*   **Description:** Instructions that terminate a basic block.
*   **Subclasses:**
    *   `ReturnInst(val returnValue: Value?)`
    *   `BranchInst(val condition: Value?, val trueTarget: BasicBlock, val falseTarget: BasicBlock?)`
    *   `SwitchInst(...)`

**2. `BinaryInst` (Sealed Class)**
*   **Description:** Standard two-operand arithmetic and bitwise operations.
*   **Subclasses:**
    *   `AddInst(val lhs: Value, val rhs: Value)`
    *   `SubInst(val lhs: Value, val rhs: Value)`
    *   `MulInst(...)`
    *   `SDivInst(...)`
    *   `AndInst(...)`, `OrInst(...)`, `XorInst(...)`

**3. `MemoryInst` (Sealed Class)**
*   **Description:** Instructions for memory access.
*   **Subclasses:**
    *   `AllocaInst(val allocatedType: Type)`: Allocates memory on the stack.
    *   `LoadInst(val address: Value)`: Reads from memory.
    *   `StoreInst(val value: Value, val address: Value)`
    *   `GetElementPtrInst(val address: Value, val indices: List<Value>)`: Performs pointer arithmetic.

**4. `CastInst` (Sealed Class)**
*   **Description:** Instructions for converting between types.
*   **Subclasses:**
    *   `TruncInst(val value: Value, val targetType: IntegerType)`
    *   `ZExtInst(val value: Value, val targetType: IntegerType)` (Zero Extend)
    *   `SExtInst(...)` (Sign Extend)
    *   `BitcastInst(...)`

**5. `OtherInst` (Sealed Class)**
*   **Description:** A category for other common instructions.
*   **Subclasses:**
    *   `CallInst(val function: Value, val args: List<Value>)`
    *   `ICmpInst(val predicate: IcmpPredicate, val lhs: Value, val rhs: Value)` (Enum `IcmpPredicate`: `EQ`, `NE`, `SGT`, `SLT`, etc.)
    *   `PhiNode(val incoming: MutableMap<BasicBlock, Value>)`

---

### IV. The `IRBuilder` Class

This is the primary user-facing class for constructing the IR. It simplifies the process by managing the current insertion point.

`class IRBuilder(val module: Module)`
*   **Properties:**
    *   `private var currentBlock: BasicBlock?`
    *   `private var insertionPoint: MutableListIterator<Instruction>?`
*   **Public Methods (Positioning):**
    *   `positionAtEnd(block: BasicBlock)`
    *   `positionBefore(instruction: Instruction)`
    *   `clearInsertionPoint()`
*   **Public Methods (IR Construction):**
    *   **Functions & Blocks:**
        *   `createFunction(name: String, type: FunctionType): Function`
        *   `createBasicBlock(name: String, function: Function): BasicBlock`
    *   **Terminators:**
        *   `insertRet(value: Value?)`
        *   `insertBr(target: BasicBlock)`
        *   `insertCondBr(condition: Value, trueTarget: BasicBlock, falseTarget: BasicBlock)`
    *   **Binary Ops:**
        *   `insertAdd(lhs: Value, rhs: Value, name: String = ""): Value`
        *   `insertSub(lhs: Value, rhs: Value, name: String = ""): Value`
        *   `insertMul(...)`
    *   **Memory Ops:**
        *   `insertAlloca(type: Type, name: String = ""): Value`
        *   `insertLoad(address: Value, name: String = ""): Value`
        *   `insertStore(value: Value, address: Value)`
        *   `insertGep(address: Value, indices: List<Value>, name: String = ""): Value`
    *   **Other Ops:**
        *   `insertCall(function: Function, args: List<Value>, name: String = ""): Value`
        *   `insertICmp(pred: IcmpPredicate, lhs: Value, rhs: Value, name: String = ""): Value`

---

### V. Scalability and Tooling

To make this system production-ready, consider the following additions.

#### 1. `Visitor` Pattern

A visitor pattern is crucial for implementing analyses, transformations, and pretty-printing without cluttering the IR classes themselves.

```kotlin
interface IRVisitor<T> {
    fun visitModule(module: Module): T
    fun visitFunction(function: Function): T
    fun visitBasicBlock(block: BasicBlock): T

    // Overloads for each instruction type
    fun visitReturnInst(inst: ReturnInst): T
    fun visitAddInst(inst: AddInst): T
    // ... etc.
}
```

#### 2. `IRPrinter`

An implementation of the `IRVisitor` that traverses the `Module` and generates a human-readable `.ll` file representation.

`class IRPrinter : IRVisitor<Unit>`
*   **Functionality:** Implements each `visit` method to append the correct LLVM assembly representation to a `StringBuilder`. This class will manage indentation and the proper formatting of types, names, and operands.

This blueprint provides a solid foundation for building a robust and scalable LLVM IR generation system in Kotlin. It balances simplicity with the structural integrity required for a production-ready compiler toolchain.