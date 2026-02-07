package space.norb.llvm.analysis

import kotlin.reflect.KClass
import space.norb.llvm.structure.Module

/**
 * An Analysis is a computation that extracts information from the IR tree.
 *
 * Analyses should be pure functions of the IR tree, and should not modify the IR tree.
 */
abstract class Analysis<Result : AnalysisResult> {
    // Unique key to identify this analysis (using class itself)
    open val key: KClass<out Analysis<Result>> = this::class
    abstract fun compute(module: Module, am: AnalysisManager): Result
}
