package dev.ayupi.penguinstorageexplorer.ui.util

import androidx.compose.ui.graphics.Color

fun String.toRGBColor() =
    Color(
        red = this.substring(1..2).toInt(16),
        green = substring(3..4).toInt(16),
        blue = substring(5..6).toInt(16),
    )