package space.norb.llvm.e2e

import org.jetbrains.kotlin.cli.common.ExitCode
import org.jetbrains.kotlin.cli.common.arguments.K2JVMCompilerArguments
import org.jetbrains.kotlin.cli.common.messages.MessageRenderer
import org.jetbrains.kotlin.cli.common.messages.PrintingMessageCollector
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
import org.jetbrains.kotlin.config.Services
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import space.norb.llvm.builder.IRBuilder
import space.norb.llvm.e2e.framework.IRNormalizer
import space.norb.llvm.structure.Module
import space.norb.llvm.visitors.IRPrinter
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.net.URLClassLoader
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.util.Comparator
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.max
import kotlin.test.fail

class DynamicEndToEndTest {

    @TestFactory
    fun dynamicTests(): Collection<DynamicTest> {
        val testCases = discoverTestCases()
        if (testCases.isEmpty()) {
            fail("No end-to-end test cases discovered under ${RESOURCES_ROOT}")
        }

        return testCases.map { testCase ->
            DynamicTest.dynamicTest(testCase.name) {
                executeTest(testCase)
            }
        }
    }

    private fun executeTest(testCase: TestCase) {
        val compiled = TestCompiler.compile(testCase)
        val module = Module(testCase.name)
        val builder = IRBuilder(module)

        try {
            compiled.buildIrMethod.invoke(compiled.instance, module, builder)
        } catch (ex: Throwable) {
            fail(
                buildString {
                    appendLine("Failed to execute buildIR for '${testCase.name}'")
                    appendLine("Source: ${testCase.source}")
                    appendLine("Reason: ${ex.message}")
                }
            )
        }

        val printer = IRPrinter()
        val actualIr = printer.print(module)
        writeDump(testCase.dump, actualIr)

        val expectedIr = readFile(testCase.expected)
        val normalizedActual = IRNormalizer.normalize(actualIr)
        val normalizedExpected = IRNormalizer.normalize(expectedIr)

        if (normalizedActual != normalizedExpected) {
            val diff = generateDiff(normalizedExpected, normalizedActual)
            fail(
                buildString {
                    appendLine("IR mismatch for test '${testCase.name}'")
                    appendLine("Source: ${testCase.source}")
                    appendLine("Expected: ${testCase.expected}")
                    appendLine("Dump: ${testCase.dump}")
                    appendLine()
                    appendLine("Diff (expected vs actual):")
                    appendLine(diff)
                    appendLine()
                    appendLine("Normalized expected IR:")
                    appendLine(normalizedExpected)
                    appendLine()
                    appendLine("Normalized actual IR:")
                    appendLine(normalizedActual)
                }
            )
        }
    }

    private fun discoverTestCases(): List<TestCase> {
        if (!Files.isDirectory(RESOURCES_ROOT)) {
            return emptyList()
        }

        val directories = Files.list(RESOURCES_ROOT).use { paths ->
            paths
                .filter { Files.isDirectory(it) }
                .sorted(Comparator.comparing { it.fileName.toString() })
                .toList()
        }

        return directories.mapNotNull { buildTestCase(it) }
    }

    private fun buildTestCase(directory: Path): TestCase? {
        val kotlinSources = collectKotlinSources(directory)
        if (kotlinSources.isEmpty()) {
            return null
        }
        if (kotlinSources.size > 1) {
            fail(
                "Multiple Kotlin sources found in $directory. " +
                    "Expected exactly one test source. Found: ${kotlinSources.map { it.fileName }}"
            )
        }

        val sourceFile = kotlinSources.single()
        val expectedFile = directory.resolve("expected.ll")
        if (!Files.isRegularFile(expectedFile)) {
            fail("Missing expected.ll for test directory $directory")
        }

        val qualifiedObjectName = extractQualifiedObjectName(sourceFile)

        return TestCase(
            name = directory.fileName.toString(),
            directory = directory,
            source = sourceFile,
            expected = expectedFile,
            dump = directory.resolve("dump.ll"),
            objectQualifiedName = qualifiedObjectName
        )
    }

    private fun collectKotlinSources(directory: Path): List<Path> {
        val sources = mutableListOf<Path>()
        Files.newDirectoryStream(directory, "*.kt").use { stream ->
            for (path in stream) {
                if (Files.isRegularFile(path)) {
                    sources.add(path)
                }
            }
        }
        return sources
    }

    private fun extractQualifiedObjectName(source: Path): String {
        val content = readFile(source)
        val packageName = PACKAGE_REGEX.find(content)?.groupValues?.get(1)
            ?: fail("Missing package declaration in $source")
        val objectName = OBJECT_REGEX.find(content)?.groupValues?.get(1)
            ?: fail("Missing top-level object declaration in $source")
        return "$packageName.$objectName"
    }

    private fun writeDump(target: Path, content: String) {
        target.parent?.let { Files.createDirectories(it) }
        Files.writeString(
            target,
            content,
            StandardCharsets.UTF_8,
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING,
            StandardOpenOption.WRITE
        )
    }

