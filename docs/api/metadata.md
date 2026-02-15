# Metadata

Metadata in LLVM IR is used to store additional information about the program that doesn't affect execution but is useful for debugging, optimization, and other tools.

## Core Metadata Classes

Metadata values are located in `space.norb.llvm.values`. All metadata classes inherit from the base `Metadata` class.

### MDString
Represents a simple string metadata.
```kotlin
val mdString = MDString("debug_info")
// Generated IR: !"debug_info"
```

### MDNode
Represents a complex metadata node that contains other metadata as operands.
```kotlin
val mdNode = MDNode(listOf(MDString("flag1"), MDString("flag2")))
// Generated IR: !{!"flag1", !"flag2"}

val distinctNode = MDNode(listOf(MDString("id")), distinct = true)
// Generated IR: !distinct {!"id"}
```

### ConstantAsMetadata
Wraps a regular constant as metadata.
```kotlin
val intConst = IntConstant(42, IntegerType.I32)
val mdConst = ConstantAsMetadata(intConst)
// Generated IR: i32 42
```

## Attaching Metadata

Several IR entities implement the `MetadataCapable` interface, allowing you to attach metadata to them using `setMetadata(kind: String, metadata: Metadata)`.

### Instructions
```kotlin
val add = builder.insertAdd(val1, val2, "sum")
add.setMetadata("dbg", MDString("location1"))
```

### Functions
```kotlin
val func = module.registerFunction("main", funcType)
func.setMetadata("custom.attr", MDNode(listOf(MDString("profile"))))
```

### Global Variables
```kotlin
val global = module.registerGlobalVariable("my_global", initializer)
global.setMetadata("tbaa", MDString("scalar"))
```

## Named Metadata

Modules can store named metadata, which often contains global information like module flags or debug info anchors.

```kotlin
val module = Module("my_module")
module.namedMetadata["llvm.module.flags"] = MDNode(listOf(MDString("flag1")))
// Generated IR at end of module: !llvm.module.flags = !{!"flag1"}
```

## Using IRBuilder for Metadata

The `IRBuilder` provides convenience methods to manage metadata for all subsequently created instructions.

### Default Metadata
You can set a default metadata that will be automatically attached to every instruction created by the builder.

```kotlin
val builder = IRBuilder(module)
builder.setDefaultMetadata("tbaa", myTbaaNode)

// All instructions created now will have !tbaa attached
val load = builder.insertLoad(i32, ptr) 
```

### Debug Locations
A common use case is setting the current debug location.

```kotlin
builder.setCurrentDebugLocation(MDString("source.kt:10"))
val add = builder.insertAdd(a, b)
// Generated IR: %add = add i32 %a, %b, !dbg !"source.kt:10"
```
