# Compose PieChart Library

[![](https://jitpack.io/v/ricardomorarey/Compose-Piechart-library.svg)](https://jitpack.io/#ricardomorarey/Compose-Piechart-library)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

A lightweight, animated chart library for **Jetpack Compose** — **pie / donut charts** and **bar charts with X/Y axes** — written 100% in Kotlin with no third-party dependencies.

## Features

**PieChart**
- 🥧 Pie and donut modes (configurable hole ratio)
- 🎬 Animated sweep on first display and on data changes
- 👆 Click handling per slice
- 🏷️ Optional percentage labels (auto-hidden on tiny slices)
- ↔️ Configurable start angle and spacing between slices

**BarChart**
- 📊 Vertical bars with X and Y axes
- 📏 Automatic "nice" rounded Y-axis scale (or a fixed maximum)
- 🌫️ Optional grid lines, axis values and per-bar labels (with ellipsis)
- 🎬 Animated bar growth and click handling per bar
- 🎨 Per-bar colors and rounded corners

**Both**
- 🎨 Built-in colorblind-friendly palette
- 📦 Min SDK 21, no dependencies beyond Compose itself

## Installation

Add JitPack to your repositories (`settings.gradle.kts`):

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}
```

Add the dependency:

```kotlin
dependencies {
    implementation("com.github.ricardomorarey:Compose-Piechart-library:1.1.0")
}
```

## Usage

### Basic pie chart

```kotlin
val slices = listOf(
    PieSlice(value = 40f, color = PieChartDefaults.colorFor(0), label = "Kotlin"),
    PieSlice(value = 25f, color = PieChartDefaults.colorFor(1), label = "Java"),
    PieSlice(value = 20f, color = PieChartDefaults.colorFor(2), label = "C++"),
    PieSlice(value = 15f, color = PieChartDefaults.colorFor(3), label = "Other"),
)

PieChart(
    slices = slices,
    modifier = Modifier.size(220.dp),
)
```

### Donut chart with labels and click handling

```kotlin
PieChart(
    slices = slices,
    modifier = Modifier.size(220.dp),
    style = PieChartStyle(
        holeRatio = 0.6f,              // 0 = pie, up to 0.95 = thin ring
        sliceSpacingDegrees = 2f,      // gap between slices
        startAngleDegrees = -90f,      // start at 12 o'clock
        showPercentageLabels = true,
    ),
    onSliceClick = { slice -> println("Clicked: ${slice.label}") },
)
```

### Bar chart with X/Y axes

```kotlin
val entries = listOf(
    BarEntry(value = 12f, label = "Jan"),
    BarEntry(value = 30f, label = "Feb"),
    BarEntry(value = 22f, label = "Mar"),
    BarEntry(value = 45f, label = "Apr", color = Color(0xFFE15759)), // per-bar color
)

BarChart(
    entries = entries,
    modifier = Modifier.fillMaxWidth().height(220.dp),
    style = BarChartStyle(
        barColor = PieChartDefaults.colorFor(0),
        yTickCount = 5,            // Y-axis divisions (auto "nice" scale)
        barSpacingRatio = 0.3f,    // gap around each bar
        showGridLines = true,
    ),
    onBarClick = { entry -> println("Clicked: ${entry.label}") },
)
```

### Customizing the animation

```kotlin
PieChart(
    slices = slices,
    modifier = Modifier.size(220.dp),
    animationSpec = tween(durationMillis = 1500, easing = LinearOutSlowInEasing),
)
```

## API overview

| Type | Description |
|---|---|
| `PieChart` | Pie / donut chart composable |
| `PieSlice(value, color, label)` | One slice of data |
| `PieChartStyle` | Pie visual configuration (hole ratio, spacing, start angle, labels) |
| `BarChart` | Bar chart composable with X/Y axes |
| `BarEntry(value, label, color)` | One bar of data |
| `BarChartStyle` | Bar visual configuration (axis, grid, spacing, corners) |
| `PieChartDefaults` | Colorblind-friendly default palette |

## Sample app

The [`sample`](sample/) module contains a demo app showing a pie chart, a donut chart, a bar chart, selection handling and a legend. Open the project in Android Studio and run the `sample` configuration.

## License

```
MIT License — Copyright (c) 2026 Ricardo Mora Rey
```

See [LICENSE](LICENSE) for details. This library is an original implementation; it does not include code from other charting libraries.