    private fun readFile(path: Path): String = Files.readString(path, StandardCharsets.UTF_8)

    private fun generateDiff(expected: String, actual: String): String {
        val expectedLines = expected.split('\n')
        val actualLines = actual.split('\n')
        val maxLines = max(expectedLines.size, actualLines.size)
        val diffBuilder = StringBuilder()

        for (index in 0 until maxLines) {
            val expectedLine = expectedLines.getOrNull(index)
            val actualLine = actualLines.getOrNull(index)

            if (expectedLine == actualLine) {
                continue
            }

            diffBuilder.appendLine("line ${index + 1}:")
            diffBuilder.appendLine("  expected: ${expectedLine ?: "<missing>"}")
            diffBuilder.appendLine("  actual  : ${actualLine ?: "<missing>"}")
        }

        return diffBuilder.toString().ifBlank { "<no diff available>" }
    }

    private data class TestCase(
        val name: String,
        val directory: Path,
        val source: Path,
        val expected: Path,
        val dump: Path,
        val objectQualifiedName: String
    )

    private data class CompiledTest(
        val instance: Any,
        val buildIrMethod: java.lang.reflect.Method,
        val classLoader: ClassLoader
    )

    private object TestCompiler {
        private val cache = ConcurrentHashMap<Path, CompiledTest>()
        private val compilationRoot: Path = Files.createTempDirectory("dynamic-e2e-classes").also {
            it.toFile().deleteOnExit()
        }

        fun compile(testCase: TestCase): CompiledTest {
            val key = testCase.source.toAbsolutePath().normalize()
            return cache.computeIfAbsent(key) { compileCase(testCase) }
        }

        private fun compileCase(testCase: TestCase): CompiledTest {
            val outputDir = Files.createDirectories(
                compilationRoot.resolve(sanitizeName(testCase.name))
            )

            val logBuffer = ByteArrayOutputStream()
            val logStream = PrintStream(logBuffer)

            val arguments = K2JVMCompilerArguments().apply {
                freeArgs = listOf(testCase.source.toString())
                destination = outputDir.toString()
                classpath = System.getProperty("java.class.path") ?: ""
                jvmTarget = "21"
                noStdlib = true
                noReflect = true
            }

            val exitCode = K2JVMCompiler().exec(
                PrintingMessageCollector(logStream, MessageRenderer.PLAIN_RELATIVE_PATHS, false),
                Services.EMPTY,
                arguments
            )

            logStream.flush()
            logStream.close()

            if (exitCode != ExitCode.OK) {
                val compilerOutput = logBuffer.toString(StandardCharsets.UTF_8)
                fail(
                    buildString {
                        appendLine("Failed to compile test source ${testCase.source}")
                        appendLine("Compiler exit code: $exitCode")
                        appendLine("Compiler output:")
                        appendLine(compilerOutput)
                    }
                )
            }

            val classLoader = URLClassLoader(arrayOf(outputDir.toUri().toURL()), DynamicEndToEndTest::class.java.classLoader)
            val testClass = runCatching { classLoader.loadClass(testCase.objectQualifiedName) }
                .getOrElse { throwable ->
                    fail(
                        buildString {
                            appendLine("Unable to load compiled test class ${testCase.objectQualifiedName}")
                            appendLine("Source: ${testCase.source}")
                            appendLine("Reason: ${throwable.message}")
                        }
                    )
                }

            val instance = runCatching { testClass.getField("INSTANCE").get(null) }
                .getOrElse { throwable ->
                    fail(
                        buildString {
                            appendLine("Compiled object ${testCase.objectQualifiedName} does not expose INSTANCE")
                            appendLine("Source: ${testCase.source}")
                            appendLine("Reason: ${throwable.message}")
                        }
                    )
                }

            val buildIr = runCatching {
                testClass.getDeclaredMethod("buildIR", Module::class.java, IRBuilder::class.java)
                    .apply { isAccessible = true }
            }.getOrElse { throwable ->
                fail(
                    buildString {
                        appendLine("Missing buildIR(Module, IRBuilder) on ${testCase.objectQualifiedName}")
                        appendLine("Source: ${testCase.source}")
                        appendLine("Reason: ${throwable.message}")
                    }
                )
            }

            return CompiledTest(instance = instance, buildIrMethod = buildIr, classLoader = classLoader)
        }

        private fun sanitizeName(raw: String): String = raw.replace(NON_IDENTIFIER_CHAR_REGEX, "_")
    }

    companion object {
        private val RESOURCES_ROOT: Path = Paths.get(
            "src",
            "test",
            "resources",
            "space",
            "norb",
            "llvm",
            "e2e"
        ).toAbsolutePath().normalize()

        private val PACKAGE_REGEX = Regex("^\\s*package\\s+([A-Za-z0-9_.]+)", RegexOption.MULTILINE)
        private val OBJECT_REGEX = Regex("^\\s*object\\s+([A-Za-z0-9_]+)", RegexOption.MULTILINE)
        private val NON_IDENTIFIER_CHAR_REGEX = Regex("[^A-Za-z0-9_.-]")
    }
}