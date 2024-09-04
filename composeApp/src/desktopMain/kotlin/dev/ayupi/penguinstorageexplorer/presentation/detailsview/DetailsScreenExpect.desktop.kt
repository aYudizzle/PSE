package dev.ayupi.penguinstorageexplorer.presentation.detailsview

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import dev.ayupi.penguinstorageexplorer.ui.util.toRGBColor

@Composable
actual fun ColoredCircle(color: String) {
    Canvas(modifier = Modifier.size(54.dp)) {
        drawCircle(
            color = Color.Black,
            radius = 14f, // Etwas größer als der innere Kreis
            style = Stroke(width = 2f) // Breite des Randes
        )
        drawCircle(
            color = color.toRGBColor(),
            radius = 13f,
        )
    }
}