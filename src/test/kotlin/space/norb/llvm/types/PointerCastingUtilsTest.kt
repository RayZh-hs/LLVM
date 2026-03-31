package space.norb.llvm.types

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class PointerCastingUtilsTest {

    @Test
    fun `createPointerBitcast returns pointer type for pointer inputs`() {
        val result = PointerCastingUtils.createPointerBitcast(PointerType, PointerType)

        assertEquals(PointerType, result)
    }

    @Test
    fun `createPointerBitcast rejects non-pointer source types`() {
        assertThrows(IllegalArgumentException::class.java) {
            PointerCastingUtils.createPointerBitcast(IntegerType.I32, PointerType)
        }
    }

    @Test
    fun `createPointerBitcast rejects non-pointer target types`() {
        assertThrows(IllegalArgumentException::class.java) {
            PointerCastingUtils.createPointerBitcast(PointerType, IntegerType.I32)
        }
    }
}
