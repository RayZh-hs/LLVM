package space.norb.llvm.examples

import space.norb.llvm.builder.IRBuilder
import space.norb.llvm.core.Constant
import space.norb.llvm.structure.Module
import space.norb.llvm.types.ArrayType
import space.norb.llvm.types.PointerType
import space.norb.llvm.utils.*
import space.norb.llvm.values.constants.IntConstant
import space.norb.llvm.values.globals.GlobalVariable

fun main() {
    // This example stores a C-style "Hello, World!" string and returns the pointer to it.
    val module = Module("HelloWorldModule").apply {
        val builder = IRBuilder(this)

        // store the "Hello, World!" string as a global constant
        val helloWorldString = "Hello, World!\n\u0000".toByteArray(Charsets.US_ASCII)
        val globalHelloWorldPtr = this.registerGlobalVariable(
            globalVariable = GlobalVariable.createWithElementType(
                name = "helloWorldStr",
                elementType = ArrayType(
                    helloWorldString.size,
                    CharType
                ),
                module = this,
                initializer = TODO()
            )
        )

        val helloFunction = this.registerFunction("hello", returnType = PointerType, parameterTypes = emptyList()).apply {
            this.insertBasicBlock("entry", setAsEntrypoint = true).apply {
                builder.positionAtEnd(this)
            }
        }
    }
    println(module.toIRString())
}