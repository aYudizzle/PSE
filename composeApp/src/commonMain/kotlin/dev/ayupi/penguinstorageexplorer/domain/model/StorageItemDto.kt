package dev.ayupi.penguinstorageexplorer.domain.model

import java.time.LocalDate

data class StorageItemDto(
    val id: Int,
    val itemName: String,
    val itemStock: Int,
    val location: StorageLocationDto,
    val itemSize: Int,
    val dateOfExpiry: LocalDate,
    val dateOfExpiryState: DateOfExpiryState,
)
