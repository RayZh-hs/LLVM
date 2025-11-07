package space.norb.llvm.manual

import space.norb.llvm.builder.IRBuilder
import space.norb.llvm.enums.IcmpPredicate
import space.norb.llvm.structure.*
import space.norb.llvm.types.*
import space.norb.llvm.values.constants.IntConstant
import space.norb.llvm.visitors.IRPrinter
import kotlin.test.Test

class ManualBlockTest {
    @Test
    fun testSimpleFunction() {
        val mod = Module("SimpleModule").apply {
            // Function and block creation logic would go here
            val builder = IRBuilder(this)
            val func = builder.createFunction(
                name="abs",
                returnType = TypeUtils.I32,
                paramTypes = listOf(TypeUtils.I32),
                isVarArg = false
            ).apply {
                val param = this.parameters[0]

                val trueBlock = builder.createBasicBlock("trueBlock", this).apply {
                    builder.positionAtEnd(this)
                    builder.insertRet(param)
                }
                val falseBlock = builder.createBasicBlock("falseBlock", this).apply {
                    builder.positionAtEnd(this)
                    val negVal = builder.insertNeg(param, name = "neg")
                    builder.insertRet(negVal)
                }

                val entryBlock = builder.createBasicBlock("entry", this).apply {
                    builder.positionAtEnd(this)
                    val cmp = builder.insertICmp(pred = IcmpPredicate.NE, lhs = param, rhs = IntConstant(0L,
                        TypeUtils.I32 as IntegerType
                    ), name = "cmp")
                    builder.insertCondBr(
                        condition = cmp,
                        trueTarget = trueBlock,
                        falseTarget = falseBlock,
                    )
                }
                this.setBasicBlocks(listOf(entryBlock, trueBlock, falseBlock))
            }
            this.functions.add(func)
        }

        val printer = IRPrinter()
        val irgen = printer.print(mod)
        println(irgen)
    }
}