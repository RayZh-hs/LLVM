# Known Issues (TODO Tracker)

[x] User.getOperandIndex returns -1 for “not found”; that’s a classic sentinel and would be more idiomatic as Int? or a sibling like findOperandIndex(...) in src/main/kotlin/space/norb/llvm/core/User.kt:101.
[x] PhiNode.replaceIncomingValueForBlock has to special-case that -1 sentinel today, so it would simplify naturally if the lookup returned Int? in src/main/kotlin/space/norb/llvm/instructions/other/PhiNode.kt:215.
[x] PointerCastingUtils.createPointerBitcast returns Pair<Boolean, Type>, where the Type is only meaningful when the Boolean is true; that’s another sentinel-ish shape and would be cleaner as Type? or a sealed result in src/main/kotlin/space/norb/llvm/types/PointerCastingUtils.kt:41.