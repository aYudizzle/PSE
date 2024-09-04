package dev.ayupi.penguinstorageexplorer.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import dev.ayupi.penguinstorageexplorer.domain.model.DateOfExpiryState
import dev.ayupi.penguinstorageexplorer.domain.model.StorageItemDto
import dev.ayupi.penguinstorageexplorer.presentation.detailsview.ColoredCircle
import dev.ayupi.penguinstorageexplorer.ui.util.formatDate

@OptIn(ExperimentalMaterial3Api::class)
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
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.EndToStart) {
                onDelete(item)
            }
            true
        }
    )
    val degrees by animateFloatAsState(
        targetValue = if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) {
            -45f
        } else {
            0f
        },
        label = "delete degrees"
    )
    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            DismissBackground(
                dismissState = dismissState,
                degrees = degrees
            )
        },
        enableDismissFromStartToEnd = false,
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
                onClick = onClick,
                onIncreaseClicked = onIncreaseClicked,
                onItemSelectionChanged = onItemSelectionChanged,
                itemSelected = itemSelected
            )
        }
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
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(75.dp)
            .clickable { onClick(item.id) },
        shape = RoundedCornerShape(0.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color.LightGray
                )
                .padding(start = 10.dp, end = 10.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(54.dp)
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
                    ColoredCircle(color = if (itemSelected) "#FC7B06" else item.location.color)
                    androidx.compose.animation.AnimatedVisibility(
                        visible = itemSelected,
                        enter = fadeIn(),
                        exit = fadeOut() + shrinkOut(),
                    ) {
                        androidx.compose.material.Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null
                        )
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
                    Row() {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DismissBackground(
    dismissState: SwipeToDismissBoxState,
    degrees: Float
) {
    val color = when (dismissState.dismissDirection) {
        SwipeToDismissBoxValue.EndToStart -> Color.Red
        else -> Color.Transparent
    }
    val direction = dismissState.dismissDirection

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(color)
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier)
        if (direction == SwipeToDismissBoxValue.EndToStart) Icon(
            modifier = Modifier.rotate(degrees),
            imageVector = Icons.Default.Delete,
            contentDescription = null,
            tint = Color.White
        )
    }
}

@Composable
internal fun StorageItemView(
    onClick: (itemId: Int) -> Unit,
    item: StorageItemDto,
    onIncreaseClicked: (StorageItemDto) -> Unit,
    onDecreaseClicked: (StorageItemDto) -> Unit,
    itemSelected: Boolean,
    onItemSelectionChanged: (StorageItemDto) -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(75.dp)
            .clickable { onClick(item.id) },
        shape = RoundedCornerShape(0.dp),
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
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(54.dp)
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
                    ColoredCircle(color = if (itemSelected) "#FC7B06" else item.location.color)
                    androidx.compose.animation.AnimatedVisibility(
                        visible = itemSelected,
                        enter = fadeIn(),
                        exit = fadeOut() + shrinkOut(),
                    ) {
                        androidx.compose.material.Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null
                        )
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