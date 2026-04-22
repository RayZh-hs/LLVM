package space.norb.llvm.analysis.presets

import org.junit.jupiter.api.Test
import space.norb.llvm.analysis.AnalysisManager
import space.norb.llvm.builder.IRBuilder
import space.norb.llvm.structure.Module
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.VoidType
import kotlin.test.assertContentEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FunctionDependencyAnalysisTest {
    private fun analyze(module: Module): FunctionDependencyGraph =
        AnalysisManager(module).get(FunctionDependencyAnalysis::class)

    @Test
    fun `answers transitive function dependencies through condensed graph`() {
        val module = Module("test")
        val builder = IRBuilder(module)
        val a = module.registerFunction("a", FunctionType(VoidType, emptyList()))
        val b = module.registerFunction("b", FunctionType(VoidType, emptyList()))
        val c = module.registerFunction("c", FunctionType(VoidType, emptyList()))

        builder.positionAtEnd(a.insertBasicBlock("entry"))
        builder.insertVoidCall(b, emptyList())
        builder.insertRetVoid()

        builder.positionAtEnd(b.insertBasicBlock("entry"))
        builder.insertVoidCall(c, emptyList())
        builder.insertRetVoid()

        builder.positionAtEnd(c.insertBasicBlock("entry"))
        builder.insertRetVoid()

        val graph = analyze(module)

        assertTrue(graph.dependsOn(a, b))
        assertTrue(graph.dependsOn(a, c))
        assertFalse(graph.dependsOn(c, a))
        assertFalse(graph.dependsOn(a, a))
        assertContentEquals(listOf("c", "b", "a"), graph.bottomUpFunctions().map { it.name })
    }

    @Test
    fun `reports direct and mutual self dependency`() {
        val module = Module("test")
        val builder = IRBuilder(module)
        val self = module.registerFunction("self", FunctionType(VoidType, emptyList()))
        val left = module.registerFunction("left", FunctionType(VoidType, emptyList()))
        val right = module.registerFunction("right", FunctionType(VoidType, emptyList()))

        builder.positionAtEnd(self.insertBasicBlock("entry"))
        builder.insertVoidCall(self, emptyList())
        builder.insertRetVoid()

        builder.positionAtEnd(left.insertBasicBlock("entry"))
        builder.insertVoidCall(right, emptyList())
        builder.insertRetVoid()

        builder.positionAtEnd(right.insertBasicBlock("entry"))
        builder.insertVoidCall(left, emptyList())
        builder.insertRetVoid()

        val graph = analyze(module)

        assertTrue(graph.dependsOn(self, self))
        assertTrue(graph.hasSelfLoop(self))
        assertTrue(graph.dependsOn(left, left))
        assertTrue(graph.dependsOn(right, right))
        assertTrue(graph.dependsOn(left, right))
        assertTrue(graph.dependsOn(right, left))
    }
}
