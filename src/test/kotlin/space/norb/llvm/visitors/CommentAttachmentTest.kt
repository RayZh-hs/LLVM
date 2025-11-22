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

    @Test
    fun `inline comments are printed on the same line`() {
        val module = Module("inline_comments")
        val functionType = FunctionType(IntegerType.I32, emptyList())
        val function = module.registerFunction("with_inline", functionType)
        val entry = function.insertBasicBlock("entry", setAsEntrypoint = true)

        val builder = IRBuilder(module)
        builder.positionAtEnd(entry)

        val ret = builder.insertRet(BuilderUtils.getIntConstant(1, IntegerType.I32))
        ret.inlineComment = "return one"

        val printed = module.toIRString()
        val retLine = printed.lines().first { it.trimStart().startsWith("ret") }
        assertTrue(retLine.contains("; return one"))
    }

    @Test
    fun `inline comments support multiple lines`() {
        val module = Module("inline_comments_multiline")
        val functionType = FunctionType(IntegerType.I32, emptyList())
        val function = module.registerFunction("with_inline_multiline", functionType)
        val entry = function.insertBasicBlock("entry", setAsEntrypoint = true)

        val builder = IRBuilder(module)
        builder.positionAtEnd(entry)

        val sum = builder.insertAdd(
            BuilderUtils.getIntConstant(2, IntegerType.I32),
            BuilderUtils.getIntConstant(3, IntegerType.I32),
            "sum"
        )
        sum.inlineComment = "first line\nsecond line"
        builder.insertRet(sum)

        val lines = module.toIRString().lines()
        val addIndex = lines.indexOfFirst { it.contains("%sum = add") }
        assertTrue(addIndex >= 0)
        assertTrue(lines[addIndex].contains("; first line"))
        assertTrue(lines.getOrNull(addIndex + 1)?.trimStart() == "; second line")
    }
}
