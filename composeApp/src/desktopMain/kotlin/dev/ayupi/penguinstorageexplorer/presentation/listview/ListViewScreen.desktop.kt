package dev.ayupi.penguinstorageexplorer.presentation.listview

import androidx.compose.foundation.ScrollbarStyle
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
actual fun ScrollBarElement(lazyListState: LazyListState) {
    VerticalScrollbar(
        modifier = Modifier.fillMaxHeight(),
        style = ScrollbarStyle(
            thickness = 8.dp,
            hoverDurationMillis = 500,
            hoverColor = Color.DarkGray,
            minimalHeight = 10.dp,
            unhoverColor = Color(252,123,6),
            shape = ShapeDefaults.Medium
        ),

        adapter = rememberScrollbarAdapter(lazyListState)
    )
}
