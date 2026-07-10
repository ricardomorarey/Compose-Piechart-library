# Compose PieChart Library

[![](https://jitpack.io/v/ricardomorarey/Compose-Piechart-library.svg)](https://jitpack.io/#ricardomorarey/Compose-Piechart-library)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

A lightweight, animated **pie / donut chart** library for **Jetpack Compose**, written 100% in Kotlin with no third-party dependencies.

## Features

- 🥧 Pie and donut modes (configurable hole ratio)
- 🎬 Animated sweep on first display and on data changes
- 👆 Click handling per slice
- 🏷️ Optional percentage labels (auto-hidden on tiny slices)
- ↔️ Configurable start angle and spacing between slices
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
    implementation("com.github.ricardomorarey:Compose-Piechart-library:1.0.0")
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
| `PieChart` | The chart composable |
| `PieSlice(value, color, label)` | One slice of data |
| `PieChartStyle` | Visual configuration (hole ratio, spacing, start angle, labels) |
| `PieChartDefaults` | Colorblind-friendly default palette |

## Sample app

The [`sample`](sample/) module contains a demo app showing a pie chart, a donut chart, slice selection and a legend. Open the project in Android Studio and run the `sample` configuration.

## License

```
MIT License — Copyright (c) 2026 Ricardo Mora Rey
```

See [LICENSE](LICENSE) for details. This library is an original implementation; it does not include code from other charting libraries.
