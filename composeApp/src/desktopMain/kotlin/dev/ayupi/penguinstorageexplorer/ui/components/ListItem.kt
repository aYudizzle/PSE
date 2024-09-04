package dev.ayupi.penguinstorageexplorer.ui.components

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.ayupi.penguinstorageexplorer.domain.model.DateOfExpiryState
import dev.ayupi.penguinstorageexplorer.domain.model.StorageItemDto
import dev.ayupi.penguinstorageexplorer.presentation.detailsview.ColoredCircle
import dev.ayupi.penguinstorageexplorer.ui.util.formatDate

@Composable
actual fun ListItem(
    item: StorageItemDto,
    itemSelected: Boolean,
    onClick: (itemId: Int) -> Unit,
    onDelete: (StorageItemDto) -> Unit,
    onIncreaseClicked: (StorageItemDto) -> Unit,
    onDecreaseClicked: (StorageItemDto) -> Unit,
    onItemSelectionChanged: (StorageItemDto) -> Unit,
) {
    if (item.itemStock > 0) {
        StorageItemView(
            item = item,
            itemSelected = itemSelected,
            onItemSelectionChanged = onItemSelectionChanged,
            onClick = onClick,
            onIncreaseClicked = onIncreaseClicked,
            onDecreaseClicked = onDecreaseClicked,
        )
    } else {
        EmptyStorageItemView(
            item = item,
            itemSelected = itemSelected,
            onClick = onClick,
            onIncreaseClicked = onIncreaseClicked,
            onItemSelectionChanged = onItemSelectionChanged
        )
    }
}

@Composable
actual fun EmptyStorageItemView(
    item: StorageItemDto,
    onClick: (itemId: Int) -> Unit,
    onIncreaseClicked: (StorageItemDto) -> Unit,
    onItemSelectionChanged: (StorageItemDto) -> Unit,
    itemSelected: Boolean,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(75.dp)
            .background(Color.LightGray)
            .border(
                width = 1.dp,
                color = Color.DarkGray,
                shape = RoundedCornerShape(4.dp) // Etwas abgerundete Ecken für Desktop
            )
            .clickable {
                onClick(item.id)
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 10.dp, end = 0.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .padding(end = 10.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.clickable {
                        onItemSelectionChanged(item)
                    },
                    contentAlignment = Center
                ) {
                    ColoredCircle(color = if(itemSelected) "#FC7B06" else item.location.color)
                    androidx.compose.animation.AnimatedVisibility(
                        visible = itemSelected,
                        enter = fadeIn(),
                        exit = fadeOut() + shrinkOut(),
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null)
                    }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(10f)
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween

                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(0.5f),
                        color = Color.DarkGray,
                        text = item.itemName,
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        modifier = Modifier.fillMaxWidth(0.4f),
                        color = Color.DarkGray,
                        text = "${item.itemSize}",
                        style = TextStyle(
                            fontWeight = FontWeight.Medium,
                            fontSize = 18.sp,
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Row {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = Color.Transparent
                        )
                        Text(
                            modifier = Modifier.fillMaxWidth(0.2f),
                            color = Color.DarkGray,
                            text = "${item.itemStock}",
                            style = TextStyle(
                                fontWeight = FontWeight.Medium,
                                fontSize = 18.sp,
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        IconButton(
                            modifier = Modifier.size(24.dp),
                            onClick = { onIncreaseClicked(item) }) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null)
                        }
                    }
                }
            }

        }
    }
}

@Composable
internal fun ItemStorageLocationColor(
    modifier: Modifier = Modifier,
    color: Color
) {
    Canvas(
        modifier = modifier
            .fillMaxHeight()
            .width(12.dp)
    ) {
        drawRect(color = color)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun StorageItemView(
    item: StorageItemDto,
    itemSelected: Boolean,
    onIncreaseClicked: (StorageItemDto) -> Unit,
    onDecreaseClicked: (StorageItemDto) -> Unit,
    onClick: (itemId: Int) -> Unit,
    onItemSelectionChanged: (StorageItemDto) -> Unit,
) {

    var isHovered by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(75.dp)
            .background(
                if (isHovered) Color.LightGray else Color.White
            )
            .border(
                width = 1.dp,
                color = Color.Gray,
                shape = RoundedCornerShape(4.dp)
            )
            .onPointerEvent(
                eventType = PointerEventType.Enter,
                onEvent = { isHovered = true }
            )
            .onPointerEvent(
                eventType = PointerEventType.Exit,
                onEvent = { isHovered = false }
            )
            .clickable {
                onClick(item.id)
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    item.dateOfExpiryState.let {
                        when (it) {
                            DateOfExpiryState.Ok -> Color.Transparent
                            DateOfExpiryState.Warning -> Color(255, 255, 0, 40)
                            DateOfExpiryState.Expired -> Color(255, 0, 0, 40)
                        }
                    }
                )
                .padding(start = 10.dp, end = 10.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .clickable {
                        onItemSelectionChanged(item)
                    },
                contentAlignment = Center
            ) {
                ColoredCircle(color = if(itemSelected) "#FC7B06" else item.location.color)
                androidx.compose.animation.AnimatedVisibility(
                    visible = itemSelected,
                    enter = fadeIn(),
                    exit = fadeOut() + shrinkOut(),
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null)
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(10f)
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        modifier = Modifier
                            .weight(5f)
                            .padding(end = 20.dp),
                        text = item.itemName,
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        modifier = Modifier.weight(2f),
                        text = "${item.itemSize}",
                        style = TextStyle(
                            fontWeight = FontWeight.Medium,
                            fontSize = 18.sp,
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Row(
                        modifier = Modifier.weight(1.5f),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = CenterVertically
                    ) {
                        IconButton(
                            modifier = Modifier.size(24.dp),
                            onClick = { onDecreaseClicked(item) }) {
                            Icon(imageVector = Icons.Default.Remove, contentDescription = null)
                        }
                        Text(
                            text = "${item.itemStock}",
                            style = TextStyle(
                                fontWeight = FontWeight.Medium,
                                fontSize = 18.sp,
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        IconButton(
                            modifier = Modifier.size(24.dp),
                            onClick = { onIncreaseClicked(item) }) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null)
                        }
                    }

                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = item.location.name,
                        style = TextStyle(
                            fontSize = 16.sp,
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(item.dateOfExpiry.formatDate())
                }

            }
        }
    }
}
