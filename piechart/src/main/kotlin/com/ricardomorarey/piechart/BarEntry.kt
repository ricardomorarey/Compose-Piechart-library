package com.ricardomorarey.piechart

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * A single bar of a [BarChart].
 *
 * @param value The height of the bar. Negative values are drawn as zero.
 * @param label The label shown under the bar on the X axis.
 * @param color Optional color for this bar; when null, [BarChartStyle.barColor] is used.
 */
@Immutable
data class BarEntry(
    val value: Float,
    val label: String = "",
    val color: Color? = null,
)
