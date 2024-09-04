package dev.ayupi.penguinstorageexplorer.data.remote

import dev.ayupi.penguinstorageexplorer.domain.model.DateOfExpiryState
import dev.ayupi.penguinstorageexplorer.domain.model.PSESortOrder
import dev.ayupi.penguinstorageexplorer.domain.model.StorageItemDto
import dev.ayupi.penguinstorageexplorer.domain.model.StorageLocationDto

interface ApiRepository {
    suspend fun getAllItems(sort: PSESortOrder, locationId: Int? = null, filterExpiry: DateOfExpiryState? = null, query: String? = null): Result<List<StorageItemDto>>
    suspend fun getItemById(id: Int): Result<StorageItemDto>
    suspend fun upsertItem(item: StorageItemDto, restore: Boolean = false): Result<StorageItemDto>
    suspend fun deleteItem(id: Int): Result<Boolean>
    suspend fun batchDeleteItems(ids: Set<Int>): Result<Boolean>

    suspend fun getAllLocations(): Result<List<StorageLocationDto>>
    suspend fun getLocationById(id: Int): Result<StorageLocationDto>
    suspend fun upsertLocation(location: StorageLocationDto): Result<StorageLocationDto>
    suspend fun deleteLocation(id: Int): Result<Boolean>
}