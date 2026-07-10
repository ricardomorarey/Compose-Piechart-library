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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import kotlin.math.min

/**
 * An animated vertical bar chart with X and Y axes.
 *
 * The Y axis is divided into round-valued ticks computed from the data (or a
 * fixed maximum via [BarChartStyle.yAxisMax]); the X axis shows one label per
 * bar. Bars grow from the baseline when the chart first appears or when
 * [entries] change.
 *
 * @param entries The bars to display, in order. Negative values are drawn as zero.
 * @param modifier Modifier for the chart. Give it a size, e.g.
 * `Modifier.fillMaxWidth().height(220.dp)`.
 * @param style Visual configuration; see [BarChartStyle].
 * @param animationSpec Animation used for the growth of the bars.
 * @param onBarClick Invoked with the entry whose column is tapped. Taps outside
 * the plot area are ignored.
 */
@Composable
public fun BarChart(
    entries: List<BarEntry>,
    modifier: Modifier = Modifier,
    style: BarChartStyle = BarChartStyle(),
    animationSpec: AnimationSpec<Float> = tween(durationMillis = 900, easing = FastOutSlowInEasing),
    onBarClick: ((BarEntry) -> Unit)? = null,
) {
    val progress = remember { Animatable(0f) }
    LaunchedEffect(entries) {
        progress.snapTo(0f)
        progress.animateTo(1f, animationSpec)
    }

    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val tickCount = style.yTickCount.coerceAtLeast(1)

    val maxValue = entries.maxOfOrNull { it.value }?.coerceAtLeast(0f) ?: 0f
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

    val inputModifier = if (onBarClick != null) {
        Modifier.pointerInput(entries, leftGutter, bottomGutter, plotTop) {
            detectTapGestures { tap ->
                val index = findBarIndexAt(
                    x = tap.x,
                    y = tap.y,
                    plotLeft = leftGutter,
                    plotRight = size.width.toFloat(),
                    plotTop = plotTop,
                    plotBottom = size.height - bottomGutter,
                    barCount = entries.size,
                )
                if (index != null) onBarClick(entries[index])
            }
        }
    } else {
        Modifier
    }

    Canvas(modifier = modifier.then(inputModifier)) {
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

        if (entries.isEmpty()) return@Canvas

        // Bars and X-axis labels.
        val slotWidth = (plotRight - plotLeft) / entries.size
        val barWidth = slotWidth * (1f - style.barSpacingRatio.coerceIn(0f, 0.9f))
        val cornerRadius = style.barCornerRadius.toPx()

        entries.forEachIndexed { index, entry ->
            val slotLeft = plotLeft + index * slotWidth
            val barLeft = slotLeft + (slotWidth - barWidth) / 2f
            val fraction = (entry.value.coerceAtLeast(0f) / axisMax).coerceAtMost(1f)
            val barHeight = fraction * plotHeight * progress.value

            if (barHeight > 0f) {
                val radius = min(cornerRadius, min(barWidth / 2f, barHeight))
                val path = Path().apply {
                    addRoundRect(
                        RoundRect(
                            rect = Rect(barLeft, plotBottom - barHeight, barLeft + barWidth, plotBottom),
                            topLeft = CornerRadius(radius),
                            topRight = CornerRadius(radius),
                        )
                    )
                }
                drawPath(path, color = entry.color ?: style.barColor)
            }

            if (style.showXAxisLabels && entry.label.isNotEmpty()) {
                val layout = textMeasurer.measure(
                    text = AnnotatedString(entry.label),
                    style = style.axisTextStyle,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    constraints = Constraints(maxWidth = slotWidth.toInt().coerceAtLeast(1)),
                )
                drawText(
                    textLayoutResult = layout,
                    topLeft = Offset(
                        x = slotLeft + (slotWidth - layout.size.width) / 2f,
                        y = plotBottom + xLabelGap,
                    ),
                )
            }
        }
    }
}
