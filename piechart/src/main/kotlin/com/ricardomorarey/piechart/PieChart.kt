package com.ricardomorarey.piechart

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * An animated pie or donut chart.
 *
 * The chart distributes a full turn proportionally to each slice value and
 * animates the sweep from the start angle when it first appears or when
 * [slices] change.
 *
 * @param slices The data to display. Slices with non-positive values are skipped.
 * @param modifier Modifier for the chart. Give it a size (e.g. `Modifier.size(200.dp)`);
 * the chart draws centered inside the smallest square that fits.
 * @param style Visual configuration; see [PieChartStyle]. Use `holeRatio` above 0
 * to render a donut instead of a full pie.
 * @param animationSpec Animation used for the initial sweep.
 * @param onSliceClick Invoked with the slice under the finger when the chart is
 * tapped. Taps on the hole or outside the ring are ignored.
 */
@Composable
public fun PieChart(
    slices: List<PieSlice>,
    modifier: Modifier = Modifier,
    style: PieChartStyle = PieChartStyle(),
    animationSpec: AnimationSpec<Float> = tween(durationMillis = 900, easing = FastOutSlowInEasing),
    onSliceClick: ((PieSlice) -> Unit)? = null,
) {
    val arcs = remember(slices, style.startAngleDegrees, style.sliceSpacingDegrees) {
        computeArcs(slices, style.startAngleDegrees, style.sliceSpacingDegrees)
    }

    val progress = remember { Animatable(0f) }
    LaunchedEffect(arcs) {
        progress.snapTo(0f)
        progress.animateTo(1f, animationSpec)
    }

    val textMeasurer = rememberTextMeasurer()
    val holeRatio = style.holeRatio.coerceIn(0f, 0.95f)

    val inputModifier = if (onSliceClick != null) {
        Modifier.pointerInput(arcs, holeRatio) {
            detectTapGestures { tap ->
                val outerRadius = min(size.width, size.height) / 2f
                val innerRadius = outerRadius * holeRatio
                val arc = findArcAt(
                    dx = tap.x - size.width / 2f,
                    dy = tap.y - size.height / 2f,
                    outerRadius = outerRadius,
                    innerRadius = innerRadius,
                    arcs = arcs,
                )
                if (arc != null) onSliceClick(arc.slice)
            }
        }
    } else {
        Modifier
    }

    Canvas(modifier = modifier.then(inputModifier)) {
        if (arcs.isEmpty()) return@Canvas

        val outerRadius = min(this.size.width, this.size.height) / 2f
        val innerRadius = outerRadius * holeRatio
        val ringWidth = outerRadius - innerRadius
        val arcRadius = innerRadius + ringWidth / 2f
        val arcTopLeft = Offset(center.x - arcRadius, center.y - arcRadius)
        val arcSize = Size(arcRadius * 2f, arcRadius * 2f)

        arcs.forEach { arc ->
            drawArc(
                color = arc.slice.color,
                startAngle = arc.startAngle,
                sweepAngle = arc.sweepAngle * progress.value,
                useCenter = false,
                topLeft = arcTopLeft,
                size = arcSize,
                style = Stroke(width = ringWidth, cap = StrokeCap.Butt),
            )
        }

        if (style.showPercentageLabels) {
            val labelRadius = (innerRadius + outerRadius) / 2f
            arcs.forEach { arc ->
                if (arc.sweepAngle >= style.minLabelSweepDegrees) {
                    drawPercentageLabel(
                        textMeasurer = textMeasurer,
                        arc = arc,
                        labelRadius = labelRadius,
                        style = style,
                        alpha = progress.value,
                    )
                }
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawPercentageLabel(
    textMeasurer: TextMeasurer,
    arc: ArcSpec,
    labelRadius: Float,
    style: PieChartStyle,
    alpha: Float,
) {
    val text = "${(arc.fraction * 100f).roundToInt()}%"
    val midAngleRadians = Math.toRadians((arc.startAngle + arc.sweepAngle / 2f).toDouble())
    val labelCenter = Offset(
        x = center.x + labelRadius * cos(midAngleRadians).toFloat(),
        y = center.y + labelRadius * sin(midAngleRadians).toFloat(),
    )
    val textStyle = style.labelTextStyle.copy(
        color = style.labelTextStyle.color.copy(alpha = style.labelTextStyle.color.alpha * alpha),
    )
    val layout = textMeasurer.measure(AnnotatedString(text), textStyle)
    drawText(
        textLayoutResult = layout,
        topLeft = Offset(
            x = labelCenter.x - layout.size.width / 2f,
            y = labelCenter.y - layout.size.height / 2f,
        ),
    )
}
