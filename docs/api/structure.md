# Module Structure Documentation

The module structure in Kotlin-LLVM provides the organizational framework for LLVM IR code.

## Overview

The module structure consists of:
- **Modules** - Top-level containers for code
- **Functions** - Executable code units
- **Basic Blocks** - Sequences of instructions
- **Arguments** - Function parameters
- **Global Variables** - Module-level data

## Modules

### Creating Modules
```kotlin
// Create a new module
val module = Module("myModule")

// Set target information
module.targetTriple = "x86_64-pc-linux-gnu"
module.dataLayout = "e-m:e-i64:64-f80:128-n8:16:32:64-S128"
```

### Module Properties
```kotlin
// Set target information
module.targetTriple = "x86_64-pc-linux-gnu"
module.dataLayout = "e-m:e-i64:64-f80:128-n8:16:32:64-S128"

// Convert to IR string
val irString = module.toIRString()
```

### Function Management
```kotlin
// Register function with type
val functionType = FunctionType(
    returnType = IntegerType.I32,
    paramTypes = listOf(IntegerType.I32, IntegerType.I32)
)
val function = module.registerFunction("add", functionType)

// Register function with return type and parameter types
val function2 = module.registerFunction(
    name = "multiply",
    returnType = IntegerType.I32,
    parameterTypes = listOf(IntegerType.I32, IntegerType.I32)
)
```

### Global Variable Management
```kotlin
// Register global variable (use factory helpers to create it)
val global = GlobalVariable.createWithElementType(
    name = "counter",
    elementType = IntegerType.I32,
    module = module
)
module.registerGlobalVariable(global)
```

### Struct Type Management
```kotlin
// Register named struct type
val personType = module.registerNamedStructType(
    name = "Person",
    elementTypes = listOf(IntegerType.I32, PointerType)
)

// Register opaque struct type
val opaqueType = module.registerOpaqueStructType("OpaqueStruct")

// Complete opaque struct later
module.completeOpaqueStructType(
    name = "OpaqueStruct",
    elementTypes = listOf(IntegerType.I64, PointerType)
)

// Check if struct type exists
val hasPerson = module.hasNamedStructType("Person")

// Get named struct type
val retrievedType = module.getNamedStructType("Person")

// Get all struct types
val allNamedTypes = module.getAllNamedStructTypes()
val allAnonymousTypes = module.getAllAnonymousStructTypes()
val allStructTypes = module.getAllStructTypes()
```

## Functions

### Creating Functions
```kotlin
// Preferred: let the module construct and register the function
val functionType = FunctionType(
    returnType = IntegerType.I32,
    paramTypes = listOf(IntegerType.I32, IntegerType.I32),
    paramNames = listOf("a", "b")
)
val function = module.registerFunction("add", functionType)

// Manual construction is allowed, but remember to record the function on the module
val helper = Function("helper", functionType, module)
module.functions.add(helper)
```

### Function Properties
```kotlin
// Access function properties
val name = function.name
val type = function.type
val returnType = function.returnType
val parameters = function.parameters
val basicBlocks = function.basicBlocks
val entryBlock = function.entryBlock
val parentModule = function.module
```

### Function Parameters
```kotlin
// Access parameters
val param1 = function.parameters[0]
val param2 = function.parameters[1]

// Parameter properties
val paramName = param1.name
val paramType = param1.type
val paramIndex = param1.index
val parentFunction = param1.function
```

### Basic Block Management
```kotlin
// Insert basic block
val entryBlock = function.insertBasicBlock("entry")
val thenBlock = function.insertBasicBlock("then")
val elseBlock = function.insertBasicBlock("else")

// Set single basic block
function.setBasicBlock(entryBlock)

// Set multiple basic blocks
function.setBasicBlocks(listOf(entryBlock, thenBlock, elseBlock))
```

## Basic Blocks

### Creating Basic Blocks
```kotlin
// Recommended helper – the function keeps track of ownership and entry status
val entryBlock = function.insertBasicBlock("entry", setAsEntrypoint = true)

// Manual allocation is possible, but add the block to the function yourself
val extraBlock = BasicBlock("extra", function)
function.basicBlocks.add(extraBlock)
```

### Basic Block Properties
```kotlin
// Access basic block properties
val name = basicBlock.name
val type = basicBlock.type // Always LabelType
val parentFunction = basicBlock.function
val instructions = basicBlock.instructions
val terminator = basicBlock.terminator
```

