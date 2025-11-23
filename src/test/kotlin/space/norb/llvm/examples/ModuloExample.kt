package space.norb.llvm.examples

import space.norb.llvm.builder.IRBuilder
import space.norb.llvm.structure.Module
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.IntegerType
import space.norb.llvm.visitors.IRPrinter

/**
 * Example demonstrating the use of modulo instructions (urem and srem).
 */
object ModuloExample {
    
    fun generateModuloExample(): String {
        val module = Module("modulo_example")
        val builder = IRBuilder(module)
        
        // Create unsigned remainder function
        val uremFunctionType = FunctionType(
            returnType = IntegerType.I32,
            paramTypes = listOf(IntegerType.I32, IntegerType.I32)
        )
        
        val uremFunction = builder.createFunction("unsigned_remainder", uremFunctionType)
        module.functions.add(uremFunction)
        
        val uremEntryBlock = uremFunction.insertBasicBlock("entry")
        uremFunction.entryBlock = uremEntryBlock
        builder.positionAtEnd(uremEntryBlock)
        
        val uremArg0 = uremFunction.parameters[0]
        val uremArg1 = uremFunction.parameters[1]
        val uremResult = builder.insertURem(uremArg0, uremArg1, "urem_result")
        builder.insertRet(uremResult)
        
        // Create signed remainder function
        val sremFunctionType = FunctionType(
            returnType = IntegerType.I32,
            paramTypes = listOf(IntegerType.I32, IntegerType.I32)
        )
        
        val sremFunction = builder.createFunction("signed_remainder", sremFunctionType)
        module.functions.add(sremFunction)
        
        val sremEntryBlock = sremFunction.insertBasicBlock("entry")
        sremFunction.entryBlock = sremEntryBlock
        builder.positionAtEnd(sremEntryBlock)
        
        val sremArg0 = sremFunction.parameters[0]
        val sremArg1 = sremFunction.parameters[1]
        val sremResult = builder.insertSRem(sremArg0, sremArg1, "srem_result")
        builder.insertRet(sremResult)
        
        // Generate IR
        return IRPrinter().print(module)
    }
    
    @JvmStatic
    fun main(args: Array<String>) {
        println("=== LLVM IR Modulo Instructions Example ===")
        println()
        println(generateModuloExample())
    }
}