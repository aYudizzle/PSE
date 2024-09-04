package dev.ayupi.penguinstorageexplorer.domain.mapper

import dev.ayupi.penguinstorageexplorer.data.model.StorageItem
import dev.ayupi.penguinstorageexplorer.data.model.request.StorageItemRequest
import dev.ayupi.penguinstorageexplorer.domain.model.StorageItemDto
import dev.ayupi.penguinstorageexplorer.domain.model.toDateOfExpiryState

fun StorageItem.toStorageItemDto() = StorageItemDto(
    id = id,
    itemName = itemName,
    itemStock = itemStock,
    location = location.toStorageLocationDto(),
    itemSize = itemSize,
    dateOfExpiry = dateOfExpiry,
    dateOfExpiryState = dateOfExpiryState.toDateOfExpiryState()
)

fun StorageItemDto.toStorageItemRequest() = StorageItemRequest(
    itemName = itemName,
    itemStock = itemStock,
    itemLocationId = this.location.id,
    itemSize = itemSize,
    dateOfExpiry = dateOfExpiry
)