### Instruction Management
```kotlin
// Access instructions
val instructionList = basicBlock.instructions
val firstInstruction = basicBlock.instructions.firstOrNull()
val lastInstruction = basicBlock.instructions.lastOrNull()

// Check if has terminator
val hasTerminator = basicBlock.terminator != null
```

## Arguments

### Working with Arguments
```kotlin
// Access function arguments
val args = function.parameters

// Get argument by index
val arg = function.parameters[0]

// Argument properties
val argName = arg.name
val argType = arg.type
val argIndex = arg.index
val parentFunction = arg.function
```

### Parameter Names
```kotlin
// Function type with parameter names
val functionType = FunctionType(
    returnType = IntegerType.I32,
    paramTypes = listOf(IntegerType.I32, IntegerType.I32),
    paramNames = listOf("a", "b")
)

// Get parameter name from type
val paramName = functionType.getParameterName(0) // Returns "a"
val paramName2 = functionType.getParameterName(1) // Returns "b"
val defaultName = functionType.getParameterName(2) // Returns "arg2"
```

## Global Variables

### Creating Global Variables
```kotlin
// Create global variable
val global = GlobalVariable("counter", IntegerType.I32)

// Register with module
module.registerGlobalVariable(global)
```

### Global Variable Properties
```kotlin
// Access global variable properties
val name = global.name
val type = global.type
```

## Module Organization

### Struct Type Organization
```kotlin
// Named struct types for organization
val personType = module.registerNamedStructType(
    name = "Person",
    elementTypes = listOf(
        IntegerType.I32,    // age
        PointerType,        // name
        FloatingPointType.FloatType  // height
    )
)

val addressType = module.registerNamedStructType(
    name = "Address",
    elementTypes = listOf(
        PointerType,        // street
        PointerType,        // city
        IntegerType.I32     // zip
    )
)

// Anonymous struct types for one-off use
val pointType = module.getOrCreateAnonymousStructType(
    elementTypes = listOf(
        FloatingPointType.FloatType,  // x
        FloatingPointType.FloatType   // y
    )
)
```

### Function Organization
```kotlin
// Use naming conventions for organization
val mathAdd = module.registerFunction(
    "math.add",
    FunctionType(IntegerType.I32, listOf(IntegerType.I32, IntegerType.I32))
)

val stringLength = module.registerFunction(
    "string.length",
    FunctionType(IntegerType.I32, listOf(PointerType))
)

// Group related functionality
val vectorOps = listOf(
    module.registerFunction("vector.add", vectorAddType),
    module.registerFunction("vector.sub", vectorSubType),
    module.registerFunction("vector.mul", vectorMulType),
    module.registerFunction("vector.div", vectorDivType)
)
```

### Module Dependencies
```kotlin
// External function declarations
val malloc = module.registerFunction(
    name = "malloc",
    type = FunctionType(PointerType, listOf(IntegerType.I64))
)

val free = module.registerFunction(
    name = "free",
    type = FunctionType(VoidType, listOf(PointerType))
)

val printf = module.registerFunction(
    name = "printf",
    type = FunctionType(IntegerType.I32, listOf(PointerType), isVarArg = true)
)
```

## Type Management in Modules

### Struct Type Deduplication
```kotlin
// Anonymous struct types are deduplicated
val struct1 = module.getOrCreateAnonymousStructType(
    elementTypes = listOf(IntegerType.I32, IntegerType.I64)
)

val struct2 = module.getOrCreateAnonymousStructType(
    elementTypes = listOf(IntegerType.I32, IntegerType.I64)
)

// struct1 === struct2 (same instance due to deduplication)
```

### Named Struct Type Lifecycle
```kotlin
// 1. Create opaque struct
val opaque = module.registerOpaqueStructType("LinkedList")

// 2. Use in function signatures before definition
val nodePtrType = PointerType
val listType = FunctionType(VoidType, listOf(nodePtrType))
val insertFunc = module.registerFunction("LinkedList.insert", listType)

// 3. Complete definition later
module.completeOpaqueStructType(
    name = "LinkedList",
    elementTypes = listOf(
        IntegerType.I32,    // value
        PointerType          // next
    )
)
```

## Best Practices

1. **Use descriptive names** for all entities
2. **Organize functions** into logical groups with prefixes
3. **Use named struct types** for reusable data structures
4. **Use anonymous struct types** for one-off structures
5. **Register all types** in modules for proper organization
6. **Complete opaque structs** before using them in complex ways
7. **Use consistent naming conventions** across your module
8. **Document module structure** with comments
9. **Minimize global variables** when possible
10. **Group related functionality** together