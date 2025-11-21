package space.norb.llvm.visitors

import space.norb.llvm.builder.BuilderUtils
import space.norb.llvm.builder.IRBuilder
import space.norb.llvm.structure.Module
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.IntegerType
import space.norb.llvm.types.VoidType
import kotlin.test.Test
import kotlin.test.assertTrue

class CommentAttachmentTest {

    @Test
    fun `comment attachments are printed in order`() {
        val module = Module("comments")
        val functionType = FunctionType(VoidType, emptyList())
        val function = module.registerFunction("emit_comments", functionType)
        val entry = function.insertBasicBlock("entry", setAsEntrypoint = true)

        val builder = IRBuilder(module)
        builder.positionAtEnd(entry)

        builder.insertComment("allocate space for result")
        val slot = builder.insertAlloca(IntegerType.I32, "slot")
        builder.insertStore(BuilderUtils.getIntConstant(42, IntegerType.I32), slot)
        builder.insertComment("finish up")
        builder.insertRetVoid()

        val printed = module.toIRString()

        assertTrue(printed.contains("; allocate space for result"))
        assertTrue(printed.contains("%slot = alloca i32"))
        assertTrue(printed.contains("; finish up"))
        assertTrue(printed.contains("ret void"))

        val validator = IRValidator()
        assertTrue(validator.validate(module))
    }
}
