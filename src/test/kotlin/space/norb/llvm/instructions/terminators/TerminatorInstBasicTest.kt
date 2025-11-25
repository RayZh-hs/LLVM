package space.norb.llvm.instructions.terminators

import space.norb.llvm.core.Type
import space.norb.llvm.structure.BasicBlock
import space.norb.llvm.structure.Function
import space.norb.llvm.structure.Module
import space.norb.llvm.values.constants.IntConstant
import space.norb.llvm.types.IntegerType
import space.norb.llvm.types.FunctionType
import space.norb.llvm.types.VoidType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * Basic test for terminator instructions that doesn't require the full project to compile.
 * This test focuses on the core functionality of the implemented terminator instructions.
 */
class TerminatorInstBasicTest {
    
    @Test
    fun testBranchInstFactoryMethods() {
        val voidType = Type.getVoidType()
        val module = Module("test")
        val functionType = Type.getFunctionType(voidType, emptyList()) as FunctionType
        val function = Function("test", functionType, module)
        val dest = BasicBlock("dest", function)
        
        // Test unconditional branch creation
        val uncondBranch = BranchInst.createUnconditional("br", voidType, dest)
        assertTrue(uncondBranch.isUnconditional())
        assertFalse(uncondBranch.isConditional())
        assertNull(uncondBranch.getCondition())
        assertEquals(dest, uncondBranch.getDestination())
        
        // Test conditional branch creation
        val i1Type = Type.getIntegerType(1) as IntegerType
        val condition = IntConstant(1L, i1Type)
        val trueDest = BasicBlock("true", function)
        val falseDest = BasicBlock("false", function)
        
        val condBranch = BranchInst.createConditional("condbr", voidType, condition, trueDest, falseDest)
        assertFalse(condBranch.isUnconditional())
        assertTrue(condBranch.isConditional())
        assertEquals(condition, condBranch.getCondition())
        assertEquals(trueDest, condBranch.getTrueDestination())
        assertEquals(falseDest, condBranch.getFalseDestination())
    }
    
    @Test
    fun testSwitchInstFactoryMethod() {
        val voidType = Type.getVoidType()
        val module = Module("test")
        val functionType = Type.getFunctionType(voidType, emptyList()) as FunctionType
        val function = Function("test", functionType, module)
        
        val i32Type = Type.getIntegerType(32) as IntegerType
        val condition = IntConstant(42L, i32Type)
        val defaultDest = BasicBlock("default", function)
        val case1Value = IntConstant(1L, i32Type)
        val case1Dest = BasicBlock("case1", function)
        val case2Value = IntConstant(2L, i32Type)
        val case2Dest = BasicBlock("case2", function)
        
        val cases = listOf(Pair(case1Value, case1Dest), Pair(case2Value, case2Dest))
        val switch = SwitchInst.create("switch", voidType, condition, defaultDest, cases)
        
        assertEquals(condition, switch.getCondition())
        assertEquals(defaultDest, switch.getDefaultDestination())
        assertEquals(2, switch.getNumCases())
        
        assertEquals(case1Value, switch.getCaseValue(0))
        assertEquals(case1Dest, switch.getCaseDestination(0))
        assertEquals(case2Value, switch.getCaseValue(1))
        assertEquals(case2Dest, switch.getCaseDestination(1))
        
        val retrievedCases = switch.getCases()
        assertEquals(2, retrievedCases.size)
        assertEquals(case1Value, retrievedCases[0].first)
        assertEquals(case1Dest, retrievedCases[0].second)
        assertEquals(case2Value, retrievedCases[1].first)
        assertEquals(case2Dest, retrievedCases[1].second)
    }
    
    @Test
    fun testReturnInstFactoryMethods() {
        val voidType = Type.getVoidType()
        
        // Test void return
        val voidRet = ReturnInst.createVoid("ret", voidType)
        assertTrue(voidRet.isFunctionTerminating())
        assertFalse(voidRet.hasReturnValue())
        assertNull(voidRet.getReturnValue())
        
        // Test value return
        val i32Type = Type.getIntegerType(32) as IntegerType
        val returnValue = IntConstant(42L, i32Type)
        val valRet = ReturnInst.createWithValue("ret", voidType, returnValue)
        
        assertTrue(valRet.isFunctionTerminating())
        assertTrue(valRet.hasReturnValue())
        assertEquals(returnValue, valRet.getReturnValue())
    }
    
    @Test
    fun testTerminatorInstSuccessors() {
        val voidType = Type.getVoidType()
        val module = Module("test")
        val functionType = Type.getFunctionType(voidType, emptyList()) as FunctionType
        val function = Function("test", functionType, module)
        
        // Test unconditional branch successors
        val dest = BasicBlock("dest", function)
        val uncondBranch = BranchInst.createUnconditional("br", voidType, dest)
        val uncondSuccessors = uncondBranch.getSuccessors()
        assertEquals(1, uncondSuccessors.size)
        assertEquals(dest, uncondSuccessors[0])
        
        // Test conditional branch successors
        val i1Type = Type.getIntegerType(1) as IntegerType
        val condition = IntConstant(1L, i1Type)
        val trueDest = BasicBlock("true", function)
        val falseDest = BasicBlock("false", function)
        val condBranch = BranchInst.createConditional("condbr", voidType, condition, trueDest, falseDest)
        val condSuccessors = condBranch.getSuccessors()
        assertEquals(2, condSuccessors.size)
        assertTrue(condSuccessors.contains(trueDest))
        assertTrue(condSuccessors.contains(falseDest))
        
        // Test switch successors
        val i32Type = Type.getIntegerType(32) as IntegerType
        val switchCondition = IntConstant(42L, i32Type)
        val defaultDest = BasicBlock("default", function)
        val case1Value = IntConstant(1L, i32Type)
        val case1Dest = BasicBlock("case1", function)
        val cases = listOf(Pair(case1Value, case1Dest))
        val switch = SwitchInst.create("switch", voidType, switchCondition, defaultDest, cases)
        val switchSuccessors = switch.getSuccessors()
        assertEquals(2, switchSuccessors.size) // default + 1 case
        assertTrue(switchSuccessors.contains(defaultDest))
        assertTrue(switchSuccessors.contains(case1Dest))
        
        // Test return successors (should be empty)
        val ret = ReturnInst.createVoid("ret", voidType)
        val retSuccessors = ret.getSuccessors()
        assertEquals(0, retSuccessors.size)
        
        // Test unreachable successors (should be empty)
        val unreachable = UnreachableInst.create("unreachable", voidType)
        val unreachableSuccessors = unreachable.getSuccessors()
        assertEquals(0, unreachableSuccessors.size)
    }
    
    @Test
    fun testUnreachableInstFactoryMethod() {
        val voidType = Type.getVoidType()
        
        // Test unreachable creation
        val unreachable = UnreachableInst.create("unreachable", voidType)
        assertTrue(unreachable.isFunctionTerminating())
        
        val successors = unreachable.getSuccessors()
        assertEquals(0, successors.size)
    }
}