package space.norb.llvm.e2e.framework

import space.norb.llvm.structure.Module
import space.norb.llvm.visitors.IRPrinter
import kotlin.test.fail

/**
 * Main test framework for comparing generated LLVM IR with expected IR strings.
 * 
 * This component provides a simple interface for end-to-end testing of IR generation.
 * It integrates with the existing IRBuilder and IRPrinter classes to:
 * 1. Execute IR builder functions
 * 2. Generate IR output using IRPrinter
 * 3. Compare with expected IR from resource files
 * 
 * Usage example:
 * ```
 * IRTestFramework.testIRBuilder(
 *     testName = "simple_function",
 *     resourcePath = "test_data/simple_function.ll",
 *     setupModule = { Module("test") },
 *     buildIR = { module, builder ->
 *         // Build IR using the builder
 *     }
 * )
 * ```
 */
object IRTestFramework {
    
    /**
     * Runs an IR builder test and compares the generated IR with expected IR from a resource file.
     * 
     * @param testName The name of the test (used for error reporting)
     * @param resourcePath Path to the resource file containing expected IR
     * @param setupModule Function to create and configure the test module
     * @param buildIR Function that builds IR using the provided module and builder
     * @param normalizeType Type of normalization to apply (default: NORMALIZE)
     */
    fun testIRBuilder(
        testName: String,
        resourcePath: String,
        setupModule: () -> Module,
        buildIR: (Module, space.norb.llvm.builder.IRBuilder) -> Unit,
        normalizeType: NormalizeType = NormalizeType.NORMALIZE
    ) {
        try {
            // Load expected IR from resource
            val expectedIR = IRResourceLoader.loadIRResource(resourcePath)
            
            // Create module and builder
            val module = setupModule()
            val builder = space.norb.llvm.builder.IRBuilder(module)
            
            // Build IR using the provided function
            buildIR(module, builder)
            
            // Generate IR output
            val printer = IRPrinter()
            val actualIR = printer.print(module)
            
            // Compare IR strings with appropriate normalization
            val comparisonResult = compareIR(actualIR, expectedIR, normalizeType)
            
            if (!comparisonResult.matches) {
                fail(
                    buildString {
                        appendLine("IR comparison failed for test: $testName")
                        appendLine("Resource: $resourcePath")
                        appendLine()
                        appendLine("Expected IR:")
                        appendLine(comparisonResult.normalizedExpected)
                        appendLine()
                        appendLine("Actual IR:")
                        appendLine(comparisonResult.normalizedActual)
                        appendLine()
                        appendLine("Diff:")
                        appendLine(generateDiff(comparisonResult.normalizedExpected, comparisonResult.normalizedActual))
                    }
                )
            }
        } catch (e: Exception) {
            fail("Test '$testName' failed with exception: ${e.message}", e)
        }
    }
    
    /**
     * Runs an IR builder test with the resource path relative to a test class.
     * 
     * @param testName The name of the test (used for error reporting)
     * @param testClass The test class used as a reference point for relative paths
     * @param resourceName The name of the resource file containing expected IR
     * @param setupModule Function to create and configure the test module
     * @param buildIR Function that builds IR using the provided module and builder
     * @param normalizeType Type of normalization to apply (default: NORMALIZE)
     */
    fun testIRBuilderRelativeToClass(
        testName: String,
        testClass: Class<*>,
        resourceName: String,
        setupModule: () -> Module,
        buildIR: (Module, space.norb.llvm.builder.IRBuilder) -> Unit,
        normalizeType: NormalizeType = NormalizeType.NORMALIZE
    ) {
        val resourcePath = "${testClass.packageName.replace('.', '/')}/$resourceName"
        testIRBuilder(testName, resourcePath, setupModule, buildIR, normalizeType)
    }
    
    /**
     * Compares two IR strings with the specified normalization.
     * 
     * @param actual The actual IR string
     * @param expected The expected IR string
     * @param normalizeType Type of normalization to apply
     * @return ComparisonResult containing the comparison outcome and normalized strings
     */
    private fun compareIR(
        actual: String,
        expected: String,
        normalizeType: NormalizeType
    ): ComparisonResult {
        val normalizedActual = when (normalizeType) {
            NormalizeType.NONE -> actual
            NormalizeType.NORMALIZE -> IRNormalizer.normalize(actual)
            NormalizeType.FUNCTION -> IRNormalizer.normalizeFunction(actual)
            NormalizeType.MODULE -> IRNormalizer.normalizeModule(actual)
        }
        
        val normalizedExpected = when (normalizeType) {
            NormalizeType.NONE -> expected
            NormalizeType.NORMALIZE -> IRNormalizer.normalize(expected)
            NormalizeType.FUNCTION -> IRNormalizer.normalizeFunction(expected)
            NormalizeType.MODULE -> IRNormalizer.normalizeModule(expected)
        }
        
        return ComparisonResult(
            matches = normalizedActual == normalizedExpected,
            normalizedActual = normalizedActual,
            normalizedExpected = normalizedExpected
        )
    }
    
    /**
     * Generates a simple diff between two strings.
     */
    private fun generateDiff(expected: String, actual: String): String {
        val expectedLines = expected.split('\n')
        val actualLines = actual.split('\n')
        
        val diff = StringBuilder()
        val maxLines = maxOf(expectedLines.size, actualLines.size)
        
        for (i in 0 until maxLines) {
            val expectedLine = expectedLines.getOrNull(i) ?: "<missing>"
            val actualLine = actualLines.getOrNull(i) ?: "<missing>"
            
            if (expectedLine != actualLine) {
                diff.appendLine("- $expectedLine")
                diff.appendLine("+ $actualLine")
            }
        }
        
        return diff.toString()
    }
    
    /**
     * Types of normalization that can be applied to IR strings.
     */
    enum class NormalizeType {
        /** No normalization - compare strings as-is */
        NONE,
        /** Standard normalization (whitespace, comments, etc.) */
        NORMALIZE,
        /** Function-specific normalization */
        FUNCTION,
        /** Module-level normalization */
        MODULE
    }
    
    /**
     * Result of an IR comparison.
     */
    private data class ComparisonResult(
        val matches: Boolean,
        val normalizedActual: String,
        val normalizedExpected: String
    )
}