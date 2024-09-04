package dev.ayupi.penguinstorageexplorer.data.model.response

import dev.ayupi.penguinstorageexplorer.data.model.StorageItem
import kotlinx.serialization.Serializable

@Serializable
data class UpsertItemResponse(
    val entry: StorageItem,
    val message: String
)