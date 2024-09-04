package dev.ayupi.penguinstorageexplorer.data.model.response

import dev.ayupi.penguinstorageexplorer.data.model.StorageLocation
import kotlinx.serialization.Serializable

@Serializable
data class UpsertLocationResponse(
    val entry: StorageLocation,
    val message: String,
)
