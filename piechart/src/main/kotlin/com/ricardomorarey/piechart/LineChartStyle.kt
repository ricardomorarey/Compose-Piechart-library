package com.ricardomorarey.piechart

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Visual configuration for a [LineChart].
 *
 * @param lineColor Color of the line.
 * @param lineWidth Stroke width of the line.
 * @param smooth Whether to draw the line as a smooth curve instead of straight segments.
 * @param showPoints Whether to draw a dot at each data point.
 * @param pointRadius Radius of the point dots.
 * @param pointColor Color of the point dots; when null, [lineColor] is used.
 * @param fillArea Whether to fill the area between the line and the baseline.
 * @param areaColor Fill color for the area; when null, [lineColor] at 20% opacity is used.
 * @param yTickCount Number of divisions on the Y axis (the axis shows
 * `yTickCount + 1` values, including zero).
 * @param yAxisMax Fixed maximum for the Y axis; when null, a "nice" rounded
 * maximum is computed from the data.
 * @param showGridLines Whether to draw horizontal grid lines at each Y tick.
 * @param showYAxisValues Whether to draw the numeric values on the Y axis.
 * @param showXAxisLabels Whether to draw the point labels on the X axis.
 * @param axisColor Color of the X and Y axis lines.
 * @param gridColor Color of the horizontal grid lines.
 * @param axisTextStyle Text style used for axis values and labels.
 */
@Immutable
data class LineChartStyle(
    val lineColor: Color = Color(0xFF4E79A7),
    val lineWidth: Dp = 2.dp,
    val smooth: Boolean = false,
    val showPoints: Boolean = true,
    val pointRadius: Dp = 4.dp,
    val pointColor: Color? = null,
    val fillArea: Boolean = false,
    val areaColor: Color? = null,
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
