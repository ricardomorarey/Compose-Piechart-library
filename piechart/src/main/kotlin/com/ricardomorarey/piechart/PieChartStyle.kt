package com.ricardomorarey.piechart

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Visual configuration for a [PieChart].
 *
 * @param holeRatio Fraction of the radius left empty in the middle,
 * between 0 (full pie) and 0.95 (thin donut ring).
 * @param sliceSpacingDegrees Angular gap between consecutive slices, in degrees.
 * @param startAngleDegrees Angle at which the first slice starts. Angles follow
 * the screen convention: 0° points to 3 o'clock and positive values go clockwise,
 * so the default of -90° starts at 12 o'clock.
 * @param showPercentageLabels Whether to draw the percentage of each slice on top of it.
 * @param minLabelSweepDegrees Slices thinner than this angle do not get a label,
 * avoiding overlapping text on tiny slices.
 * @param labelTextStyle Text style used for percentage labels.
 */
@Immutable
data class PieChartStyle(
    val holeRatio: Float = 0f,
    val sliceSpacingDegrees: Float = 0f,
    val startAngleDegrees: Float = -90f,
    val showPercentageLabels: Boolean = false,
    val minLabelSweepDegrees: Float = 15f,
    val labelTextStyle: TextStyle = TextStyle(
        color = Color.White,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
    ),
)

/**
 * Convenience defaults for [PieChart].
 */
object PieChartDefaults {

    /**
     * A colorblind-friendly categorical palette. Use [colorFor] to pick a
     * color for the n-th slice, cycling when there are more slices than colors.
     */
    val Palette: List<Color> = listOf(
        Color(0xFF4E79A7),
        Color(0xFFF28E2B),
        Color(0xFF59A14F),
        Color(0xFFE15759),
        Color(0xFFB07AA1),
        Color(0xFF76B7B2),
        Color(0xFFEDC948),
        Color(0xFF9C755F),
    )

    /** Returns a palette color for the slice at [index], cycling through [Palette]. */
    fun colorFor(index: Int): Color = Palette[index % Palette.size]
}
