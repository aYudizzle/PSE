package dev.ayupi.penguinstorageexplorer.ui.components

import androidx.compose.runtime.Composable
import dev.ayupi.penguinstorageexplorer.domain.model.StorageItemDto

@Composable
expect fun EmptyStorageItemView(
    item: StorageItemDto,
    onClick: (itemId: Int) -> Unit,
    onIncreaseClicked: (StorageItemDto) -> Unit,
    onItemSelectionChanged: (StorageItemDto) -> Unit,
    itemSelected: Boolean,
)