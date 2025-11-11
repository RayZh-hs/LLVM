import space.norb.llvm.structure.Module
import space.norb.llvm.builder.IRBuilder
import space.norb.llvm.types.ArrayType
import space.norb.llvm.types.FloatingPointType
import space.norb.llvm.types.PointerType
import space.norb.llvm.types.FunctionType
import space.norb.llvm.values.constants.ArrayConstant
import space.norb.llvm.values.constants.FloatConstant
import space.norb.llvm.visitors.IRPrinter

fun main() {
    val module = Module("DebugArrayConstant")
    val builder = IRBuilder(module)
    
    // Create an array type for function parameters
    val arrayType = ArrayType(3, FloatingPointType.FloatType)
    
    // Create a local array constant
    val localArrayConstant = ArrayConstant.create(
        arrayType,
        FloatConstant(1.1, FloatingPointType.FloatType),
        FloatConstant(2.2, FloatingPointType.FloatType),
        FloatConstant(3.3, FloatingPointType.FloatType)
    )
    
    // Create a global variable with the array constant as initializer
    val globalArray = space.norb.llvm.values.globals.GlobalVariable.create(
        name = "globalArray",
        module = module,
        initializer = localArrayConstant,
        isConstantValue = true
    )
    module.globalVariables.add(globalArray)
    
    // Print the IR output
    val irPrinter = IRPrinter()
    val irOutput = irPrinter.print(module)
    
    println("Generated IR:")
    println(irOutput)
    
    // Check what the array constant toString() produces
    println("\nArrayConstant toString():")
    println(localArrayConstant.toString())
}