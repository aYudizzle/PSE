package dev.ayupi.penguinstorageexplorer.domain.mapper

import dev.ayupi.penguinstorageexplorer.data.model.StorageLocation
import dev.ayupi.penguinstorageexplorer.domain.model.StorageLocationDto

fun StorageLocation.toStorageLocationDto(): StorageLocationDto = StorageLocationDto(
    id = id,
    name = name,
    color = color,
)

fun StorageLocationDto.toStorageLocation(): StorageLocation = StorageLocation(
    id = id,
    name = name,
    color = color,
)