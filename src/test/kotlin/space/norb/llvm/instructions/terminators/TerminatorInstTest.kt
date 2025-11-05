package space.norb.llvm.instructions.terminators

import space.norb.llvm.core.Type
import space.norb.llvm.structure.BasicBlock
import space.norb.llvm.structure.Function
import space.norb.llvm.structure.Module
import space.norb.llvm.values.constants.IntConstant
import space.norb.llvm.types.IntegerType
import space.norb.llvm.types.FunctionType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class TerminatorInstTest {
    
    private val voidType = Type.getVoidType()
    private val i1Type = Type.getIntegerType(1) as IntegerType
    private val i32Type = Type.getIntegerType(32) as IntegerType
    
    private fun createTestBasicBlock(name: String): BasicBlock {
        val module = Module("test")
        val functionType = Type.getFunctionType(voidType, emptyList()) as FunctionType
        val function = Function("test", functionType, module)
        return BasicBlock(name, function)
    }
    
    @Test
    fun testUnconditionalBranchInst() {
        val dest = createTestBasicBlock("dest")
        val branch = BranchInst.createUnconditional("branch", voidType, dest)
        
        assertTrue(branch.isUnconditional())
        assertFalse(branch.isConditional())
        assertNull(branch.getCondition())
        assertEquals(dest, branch.getDestination())
        
        val successors = branch.getSuccessors()
        assertEquals(1, successors.size)
        assertEquals(dest, successors[0])
    }
    
    @Test
    fun testConditionalBranchInst() {
        val condition = IntConstant(1L, i1Type)
        val trueDest = createTestBasicBlock("true")
        val falseDest = createTestBasicBlock("false")
        val branch = BranchInst.createConditional("branch", voidType, condition, trueDest, falseDest)
        
        assertFalse(branch.isUnconditional())
        assertTrue(branch.isConditional())
        assertEquals(condition, branch.getCondition())
        assertEquals(trueDest, branch.getTrueDestination())
        assertEquals(falseDest, branch.getFalseDestination())
        
        val successors = branch.getSuccessors()
        assertEquals(2, successors.size)
        assertTrue(successors.contains(trueDest))
        assertTrue(successors.contains(falseDest))
    }
    
    @Test
    fun testSwitchInst() {
        val condition = IntConstant(42L, i32Type)
        val defaultDest = createTestBasicBlock("default")
        val case1Value = IntConstant(1L, i32Type)
        val case1Dest = createTestBasicBlock("case1")
        val case2Value = IntConstant(2L, i32Type)
        val case2Dest = createTestBasicBlock("case2")
        
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
        
        val successors = switch.getSuccessors()
        assertEquals(3, successors.size) // default + 2 cases
        assertTrue(successors.contains(defaultDest))
        assertTrue(successors.contains(case1Dest))
        assertTrue(successors.contains(case2Dest))
    }
    
    @Test
    fun testVoidReturnInst() {
        val ret = ReturnInst.createVoid("ret", voidType)
        
        assertTrue(ret.isFunctionTerminating())
        assertFalse(ret.hasReturnValue())
        assertNull(ret.getReturnValue())
        
        val successors = ret.getSuccessors()
        assertEquals(0, successors.size)
    }
    
    @Test
    fun testValueReturnInst() {
        val returnValue = IntConstant(42L, i32Type)
        val ret = ReturnInst.createWithValue("ret", voidType, returnValue)
        
        assertTrue(ret.isFunctionTerminating())
        assertTrue(ret.hasReturnValue())
        assertEquals(returnValue, ret.getReturnValue())
        
        val successors = ret.getSuccessors()
        assertEquals(0, successors.size)
    }
}