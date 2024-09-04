package dev.ayupi.penguinstorageexplorer.data.model.request

import dev.ayupi.penguinstorageexplorer.data.model.StorageLocation
import dev.ayupi.penguinstorageexplorer.data.model.serializer.LocalDateSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class StorageItemRequest(
    @SerialName("item_name") val itemName: String,
    @SerialName("item_stock") val itemStock: Int,
    @SerialName("item_size") val itemSize: Int,
    @SerialName("item_location_id") val itemLocationId: Int,
    @Serializable(with = LocalDateSerializer::class)
    @SerialName("date_of_expiry") val dateOfExpiry: LocalDate,
)
