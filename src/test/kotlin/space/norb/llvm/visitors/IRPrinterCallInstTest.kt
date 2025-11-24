package space.norb.llvm.visitors

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.assertFailsWith
import space.norb.llvm.builder.IRBuilder
import space.norb.llvm.structure.Module
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.VoidType

class IRPrinterCallInstTest {

    @Test
    fun printsVoidCallWithoutResultAssignment() {
        val module = Module("void_call_module")
        val builder = IRBuilder(module)

        val callee = module.registerFunction(
            name = "void_target",
            type = FunctionType(VoidType, emptyList())
        )
        val caller = module.registerFunction(
            name = "caller",
            type = FunctionType(VoidType, emptyList())
        )
        val entry = caller.insertBasicBlock("entry", setAsEntrypoint = true)
        builder.positionAtEnd(entry)

        builder.insertCall(callee, emptyList())
        builder.insertRetVoid()

        val ir = IRPrinter().print(module)

        assertTrue(ir.contains("call void @void_target()"), "Void call should be emitted without a result holder")
        assertFalse(ir.contains("= call void @void_target()"), "Void call must not assign to an SSA value")
    }

    @Test
    fun rejectsExplicitResultNameForVoidCall() {
        val module = Module("void_call_named")
        val builder = IRBuilder(module)

        val callee = module.registerFunction(
            name = "void_target",
            type = FunctionType(VoidType, emptyList())
        )
        val caller = module.registerFunction(
            name = "caller",
            type = FunctionType(VoidType, emptyList())
        )
        val entry = caller.insertBasicBlock("entry", setAsEntrypoint = true)
        builder.positionAtEnd(entry)

        assertFailsWith<IllegalArgumentException> {
            builder.insertCall(callee, emptyList(), name = "result")
        }
    }
}
