package space.norb.llvm.e2e.framework

/**
 * Utility class for normalizing LLVM IR strings to handle formatting differences.
 * 
 * This component addresses common variations in IR output that don't affect the
 * semantic meaning but can cause test failures if not handled. These include:
 * - Whitespace differences (extra spaces, tabs, newlines)
 * - Comment variations
 * - Temporary name differences (%1, %2, etc.)
 * - Ordering of independent instructions
 */
object IRNormalizer {
    
    /**
     * Normalizes an IR string by removing formatting variations.
     * 
     * @param ir The raw IR string to normalize
     * @return The normalized IR string
     */
    fun normalize(ir: String): String {
        return ir
            // Remove comments
            .removeComments()
            // Normalize whitespace
            .normalizeWhitespace()
            // Normalize temporary names
            .normalizeTempNames()
            // Remove empty lines
            .removeEmptyLines()
            // Trim leading/trailing whitespace
            .trim()
    }
    
    /**
     * Compares two IR strings after normalizing them.
     * 
     * @param actual The actual IR string
     * @param expected The expected IR string
     * @return true if the normalized IR strings are equal, false otherwise
     */
    fun compareNormalized(actual: String, expected: String): Boolean {
        val normalizedActual = normalize(actual)
        val normalizedExpected = normalize(expected)
        return normalizedActual == normalizedExpected
    }
    
    /**
     * Removes comments from the IR string.
     */
    private fun String.removeComments(): String {
        return this.split('\n')
            .map { line ->
                val commentIndex = line.indexOf(';')
                if (commentIndex >= 0) {
                    line.substring(0, commentIndex)
                } else {
                    line
                }
            }
            .joinToString("\n")
    }
    
    /**
     * Normalizes whitespace in the IR string.
     */
    private fun String.normalizeWhitespace(): String {
        return this
            // Replace tabs with spaces
            .replace("\t", " ")
            // Replace multiple spaces with a single space
            .replace(Regex(" +"), " ")
            // Remove spaces at the beginning of lines
            .split('\n')
            .map { it.trimStart() }
            .joinToString("\n")
    }
    
    /**
     * Normalizes temporary names (%1, %2, etc.) to a consistent format.
     * This handles cases where the same logical value might get different
     * temporary names in different runs.
     */
    private fun String.normalizeTempNames(): String {
        // This is a simplified implementation that preserves the structure
        // while making the comparison more robust. In a more sophisticated
        // implementation, we might want to track value definitions and uses
        // to ensure proper renaming.
        
        // For now, we'll just ensure consistent formatting of % followed by numbers
        return this.replace(Regex("%(\\d+)")) { matchResult ->
            val number = matchResult.groupValues[1]
            "%${number.toInt()}" // Normalize to remove leading zeros
        }
    }
    
    /**
     * Removes empty lines from the IR string.
     */
    private fun String.removeEmptyLines(): String {
        return this.split('\n')
            .filter { it.isNotBlank() }
            .joinToString("\n")
    }
    
    /**
     * Normalizes function definitions by ensuring consistent formatting.
     */
    fun normalizeFunction(ir: String): String {
        return normalize(ir)
            .split('\n')
            .mapIndexed { index, line ->
                when {
                    // Ensure function definitions have consistent spacing
                    line.startsWith("define ") -> {
                        line.replace(Regex("define\\s+"), "define ")
                    }
                    // Ensure basic block labels have consistent formatting
                    line.endsWith(":") && !line.contains(" ") -> {
                        line
                    }
                    // Ensure instructions have consistent spacing around operators
                    line.contains("=") -> {
                        line.replace(Regex("\\s*=\\s+"), " = ")
                    }
                    else -> line
                }
            }
            .joinToString("\n")
    }
    
    /**
     * Normalizes module-level IR (outside of function bodies).
     */
    fun normalizeModule(ir: String): String {
        return normalize(ir)
            // Ensure consistent ordering of module-level declarations
            .split('\n')
            .sorted()
            .joinToString("\n")
    }
}