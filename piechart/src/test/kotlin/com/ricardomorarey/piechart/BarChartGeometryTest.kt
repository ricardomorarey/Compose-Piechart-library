package com.ricardomorarey.piechart

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BarChartGeometryTest {

    @Test
    fun `nice axis max is round and covers the data`() {
        // 87 / 5 ticks -> raw step 17.4 -> nice step 20 -> max 100.
        assertEquals(100f, niceAxisMax(87f, 5), 0.001f)
        // 4 / 4 ticks -> step 1 -> max 4.
        assertEquals(4f, niceAxisMax(4f, 4), 0.001f)
        // 0.35 / 5 ticks -> raw step 0.07 -> nice step 0.1 -> max 0.5.
        assertEquals(0.5f, niceAxisMax(0.35f, 5), 0.001f)
    }

    @Test
    fun `nice axis max is never below the data maximum`() {
        val values = listOf(0.1f, 1f, 7f, 42f, 99f, 100f, 101f, 12345f)
        values.forEach { value ->
            assertTrue(
                "axis max for $value should cover it",
                niceAxisMax(value, 5) >= value,
            )
        }
    }

    @Test
    fun `nice axis max falls back for non positive data`() {
        assertEquals(5f, niceAxisMax(0f, 5), 0.001f)
        assertEquals(5f, niceAxisMax(-10f, 5), 0.001f)
    }

    @Test
    fun `axis values are formatted compactly`() {
        assertEquals("20", formatAxisValue(20f))
        assertEquals("0", formatAxisValue(0f))
        assertEquals("2.5", formatAxisValue(2.5f))
        assertEquals("100", formatAxisValue(99.999f))
    }

    @Test
    fun `bar hit test maps x position to slot index`() {
        // Plot from x=100 to x=500 with 4 bars: slots of width 100.
        assertEquals(0, findBarIndexAt(150f, 50f, 100f, 500f, 0f, 100f, 4))
        assertEquals(1, findBarIndexAt(250f, 50f, 100f, 500f, 0f, 100f, 4))
        assertEquals(3, findBarIndexAt(499f, 50f, 100f, 500f, 0f, 100f, 4))
    }

    @Test
    fun `bar hit test returns null outside the plot area`() {
        assertNull(findBarIndexAt(50f, 50f, 100f, 500f, 0f, 100f, 4))
        assertNull(findBarIndexAt(600f, 50f, 100f, 500f, 0f, 100f, 4))
        assertNull(findBarIndexAt(250f, 150f, 100f, 500f, 0f, 100f, 4))
        assertNull(findBarIndexAt(250f, 50f, 100f, 500f, 0f, 100f, 0))
    }
}
