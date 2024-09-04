package dev.ayupi.penguinstorageexplorer.data.remote

import dev.ayupi.penguinstorageexplorer.data.builder.ApiOptionsBuilder
import dev.ayupi.penguinstorageexplorer.data.builder.BatchDeleteBuilder
import dev.ayupi.penguinstorageexplorer.data.model.StorageItem
import dev.ayupi.penguinstorageexplorer.data.model.StorageLocation
import dev.ayupi.penguinstorageexplorer.data.model.response.UpsertItemResponse
import dev.ayupi.penguinstorageexplorer.domain.mapper.toStorageItemDto
import dev.ayupi.penguinstorageexplorer.domain.mapper.toStorageItemRequest
import dev.ayupi.penguinstorageexplorer.domain.mapper.toStorageLocationDto
import dev.ayupi.penguinstorageexplorer.domain.model.DateOfExpiryState
import dev.ayupi.penguinstorageexplorer.domain.model.PSESortOrder
import dev.ayupi.penguinstorageexplorer.domain.model.StorageItemDto
import dev.ayupi.penguinstorageexplorer.domain.model.StorageLocationDto
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import dev.ayupi.penguinstorageexplorer.BuildKonfig
import dev.ayupi.penguinstorageexplorer.data.model.response.UpsertLocationResponse
import dev.ayupi.penguinstorageexplorer.domain.mapper.toStorageLocation

class ApiRepositoryImpl(
    private val httpClient: HttpClient
): ApiRepository {
    override suspend fun getAllItems(
        sort: PSESortOrder,
        locationId: Int?,
        filterExpiry: DateOfExpiryState?,
        query: String?
    ): Result<List<StorageItemDto>> {
        val apiUrl = ApiOptionsBuilder(url = "https://api.beste-leben.de/api/storage-items")
            .sort(sort)
            .locationId(locationId)
            .filterExpiry(filterExpiry)
            .query(query)
            .build()


        val response = httpClient.get(apiUrl) {
            headers.append(BuildKonfig.API_HEADER, value = BuildKonfig.API_KEY)
        }
        return if (response.status.isSuccess()) {
            Json.decodeFromString<List<StorageItem>>(response.bodyAsText()).map {
                it.toStorageItemDto()
            }.let {
                return Result.success(it)
            }
        } else Result.failure(Exception("Data not available"))
    }

    override suspend fun getItemById(id: Int): Result<StorageItemDto> {
        val response = httpClient.get("https://api.beste-leben.de/api/storage-items/$id") {
            headers.append(BuildKonfig.API_HEADER, value = BuildKonfig.API_KEY)
        }
        return if (response.status.isSuccess()) {
            Json.decodeFromString<StorageItem>(response.bodyAsText()).let {
                Result.success(it.toStorageItemDto())
            }
        } else Result.failure(Exception("Data not available"))
    }

    override suspend fun upsertItem(item: StorageItemDto, restore: Boolean): Result<StorageItemDto> {
        val request = if (item.id == 0 || restore) {
            httpClient.post("https://api.beste-leben.de/api/storage-items") {
                headers.append(BuildKonfig.API_HEADER, value = BuildKonfig.API_KEY)
                contentType(ContentType.Application.Json)
                setBody(Json.encodeToString(item.toStorageItemRequest()))
            }
        } else {
            httpClient.put("https://api.beste-leben.de/api/storage-items/${item.id}") {
                headers.append(BuildKonfig.API_HEADER, value = BuildKonfig.API_KEY)
                contentType(ContentType.Application.Json)
                setBody(Json.encodeToString(item.toStorageItemRequest()))
            }
        }
        return if(request.status.isSuccess()) {
            val itemResponse = Json.decodeFromString<UpsertItemResponse>(request.bodyAsText())
            Result.success(itemResponse.entry.toStorageItemDto())
        } else {
            Result.failure(Exception("Api Error: Upsert did not succeed"))
        }
    }

    override suspend fun deleteItem(id: Int): Result<Boolean> {
        val request = httpClient.delete("https://api.beste-leben.de/api/storage-items/$id") {
            headers.append(BuildKonfig.API_HEADER, value = BuildKonfig.API_KEY)
        }
        return if(request.status.isSuccess()) {
            Result.success(true)
        } else {
            Result.failure(Exception("Deleting Item not successful"))
        }
    }

    override suspend fun batchDeleteItems(ids: Set<Int>): Result<Boolean> {
        val apiUrl = BatchDeleteBuilder("https://api.beste-leben.de/api/storage-items/delete")
            .setIds(ids)
            .build()

        val apiResponse = httpClient.delete(apiUrl) {
            headers.append(BuildKonfig.API_HEADER, value = BuildKonfig.API_KEY)
        }

        return if(apiResponse.status.isSuccess()) {
            Result.success(true)
        } else {
            Result.failure(Exception("Deleting Items not successful"))
        }
    }

    override suspend fun getAllLocations(): Result<List<StorageLocationDto>> {
        httpClient.get("https://api.beste-leben.de/api/storage-locations") {
            headers.append(BuildKonfig.API_HEADER, value = BuildKonfig.API_KEY)
            contentType(ContentType.Application.Json)
        }.let { response ->
            return if (response.status.isSuccess()) {
                Json.decodeFromString<List<StorageLocation>>(response.bodyAsText()).map {
                   it.toStorageLocationDto()
                }.let {
                    Result.success(it)
                }
            } else Result.failure(Exception("Data not available"))
        }
    }

    override suspend fun getLocationById(id: Int): Result<StorageLocationDto> {
        httpClient.get("https://api.beste-leben.de/api/storage-locations/$id") {
            headers.append(BuildKonfig.API_HEADER, value = BuildKonfig.API_KEY)
            contentType(ContentType.Application.Json)
        }.let { response ->
            return if (response.status.isSuccess()) {
                Json.decodeFromString<StorageLocation>(response.bodyAsText()).let {
                    Result.success(it.toStorageLocationDto())
                }
            } else Result.failure(Exception("Data not available"))
        }
    }

    override suspend fun upsertLocation(location: StorageLocationDto): Result<StorageLocationDto> {
        val request = if (location.id == 0) {
            httpClient.post("https://api.beste-leben.de/api/storage-locations") {
                headers.append(BuildKonfig.API_HEADER, value = BuildKonfig.API_KEY)
                contentType(ContentType.Application.Json)
                setBody(Json.encodeToString(location.toStorageLocation()))
            }
        } else {
            httpClient.put("https://api.beste-leben.de/api/storage-locations/${location.id}") {
                headers.append(BuildKonfig.API_HEADER, value = BuildKonfig.API_KEY)
                contentType(ContentType.Application.Json)
                setBody(Json.encodeToString(location.toStorageLocation()))
            }
        }
        return if(request.status.isSuccess()) {
            val locationResponse = Json.decodeFromString<UpsertLocationResponse>(request.bodyAsText())
            Result.success(locationResponse.entry.toStorageLocationDto())
        } else {
            Result.failure(Exception("Api Error: Upsert did not succeed"))
        }
    }

    override suspend fun deleteLocation(id: Int): Result<Boolean> {
        val request = httpClient.delete("https://api.beste-leben.de/api/storage-locations/$id") {
            headers.append(BuildKonfig.API_HEADER, value = BuildKonfig.API_KEY)
        }
        return if(request.status.isSuccess()) {
            Result.success(true)
        } else {
            Result.failure(Exception("Deleting Item not successful"))
        }
    }
}