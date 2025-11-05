package space.norb.llvm.e2e.framework

import java.io.InputStream

/**
 * Utility class for loading expected IR resource files from the test resources directory.
 * 
 * This component provides a simple interface to load LLVM IR strings that are stored
 * as resource files alongside the test cases. It handles the common cases of loading
 * files relative to the test class location.
 */
object IRResourceLoader {
    
    /**
     * Loads an IR resource file as a string.
     * 
     * @param resourcePath The path to the resource file, relative to the test resources root
     * @return The content of the resource file as a string
     * @throws IllegalArgumentException if the resource cannot be found
     * @throws RuntimeException if there's an error reading the resource
     */
    fun loadIRResource(resourcePath: String): String {
        val inputStream = getResourceAsStream(resourcePath)
            ?: throw IllegalArgumentException("Resource not found: $resourcePath")
        
        return inputStream.use { it.bufferedReader().readText() }
    }
    
    /**
     * Loads an IR resource file relative to a specific test class.
     * 
     * @param testClass The test class used as a reference point for relative paths
     * @param resourceName The name of the resource file
     * @return The content of the resource file as a string
     * @throws IllegalArgumentException if the resource cannot be found
     * @throws RuntimeException if there's an error reading the resource
     */
    fun loadIRResourceRelativeToClass(testClass: Class<*>, resourceName: String): String {
        val resourcePath = "${testClass.packageName.replace('.', '/')}/$resourceName"
        return loadIRResource(resourcePath)
    }
    
    /**
     * Loads an IR resource file from a subdirectory relative to a test class.
     * 
     * @param testClass The test class used as a reference point
     * @param subdirectory The subdirectory containing the resource
     * @param resourceName The name of the resource file
     * @return The content of the resource file as a string
     * @throws IllegalArgumentException if the resource cannot be found
     * @throws RuntimeException if there's an error reading the resource
     */
    fun loadIRResourceFromSubdirectory(
        testClass: Class<*>, 
        subdirectory: String, 
        resourceName: String
    ): String {
        val basePath = testClass.packageName.replace('.', '/')
        val resourcePath = "$basePath/$subdirectory/$resourceName"
        return loadIRResource(resourcePath)
    }
    
    /**
     * Checks if a resource exists without loading it.
     * 
     * @param resourcePath The path to the resource file
     * @return true if the resource exists, false otherwise
     */
    fun resourceExists(resourcePath: String): Boolean {
        return getResourceAsStream(resourcePath) != null
    }
    
    /**
     * Gets an input stream for a resource file.
     * 
     * @param resourcePath The path to the resource file
     * @return An InputStream for the resource, or null if not found
     */
    private fun getResourceAsStream(resourcePath: String): InputStream? {
        return this::class.java.classLoader.getResourceAsStream(resourcePath)
    }
}