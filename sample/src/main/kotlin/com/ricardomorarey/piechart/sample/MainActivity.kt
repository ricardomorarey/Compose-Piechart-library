package com.ricardomorarey.piechart.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ricardomorarey.piechart.BarChart
import com.ricardomorarey.piechart.BarChartStyle
import com.ricardomorarey.piechart.BarEntry
import com.ricardomorarey.piechart.LineChart
import com.ricardomorarey.piechart.LineChartStyle
import com.ricardomorarey.piechart.LinePoint
import com.ricardomorarey.piechart.PieChart
import com.ricardomorarey.piechart.PieChartDefaults
import com.ricardomorarey.piechart.PieChartStyle
import com.ricardomorarey.piechart.PieSlice

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SampleScreen()
                }
            }
        }
    }
}

@Composable
fun SampleScreen() {
    val slices = remember {
        listOf(
            PieSlice(value = 40f, color = PieChartDefaults.colorFor(0), label = "Kotlin"),
            PieSlice(value = 25f, color = PieChartDefaults.colorFor(1), label = "Java"),
            PieSlice(value = 20f, color = PieChartDefaults.colorFor(2), label = "C++"),
            PieSlice(value = 15f, color = PieChartDefaults.colorFor(3), label = "Otros"),
        )
    }
    var selected by remember { mutableStateOf<PieSlice?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Text(text = "Pie chart", style = MaterialTheme.typography.titleMedium)
        PieChart(
            slices = slices,
            modifier = Modifier.size(220.dp),
            style = PieChartStyle(showPercentageLabels = true),
            onSliceClick = { selected = it },
        )

        Text(text = "Donut chart", style = MaterialTheme.typography.titleMedium)
        PieChart(
            slices = slices,
            modifier = Modifier.size(220.dp),
            style = PieChartStyle(
                holeRatio = 0.6f,
                sliceSpacingDegrees = 2f,
            ),
            onSliceClick = { selected = it },
        )

        Text(
            text = selected?.let { "Seleccionado: ${it.label} (${it.value})" }
                ?: "Toca una porción para seleccionarla",
            style = MaterialTheme.typography.bodyMedium,
        )

        Legend(slices)

        Text(text = "Bar chart", style = MaterialTheme.typography.titleMedium)
        val bars = remember {
            listOf(
                BarEntry(value = 12f, label = "Ene"),
                BarEntry(value = 30f, label = "Feb"),
                BarEntry(value = 22f, label = "Mar"),
                BarEntry(value = 45f, label = "Abr"),
                BarEntry(value = 28f, label = "May"),
                BarEntry(value = 37f, label = "Jun"),
            )
        }
        var selectedBar by remember { mutableStateOf<BarEntry?>(null) }
        BarChart(
            entries = bars,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            style = BarChartStyle(
                barColor = PieChartDefaults.colorFor(0),
                showValues = true,
                barMaxWidth = 48.dp,
            ),
            selectedIndex = selectedBar?.let { bar -> bars.indexOf(bar).takeIf { it >= 0 } },
            onBarClick = { selectedBar = if (selectedBar == it) null else it },
        )
        Text(
            text = selectedBar?.let { "Seleccionado: ${it.label} (${it.value})" }
                ?: "Toca una barra para seleccionarla",
            style = MaterialTheme.typography.bodyMedium,
        )

        Text(text = "Line chart", style = MaterialTheme.typography.titleMedium)
        val linePoints = remember {
            listOf(
                LinePoint(value = 12f, label = "Ene"),
                LinePoint(value = 30f, label = "Feb"),
                LinePoint(value = 22f, label = "Mar"),
                LinePoint(value = 45f, label = "Abr"),
                LinePoint(value = 28f, label = "May"),
                LinePoint(value = 37f, label = "Jun"),
            )
        }
        var selectedPoint by remember { mutableStateOf<LinePoint?>(null) }
        LineChart(
            points = linePoints,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            style = LineChartStyle(
                lineColor = PieChartDefaults.colorFor(2),
                smooth = true,
                fillArea = true,
            ),
            onPointClick = { selectedPoint = it },
        )
        Text(
            text = selectedPoint?.let { "Seleccionado: ${it.label} (${it.value})" }
                ?: "Toca un punto para seleccionarlo",
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun Legend(slices: List<PieSlice>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        slices.forEach { slice ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(slice.color, CircleShape),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = slice.label, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
