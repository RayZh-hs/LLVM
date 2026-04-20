# Use-Def Analysis

The LLVM Use-Def Chain allows us to track where values are used and defined in the ir. In this implementation, we will use `Analysis` to perform use-def analysis, instead of baking the analysis into `Value`.

Specifically, we implement a `UseDefAnalysis` class that provides a `UseDefChain` data structure that maps each `Value` to use and defs. The `UseDefChain` object is non-mutable.
