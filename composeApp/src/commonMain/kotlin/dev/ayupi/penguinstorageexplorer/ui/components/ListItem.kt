package dev.ayupi.penguinstorageexplorer.ui.components

import androidx.compose.runtime.Composable
import dev.ayupi.penguinstorageexplorer.domain.model.StorageItemDto

@Composable
expect fun ListItem(
    item: StorageItemDto,
    itemSelected: Boolean,
    onClick: (itemId: Int) -> Unit,
    onDelete: (StorageItemDto) -> Unit,
    onIncreaseClicked: (StorageItemDto) -> Unit,
    onDecreaseClicked: (StorageItemDto) -> Unit,
    onItemSelectionChanged: (StorageItemDto) -> Unit,
)