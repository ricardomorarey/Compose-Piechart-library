package com.ricardomorarey.piechart

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * A single slice of a [PieChart].
 *
 * @param value The magnitude of the slice. Must be positive to be rendered;
 * slices with a value of zero or less are skipped.
 * @param color The fill color of the slice.
 * @param label An optional label identifying the slice (useful for legends
 * and click handling).
 */
@Immutable
data class PieSlice(
    val value: Float,
    val color: Color,
    val label: String = "",
)
