package com.ricardomorarey.piechart

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Test

class ChartDescriptionTest {

    @Test
    fun `values description lists labels and values`() {
        val description = describeValues(listOf("Ene" to 12f, "Feb" to 30.5f))
        assertEquals("Ene: 12; Feb: 30.5", description)
    }

    @Test
    fun `empty labels fall back to the value`() {
        assertEquals("7", describeValues(listOf("" to 7f)))
    }

    @Test
    fun `empty data produces an empty description`() {
        assertEquals("", describeValues(emptyList()))
    }

    @Test
    fun `pie description uses percentages`() {
        val arcs = computeArcs(
            slices = listOf(
                PieSlice(value = 3f, color = Color.Black, label = "A"),
                PieSlice(value = 1f, color = Color.Black, label = "B"),
            ),
            startAngleDegrees = -90f,
            spacingDegrees = 0f,
        )

        assertEquals("A: 75%; B: 25%", describeArcs(arcs))
    }

    @Test
    fun `pie description falls back to the value when the label is empty`() {
        val arcs = computeArcs(
            slices = listOf(PieSlice(value = 5f, color = Color.Black)),
            startAngleDegrees = -90f,
            spacingDegrees = 0f,
        )

        assertEquals("5: 100%", describeArcs(arcs))
    }
}
