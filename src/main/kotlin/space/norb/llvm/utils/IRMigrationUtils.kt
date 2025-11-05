package space.norb.llvm.utils

import space.norb.llvm.core.Type
import space.norb.llvm.types.*
import space.norb.llvm.visitors.IRPrinter
import space.norb.llvm.structure.Module
import space.norb.llvm.structure.Function
import space.norb.llvm.values.globals.GlobalVariable

/**
 * Utilities for migrating IR between typed and un-typed pointer formats.
 *
 * This class provides methods to convert IR representations between the legacy
 * typed pointer model and the new un-typed pointer model compliant with the
 * latest LLVM IR standard.
 *
 * The migration utilities respect the migration flag and can be used to:
 * - Convert IR strings from typed to un-typed pointer format
 * - Convert IR strings from un-typed to typed pointer format
 * - Validate IR format compatibility
 * - Generate migration reports
 */
object IRMigrationUtils {
    
    /**
     * Converts an IR string from typed pointer format to un-typed pointer format.
     *
     * This method parses the IR string and replaces all typed pointer syntax
     * (e.g., "i32*", "float*") with un-typed pointer syntax ("ptr").
     *
     * @param irString The IR string in typed pointer format
     * @return The IR string converted to un-typed pointer format
     */
    fun convertToUntypedPointers(irString: String): String {
        var result = irString
        
        // Replace typed pointer patterns with un-typed pointers
        // Pattern: <type>* -> ptr
        val typedPointerRegex = Regex("""([a-zA-Z0-9_\[\]{}<>\s]+)\*""")
        result = result.replace(typedPointerRegex) { matchResult ->
            val pointeeType = matchResult.groupValues[1].trim()
            // Only replace if it looks like a valid type
            if (isValidType(pointeeType)) {
                "ptr"
            } else {
                matchResult.value // Keep original if not a valid type
            }
        }
        
        return result
    }
    
    /**
     * Converts an IR string from un-typed pointer format to typed pointer format.
     *
     * This method parses the IR string and replaces un-typed pointer syntax
     * ("ptr") with appropriate typed pointer syntax based on context.
     *
     * Note: This conversion requires type inference and may not always be
     * possible without additional context information.
     *
     * @param irString The IR string in un-typed pointer format
     * @param context Optional context information for type inference
     * @return The IR string converted to typed pointer format, or null if conversion failed
     */
    fun convertToTypedPointers(irString: String, context: TypeInferenceContext? = null): String? {
        var result = irString
        
        // Replace un-typed pointers with typed pointers based on context
        // This is more complex and requires type inference
        val untypedPointerRegex = Regex("""\bptr\b""")
        
        result = result.replace(untypedPointerRegex) { matchResult ->
            // Try to infer the appropriate typed pointer from context
            context?.inferPointerType(matchResult.range) ?: "i8*" // Default to i8* if no context
        }
        
        return result
    }
    
    /**
     * Validates if an IR string is compatible with un-typed pointer format.
     *
     * @param irString The IR string to validate
     * @return true if compatible with un-typed pointers, false otherwise
     */
    fun validateUntypedPointerFormat(irString: String): Boolean {
        // Check for any remaining typed pointer syntax
        val typedPointerRegex = Regex("""\b[a-zA-Z0-9_\[\]{}<>\s]+\*\b""")
        return !typedPointerRegex.containsMatchIn(irString)
    }
    
    /**
     * Validates if an IR string is compatible with typed pointer format.
     *
     * @param irString The IR string to validate
     * @return true if compatible with typed pointers, false otherwise
     */
    fun validateTypedPointerFormat(irString: String): Boolean {
        // Check for un-typed pointer syntax (as a type, not as a variable name)
        // Look for "ptr" followed by space, comma, or end of line, but not preceded by %
        val untypedPointerRegex = Regex("""(?<!%)\bptr\b(?=\s|,|$)""")
        // If we find un-typed pointers, it's not a pure typed format
        return !untypedPointerRegex.containsMatchIn(irString)
    }
    
    /**
     * Generates a migration report showing differences between typed and un-typed formats.
     *
     * @param originalIR The original IR string
     * @param useTypedPointers Whether the original uses typed pointers
     * @return A migration report with differences and recommendations
     */
    fun generateMigrationReport(originalIR: String, useTypedPointers: Boolean): MigrationReport {
        val convertedIR = if (useTypedPointers) {
            convertToUntypedPointers(originalIR)
        } else {
            convertToTypedPointers(originalIR) ?: originalIR
        }
        
        val differences = analyzeDifferences(originalIR, convertedIR)
        val recommendations = generateRecommendations(differences)
        
        return MigrationReport(
            originalFormat = if (useTypedPointers) "typed" else "un-typed",
            convertedFormat = if (useTypedPointers) "un-typed" else "typed",
            originalIR = originalIR,
            convertedIR = convertedIR,
            differences = differences,
            recommendations = recommendations
        )
    }
    
