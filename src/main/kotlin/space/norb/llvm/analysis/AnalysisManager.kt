package space.norb.llvm.analysis

import space.norb.llvm.structure.Module
import kotlin.reflect.KClass

/**
 * The AnalysisManager is responsible for managing the lifecycle of analyses.
 *
 * The AnalysisManager provides a consistent, centralized way to access analysis results and to reuse them across multiple passes.
 *
 * @see Analysis
 * @see AnalysisResult
 */
class AnalysisManager(private val module: Module) {
    // Analysis Class -> Analysis Result
    private val cache = mutableMapOf<KClass<*>, AnalysisResult>()

    // Stack to detect circular dependencies
    private val computingStack = mutableSetOf<KClass<*>>()

    // Registry of available analyses
    private val registeredAnalyses = mutableMapOf<KClass<*>, Analysis<*>>()

    fun <R : AnalysisResult> register(analysis: Analysis<R>) {
        registeredAnalyses[analysis.key] = analysis
    }

    @Suppress("UNCHECKED_CAST")
    fun <R : AnalysisResult> get(analysisKey: KClass<out Analysis<R>>): R {
        // 1. Check cache
        if (analysisKey in cache) {
            return cache[analysisKey] as R
        }

        // 2. Circular dependency check
        if (analysisKey in computingStack) {
            throw IllegalStateException("Circular analysis dependency detected: ${analysisKey.simpleName}")
        }

        // 3. Compute and cache
        computingStack.add(analysisKey)
        try {
            val provider = registeredAnalyses[analysisKey]
                ?: throw IllegalStateException("Analysis ${analysisKey.simpleName} not registered.")
            val result = provider.compute(module, this)
            cache[analysisKey] = result
            return result as R
        } finally {
            computingStack.remove(analysisKey)
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <R : AnalysisResult> set(analysisKey: KClass<out Analysis<R>>, result: R) {
        cache[analysisKey] = result
    }

    /**
     * Called by a Pass to indicate that all analysis results are still valid.
     */
    fun invalidateNone() = Unit

    /**
     * Called by a Pass to indicate that all metadata is invalid and should be recomputed by the next pass that needs it.
     */
    fun invalidateAll() {
        cache.clear()
    }

    /**
     * Called by a Pass to indicate what metadata is still valid.
     * Everything NOT in [preserved] is dropped from the cache.
     */
    fun invalidateAllExcept(vararg preserved: KClass<*>) {
        val keysToRemove = cache.keys - preserved.toSet()
        keysToRemove.forEach { cache.remove(it) }
    }

    /**
     * Called by a Pass to indicate that the given metadata is invalid and should be recomputed by the next pass that needs it.
     */
    fun invalidateAllIn(vararg keysToRemove: KClass<*>) {
        keysToRemove.forEach { cache.remove(it) }
    }
}