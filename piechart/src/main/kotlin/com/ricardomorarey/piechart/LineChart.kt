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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp

/**
 * An animated line chart with X and Y axes.
 *
 * Points are placed at evenly spaced positions along the X axis, one per
 * entry, with the Y axis divided into round-valued ticks computed from the
 * data (or a fixed maximum via [LineChartStyle.yAxisMax]). The line is
 * revealed from left to right when the chart first appears or when [points]
 * change.
 *
 * @param points The data points to display, in order. Negative values are drawn as zero.
 * @param modifier Modifier for the chart. Give it a size, e.g.
 * `Modifier.fillMaxWidth().height(220.dp)`.
 * @param style Visual configuration; see [LineChartStyle].
 * @param animationSpec Animation used for the left-to-right reveal.
 * @param contentDescription Description for accessibility services; when null,
 * one is generated from the point labels and values.
 * @param onPointClick Invoked with the point whose column is tapped. Taps
 * outside the plot area are ignored.
 */
@Composable
public fun LineChart(
    points: List<LinePoint>,
    modifier: Modifier = Modifier,
    style: LineChartStyle = LineChartStyle(),
    animationSpec: AnimationSpec<Float> = tween(durationMillis = 900, easing = FastOutSlowInEasing),
    contentDescription: String? = null,
    onPointClick: ((LinePoint) -> Unit)? = null,
) {
    val progress = remember { Animatable(0f) }
    LaunchedEffect(points) {
        progress.snapTo(0f)
        progress.animateTo(1f, animationSpec)
    }

    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val tickCount = style.yTickCount.coerceAtLeast(1)

    val maxValue = points.maxOfOrNull { it.value }?.coerceAtLeast(0f) ?: 0f
    val axisMax = style.yAxisMax?.takeIf { it > 0f } ?: niceAxisMax(maxValue, tickCount)
    val tickValues = remember(axisMax, tickCount) {
        (0..tickCount).map { axisMax * it / tickCount }
    }

    val yLabels = remember(tickValues, style.axisTextStyle, style.showYAxisValues) {
        if (style.showYAxisValues) {
            tickValues.map { textMeasurer.measure(AnnotatedString(formatAxisValue(it)), style.axisTextStyle) }
        } else {
            emptyList()
        }
    }

    val labelPadding = with(density) { 8.dp.toPx() }
    val xLabelGap = with(density) { 4.dp.toPx() }
    val labelHeight = yLabels.firstOrNull()?.size?.height?.toFloat()
        ?: textMeasurer.measure(AnnotatedString("0"), style.axisTextStyle).size.height.toFloat()
    val leftGutter = if (yLabels.isEmpty()) 0f else yLabels.maxOf { it.size.width } + labelPadding
    val bottomGutter = if (style.showXAxisLabels) labelHeight + xLabelGap else 0f
    val plotTop = labelHeight / 2f

    val description = contentDescription
        ?: remember(points) { describeValues(points.map { it.label to it.value }) }

    val inputModifier = if (onPointClick != null) {
        Modifier.pointerInput(points, leftGutter, bottomGutter, plotTop) {
            detectTapGestures { tap ->
                val index = findBarIndexAt(
                    x = tap.x,
                    y = tap.y,
                    plotLeft = leftGutter,
                    plotRight = size.width.toFloat(),
                    plotTop = plotTop,
                    plotBottom = size.height - bottomGutter,
                    barCount = points.size,
                )
                if (index != null) onPointClick(points[index])
            }
        }
    } else {
        Modifier
    }

    Canvas(
        modifier = modifier
            .then(inputModifier)
            .semantics { this.contentDescription = description },
    ) {
        val plotLeft = leftGutter
        val plotRight = this.size.width
        val plotBottom = this.size.height - bottomGutter
        val plotHeight = plotBottom - plotTop
        if (plotHeight <= 0f || plotRight <= plotLeft) return@Canvas

        val thinLine = 1.dp.toPx()

        // Grid lines and Y-axis values.
        tickValues.forEachIndexed { index, tick ->
            val y = plotBottom - (tick / axisMax) * plotHeight
            if (style.showGridLines && index > 0) {
                drawLine(
                    color = style.gridColor,
                    start = Offset(plotLeft, y),
                    end = Offset(plotRight, y),
                    strokeWidth = thinLine,
                )
            }
            yLabels.getOrNull(index)?.let { layout ->
                drawText(
                    textLayoutResult = layout,
                    topLeft = Offset(
                        x = plotLeft - labelPadding - layout.size.width,
                        y = y - layout.size.height / 2f,
                    ),
                )
            }
        }

        // Axis lines.
        drawLine(
            color = style.axisColor,
            start = Offset(plotLeft, plotTop),
            end = Offset(plotLeft, plotBottom),
            strokeWidth = thinLine,
        )
        drawLine(
            color = style.axisColor,
            start = Offset(plotLeft, plotBottom),
            end = Offset(plotRight, plotBottom),
            strokeWidth = thinLine,
        )

        if (points.isEmpty()) return@Canvas

        val slotWidth = (plotRight - plotLeft) / points.size
        val xs = List(points.size) { plotLeft + slotWidth * (it + 0.5f) }
        val ys = points.map { point ->
            plotBottom - (point.value.coerceAtLeast(0f) / axisMax).coerceAtMost(1f) * plotHeight
        }

        // Line, area and points, revealed from left to right.
        val revealRight = plotLeft + (plotRight - plotLeft) * progress.value
        clipRect(left = plotLeft, top = 0f, right = revealRight, bottom = this.size.height) {
            if (points.size > 1) {
                val linePath = Path().apply {
                    moveTo(xs[0], ys[0])
                    for (i in 1 until points.size) {
                        if (style.smooth) {
                            val midX = (xs[i - 1] + xs[i]) / 2f
                            cubicTo(midX, ys[i - 1], midX, ys[i], xs[i], ys[i])
                        } else {
                            lineTo(xs[i], ys[i])
                        }
                    }
                }

                if (style.fillArea) {
                    val areaPath = Path().apply {
                        addPath(linePath)
                        lineTo(xs.last(), plotBottom)
                        lineTo(xs.first(), plotBottom)
                        close()
                    }
                    drawPath(areaPath, color = style.areaColor ?: style.lineColor.copy(alpha = 0.2f))
                }

                drawPath(
                    path = linePath,
                    color = style.lineColor,
                    style = Stroke(
                        width = style.lineWidth.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round,
                    ),
                )
            }

            if (style.showPoints) {
                val pointColor = style.pointColor ?: style.lineColor
                val radius = style.pointRadius.toPx()
                xs.forEachIndexed { i, x ->
                    drawCircle(color = pointColor, radius = radius, center = Offset(x, ys[i]))
                }
            }
        }

        // X-axis labels.
        if (style.showXAxisLabels) {
            points.forEachIndexed { index, point ->
                if (point.label.isNotEmpty()) {
                    val layout = textMeasurer.measure(
                        text = AnnotatedString(point.label),
                        style = style.axisTextStyle,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        constraints = Constraints(maxWidth = slotWidth.toInt().coerceAtLeast(1)),
                    )
                    drawText(
                        textLayoutResult = layout,
                        topLeft = Offset(
                            x = plotLeft + index * slotWidth + (slotWidth - layout.size.width) / 2f,
                            y = plotBottom + xLabelGap,
                        ),
                    )
                }
            }
        }
    }
}
