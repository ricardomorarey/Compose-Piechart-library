package com.ricardomorarey.piechart

import kotlin.math.atan2
import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * A slice resolved to a concrete arc on screen.
 *
 * Angles are expressed in degrees using the screen convention:
 * 0° points to 3 o'clock and positive values go clockwise.
 */
internal data class ArcSpec(
    val slice: PieSlice,
    val startAngle: Float,
    val sweepAngle: Float,
    val fraction: Float,
)

/**
 * Converts a list of slices into arcs, distributing 360° proportionally to
 * each slice value and reserving [spacingDegrees] between consecutive slices.
 *
 * Slices with non-positive values are skipped. Returns an empty list when
 * there is nothing to draw.
 */
internal fun computeArcs(
    slices: List<PieSlice>,
    startAngleDegrees: Float,
    spacingDegrees: Float,
): List<ArcSpec> {
    val visible = slices.filter { it.value > 0f }
    if (visible.isEmpty()) return emptyList()

    val total = visible.fold(0.0) { acc, slice -> acc + slice.value }.toFloat()
    if (total <= 0f) return emptyList()

    val spacing = if (visible.size > 1) {
        spacingDegrees.coerceIn(0f, 360f / visible.size)
    } else {
        0f
    }
    val available = 360f - spacing * visible.size

    var current = startAngleDegrees + spacing / 2f
    return visible.map { slice ->
        val fraction = slice.value / total
        val sweep = available * fraction
        val arc = ArcSpec(slice, current, sweep, fraction)
        current += sweep + spacing
        arc
    }
}

/**
 * Builds an accessibility description listing each slice as "label: N%",
 * falling back to the raw value when the label is empty.
 */
internal fun describeArcs(arcs: List<ArcSpec>): String =
    arcs.joinToString("; ") { arc ->
        val percent = (arc.fraction * 100f).roundToInt()
        val name = arc.slice.label.ifEmpty { formatAxisValue(arc.slice.value) }
        "$name: $percent%"
    }

/**
 * Finds the arc under a point given by ([dx], [dy]) relative to the chart
 * center, or null when the point falls outside the ring or in a gap.
 */
internal fun findArcAt(
    dx: Float,
    dy: Float,
    outerRadius: Float,
    innerRadius: Float,
    arcs: List<ArcSpec>,
): ArcSpec? {
    val distance = sqrt(dx * dx + dy * dy)
    if (distance > outerRadius || distance < innerRadius) return null

    val angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
    return arcs.firstOrNull { arc ->
        val relative = normalizeDegrees(angle - arc.startAngle)
        relative < arc.sweepAngle
    }
}

/** Normalizes an angle in degrees to the range [0, 360). */
internal fun normalizeDegrees(degrees: Float): Float {
    val remainder = degrees % 360f
    return if (remainder < 0f) remainder + 360f else remainder
}
