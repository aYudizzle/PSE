package dev.ayupi.penguinstorageexplorer.data.model

import dev.ayupi.penguinstorageexplorer.data.model.serializer.LocalDateSerializer
import dev.ayupi.penguinstorageexplorer.domain.model.DateOfExpiryState
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class StorageItem(
    val id: Int,
    val itemName: String,
    val itemStock: Int,
    val location: StorageLocation,
    val itemSize: Int,
    @Serializable(with = LocalDateSerializer::class)
    val dateOfExpiry: LocalDate,
    val dateOfExpiryState: String
)