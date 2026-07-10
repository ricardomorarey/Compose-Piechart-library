package com.ricardomorarey.piechart

import java.util.Locale
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * Computes a "nice" Y-axis maximum for [maxValue] so that dividing it into
 * [tickCount] steps produces round tick values (multiples of 1, 2 or 5 times
 * a power of ten). The result is always greater than or equal to [maxValue].
 */
internal fun niceAxisMax(maxValue: Float, tickCount: Int): Float {
    val ticks = tickCount.coerceAtLeast(1)
    if (maxValue <= 0f) return ticks.toFloat()

    val rawStep = maxValue / ticks
    val magnitude = 10.0.pow(floor(log10(rawStep.toDouble()))).toFloat()
    // Comparing the resulting axis maximum directly (instead of the residual
    // step ratio) keeps the >= maxValue guarantee under floating point rounding.
    val niceStep = floatArrayOf(1f, 2f, 5f, 10f, 20f)
        .map { it * magnitude }
        .first { it * ticks >= maxValue }
    return niceStep * ticks
}

/**
 * Formats an axis value compactly: integers without decimals, anything else
 * with a single decimal place.
 */
internal fun formatAxisValue(value: Float): String {
    val rounded = value.roundToInt()
    return if (abs(value - rounded) < 0.005f) {
        rounded.toString()
    } else {
        String.format(Locale.US, "%.1f", value)
    }
}

/**
 * Builds an accessibility description listing each item as "label: value",
 * falling back to the value alone when the label is empty.
 */
internal fun describeValues(items: List<Pair<String, Float>>): String =
    items.joinToString("; ") { (label, value) ->
        if (label.isEmpty()) formatAxisValue(value) else "$label: ${formatAxisValue(value)}"
    }

/**
 * Maps a point inside the plot area to the index of the bar slot under it,
 * or null when the point falls outside the plot area or there are no bars.
 */
internal fun findBarIndexAt(
    x: Float,
    y: Float,
    plotLeft: Float,
    plotRight: Float,
    plotTop: Float,
    plotBottom: Float,
    barCount: Int,
): Int? {
    if (barCount <= 0 || plotRight <= plotLeft) return null
    if (x < plotLeft || x > plotRight || y < plotTop || y > plotBottom) return null

    val slot = ((x - plotLeft) / (plotRight - plotLeft) * barCount).toInt()
    return slot.coerceIn(0, barCount - 1)
}