    /**
     * Migrates a module's IR representation between pointer formats.
     *
     * @param module The module to migrate
     * @param toTypedPointers Whether to convert to typed pointers (true) or un-typed (false)
     * @return The migrated module IR string
     */
    fun migrateModuleIR(module: Module, toTypedPointers: Boolean): String {
        val printer = IRPrinter()
        val originalIR = printer.print(module)
        
        return if (toTypedPointers) {
            convertToTypedPointers(originalIR) ?: originalIR
        } else {
            convertToUntypedPointers(originalIR)
        }
    }
    
    /**
     * Checks if a string represents a valid LLVM type.
     *
     * @param typeString The type string to check
     * @return true if valid, false otherwise
     */
    private fun isValidType(typeString: String): Boolean {
        // Basic validation for common LLVM types
        return when {
            typeString.matches(Regex("""i\d+""")) -> true // Integer types
            typeString == "float" || typeString == "double" -> true // Floating point
            typeString == "void" -> true // Void type
            typeString.matches(Regex("""\[\d+\s+x\s+[a-zA-Z0-9_\[\]{}<>\s]+""")) -> true // Array types
            typeString.matches(Regex("""\{[a-zA-Z0-9_\[\]{}<>\s,]*\}""")) -> true // Struct types
            typeString.matches(Regex("""<[a-zA-Z0-9_\[\]{}<>\s,]*>""")) -> true // Packed struct types
            else -> false
        }
    }
    
    /**
     * Analyzes differences between original and converted IR.
     *
     * @param original The original IR string
     * @param converted The converted IR string
     * @return A list of differences found
     */
    private fun analyzeDifferences(original: String, converted: String): List<String> {
        val differences = mutableListOf<String>()
        
        if (original != converted) {
            differences.add("IR format differs between original and converted versions")
            
            // Count pointer type changes
            val originalTypedPointers = Regex("""[a-zA-Z0-9_\[\]{}<>\s]+\*""").findAll(original).count()
            val convertedUntypedPointers = Regex("""\bptr\b""").findAll(converted).count()
            
            if (originalTypedPointers > 0 && convertedUntypedPointers > 0) {
                differences.add("Converted $originalTypedPointers typed pointers to $convertedUntypedPointers un-typed pointers")
            }
        }
        
        return differences
    }
    
    /**
     * Generates recommendations based on differences found.
     *
     * @param differences The list of differences
     * @return A list of recommendations
     */
    private fun generateRecommendations(differences: List<String>): List<String> {
        val recommendations = mutableListOf<String>()
        
        if (differences.isNotEmpty()) {
            recommendations.add("Review the converted IR for correctness")
            recommendations.add("Run tests to ensure functionality is preserved")
            recommendations.add("Consider updating code that depends on specific pointer type representations")
        }
        
        return recommendations
    }
}

/**
 * Context information for type inference during pointer format conversion.
 *
 * This class provides information that helps infer the appropriate typed
 * pointer type when converting from un-typed to typed pointer format.
 */
class TypeInferenceContext {
    private val typeHints = mutableMapOf<Int, String>()
    
    /**
     * Adds a type hint for a specific position in the IR string.
     *
     * @param position The position in the IR string
     * @param typeHint The suggested type for that position
     */
    fun addTypeHint(position: Int, typeHint: String) {
        typeHints[position] = typeHint
    }
    
    /**
     * Infers the appropriate pointer type for a given position.
     *
     * @param range The range in the IR string
     * @return The inferred pointer type, or a default if no hint available
     */
    fun inferPointerType(range: IntRange): String? {
        // Find the closest type hint
        val closestHint = typeHints.minByOrNull { kotlin.math.abs(it.key - range.first) }
        return closestHint?.value
    }
}

/**
 * Migration report containing information about IR format conversion.
 *
 * @param originalFormat The original pointer format ("typed" or "un-typed")
 * @param convertedFormat The converted pointer format ("typed" or "un-typed")
 * @param originalIR The original IR string
 * @param convertedIR The converted IR string
 * @param differences List of differences found during conversion
 * @param recommendations List of recommendations for the migration
 */
data class MigrationReport(
    val originalFormat: String,
    val convertedFormat: String,
    val originalIR: String,
    val convertedIR: String,
    val differences: List<String>,
    val recommendations: List<String>
) {
    /**
     * Returns a summary of the migration report.
     *
     * @return A string summary of the migration
     */
    fun getSummary(): String {
        return buildString {
            appendLine("IR Migration Report")
            appendLine("==================")
            appendLine("Original Format: $originalFormat")
            appendLine("Converted Format: $convertedFormat")
            appendLine()
            
            if (differences.isNotEmpty()) {
                appendLine("Differences Found:")
                differences.forEach { appendLine("- $it") }
                appendLine()
            }
            
            if (recommendations.isNotEmpty()) {
                appendLine("Recommendations:")
                recommendations.forEach { appendLine("- $it") }
                appendLine()
            }
            
            appendLine("Conversion ${if (originalIR == convertedIR) "made no changes" else "completed successfully"}")
        }
    }
}