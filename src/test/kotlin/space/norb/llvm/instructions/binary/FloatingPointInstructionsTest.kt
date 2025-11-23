package space.norb.llvm.instructions.binary

import org.junit.jupiter.api.Test
import space.norb.llvm.builder.IRBuilder
import space.norb.llvm.structure.Module
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.FloatingPointType
import space.norb.llvm.visitors.IRPrinter
import space.norb.llvm.enums.FcmpPredicate
import kotlin.test.assertTrue

class FloatingPointInstructionsTest {
    
    @Test
    fun testFloatingPointArithmetic() {
        val module = Module("test_fp_arithmetic")
        val builder = IRBuilder(module)
        
        // Create function type: float (float, float)
        val functionType = FunctionType(
            returnType = FloatingPointType.FloatType,
            paramTypes = listOf(FloatingPointType.FloatType, FloatingPointType.FloatType)
        )
        
        // Create the function
        val function = builder.createFunction("test_fp_ops", functionType)
        module.functions.add(function)
        
        // Create entry block
        val entryBlock = function.insertBasicBlock("entry")
        function.entryBlock = entryBlock
        builder.positionAtEnd(entryBlock)
        
        // Get function arguments
        val arg0 = function.parameters[0]
        val arg1 = function.parameters[1]
        
        // Create floating point instructions
        val addResult = builder.insertFAdd(arg0, arg1, "add_res")
        val subResult = builder.insertFSub(arg0, arg1, "sub_res")
        val mulResult = builder.insertFMul(arg0, arg1, "mul_res")
        val divResult = builder.insertFDiv(arg0, arg1, "div_res")
        val remResult = builder.insertFRem(arg0, arg1, "rem_res")
        
        // Return the last result
        builder.insertRet(remResult)
        
        // Print the IR
        val ir = IRPrinter().print(module)
        
        // Verify the IR contains the instructions
        assertTrue(ir.contains("fadd float %arg0, %arg1"), "IR should contain fadd instruction")
        assertTrue(ir.contains("fsub float %arg0, %arg1"), "IR should contain fsub instruction")
        assertTrue(ir.contains("fmul float %arg0, %arg1"), "IR should contain fmul instruction")
        assertTrue(ir.contains("fdiv float %arg0, %arg1"), "IR should contain fdiv instruction")
        assertTrue(ir.contains("frem float %arg0, %arg1"), "IR should contain frem instruction")
    }
    
    @Test
    fun testFloatingPointComparison() {
        val module = Module("test_fp_comparison")
        val builder = IRBuilder(module)
        
        // Create function type: i1 (double, double) - returns boolean
        val functionType = FunctionType(
            returnType = space.norb.llvm.types.IntegerType.I1,
            paramTypes = listOf(FloatingPointType.DoubleType, FloatingPointType.DoubleType)
        )
        
        // Create the function
        val function = builder.createFunction("test_fcmp", functionType)
        module.functions.add(function)
        
        // Create entry block
        val entryBlock = function.insertBasicBlock("entry")
        function.entryBlock = entryBlock
        builder.positionAtEnd(entryBlock)
        
        // Get function arguments
        val arg0 = function.parameters[0]
        val arg1 = function.parameters[1]
        
        // Create fcmp instructions
        val oeqResult = builder.insertFCmp(FcmpPredicate.OEQ, arg0, arg1, "oeq_res")
        val ultResult = builder.insertFCmp(FcmpPredicate.ULT, arg0, arg1, "ult_res")
        
        // Return the first result
        builder.insertRet(oeqResult)
        
        // Print the IR
        val ir = IRPrinter().print(module)
        
        // Verify the IR contains the instructions
        assertTrue(ir.contains("fcmp oeq double %arg0, %arg1"), "IR should contain fcmp oeq instruction")
        assertTrue(ir.contains("fcmp ult double %arg0, %arg1"), "IR should contain fcmp ult instruction")
    }
}
