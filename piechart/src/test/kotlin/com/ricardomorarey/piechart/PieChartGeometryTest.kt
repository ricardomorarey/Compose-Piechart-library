package com.ricardomorarey.piechart

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PieChartGeometryTest {

    private fun slice(value: Float, label: String = "") =
        PieSlice(value = value, color = Color.Black, label = label)

    @Test
    fun `sweep angles are proportional and add up to 360 without spacing`() {
        val arcs = computeArcs(
            slices = listOf(slice(1f), slice(1f), slice(2f)),
            startAngleDegrees = -90f,
            spacingDegrees = 0f,
        )

        assertEquals(3, arcs.size)
        assertEquals(90f, arcs[0].sweepAngle, 0.001f)
        assertEquals(90f, arcs[1].sweepAngle, 0.001f)
        assertEquals(180f, arcs[2].sweepAngle, 0.001f)
        assertEquals(360f, arcs.fold(0f) { acc, arc -> acc + arc.sweepAngle }, 0.001f)
    }

    @Test
    fun `spacing is reserved between slices`() {
        val spacing = 4f
        val arcs = computeArcs(
            slices = listOf(slice(1f), slice(1f)),
            startAngleDegrees = 0f,
            spacingDegrees = spacing,
        )

        val totalSweep = arcs.fold(0f) { acc, arc -> acc + arc.sweepAngle }
        assertEquals(360f - spacing * arcs.size, totalSweep, 0.001f)
    }

    @Test
    fun `single slice ignores spacing and fills the whole turn`() {
        val arcs = computeArcs(
            slices = listOf(slice(5f)),
            startAngleDegrees = -90f,
            spacingDegrees = 10f,
        )

        assertEquals(1, arcs.size)
        assertEquals(360f, arcs[0].sweepAngle, 0.001f)
    }

    @Test
    fun `non positive slices are skipped`() {
        val arcs = computeArcs(
            slices = listOf(slice(0f), slice(-3f), slice(2f, "visible")),
            startAngleDegrees = 0f,
            spacingDegrees = 0f,
        )

        assertEquals(1, arcs.size)
        assertEquals("visible", arcs[0].slice.label)
    }

    @Test
    fun `empty input produces no arcs`() {
        assertTrue(computeArcs(emptyList(), 0f, 0f).isEmpty())
    }

    @Test
    fun `hit test finds the slice under the point`() {
        val arcs = computeArcs(
            slices = listOf(slice(1f, "right"), slice(1f, "left")),
            startAngleDegrees = -90f,
            spacingDegrees = 0f,
        )

        // Point at 3 o'clock, halfway out: belongs to the first slice.
        val hitRight = findArcAt(dx = 50f, dy = 0f, outerRadius = 100f, innerRadius = 0f, arcs = arcs)
        assertEquals("right", hitRight?.slice?.label)

        // Point at 9 o'clock: belongs to the second slice.
        val hitLeft = findArcAt(dx = -50f, dy = 0f, outerRadius = 100f, innerRadius = 0f, arcs = arcs)
        assertEquals("left", hitLeft?.slice?.label)
    }

    @Test
    fun `hit test returns null outside the ring`() {
        val arcs = computeArcs(listOf(slice(1f)), startAngleDegrees = -90f, spacingDegrees = 0f)

        assertNull(findArcAt(dx = 200f, dy = 0f, outerRadius = 100f, innerRadius = 0f, arcs = arcs))
        assertNull(findArcAt(dx = 10f, dy = 0f, outerRadius = 100f, innerRadius = 40f, arcs = arcs))
    }

    @Test
    fun `normalizeDegrees maps angles into 0 until 360`() {
        assertEquals(0f, normalizeDegrees(0f), 0.001f)
        assertEquals(0f, normalizeDegrees(360f), 0.001f)
        assertEquals(270f, normalizeDegrees(-90f), 0.001f)
        assertEquals(45f, normalizeDegrees(405f), 0.001f)
    }
}
