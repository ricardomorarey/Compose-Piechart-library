package com.ricardomorarey.piechart

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Visual configuration for a [BarChart].
 *
 * @param barColor Default fill color for bars without a [BarEntry.color].
 * @param barCornerRadius Corner radius applied to the top corners of each bar.
 * @param barSpacingRatio Fraction of each bar slot left empty around the bar,
 * between 0 (bars touch each other) and 0.9.
 * @param barMaxWidth Absolute maximum width for each bar; when null, the width
 * is only limited by [barSpacingRatio]. Useful to keep bars slim when the
 * chart is wide and has few bars.
 * @param showValues Whether to draw the numeric value of each bar above it.
 * @param valueTextStyle Text style used for the values above the bars.
 * @param dimmedAlpha Opacity applied to the bars that are NOT selected when a
 * `selectedIndex` is passed to the chart, between 0 and 1.
 * @param yTickCount Number of divisions on the Y axis (the axis shows
 * `yTickCount + 1` values, including zero).
 * @param yAxisMax Fixed maximum for the Y axis; when null, a "nice" rounded
 * maximum is computed from the data.
 * @param showGridLines Whether to draw horizontal grid lines at each Y tick.
 * @param showYAxisValues Whether to draw the numeric values on the Y axis.
 * @param showXAxisLabels Whether to draw the bar labels on the X axis.
 * @param axisColor Color of the X and Y axis lines.
 * @param gridColor Color of the horizontal grid lines.
 * @param axisTextStyle Text style used for axis values and labels.
 */
@Immutable
data class BarChartStyle(
    val barColor: Color = Color(0xFF4E79A7),
    val barCornerRadius: Dp = 4.dp,
    val barSpacingRatio: Float = 0.3f,
    val barMaxWidth: Dp? = null,
    val showValues: Boolean = false,
    val valueTextStyle: TextStyle = TextStyle(
        color = Color(0xFF757575),
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
    ),
    val dimmedAlpha: Float = 0.35f,
    val yTickCount: Int = 5,
    val yAxisMax: Float? = null,
    val showGridLines: Boolean = true,
    val showYAxisValues: Boolean = true,
    val showXAxisLabels: Boolean = true,
    val axisColor: Color = Color(0xFF9E9E9E),
    val gridColor: Color = Color(0x339E9E9E),
    val axisTextStyle: TextStyle = TextStyle(
        color = Color(0xFF757575),
        fontSize = 11.sp,
    ),
)
