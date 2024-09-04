package dev.ayupi.penguinstorageexplorer.data.model

import kotlinx.serialization.Serializable

@Serializable
data class StorageLocation(
    val id: Int,
    val name: String,
    val color: String,
)