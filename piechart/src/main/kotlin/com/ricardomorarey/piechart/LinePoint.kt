package com.ricardomorarey.piechart

import androidx.compose.runtime.Immutable

/**
 * A single point of a [LineChart].
 *
 * @param value The Y value of the point. Negative values are drawn as zero.
 * @param label The label shown under the point on the X axis.
 */
@Immutable
data class LinePoint(
    val value: Float,
    val label: String = "",
)
