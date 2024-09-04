package dev.ayupi.penguinstorageexplorer.presentation.detailsview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.ayupi.penguinstorageexplorer.data.remote.ApiRepository
import dev.ayupi.penguinstorageexplorer.domain.model.DateOfExpiryState
import dev.ayupi.penguinstorageexplorer.domain.model.StorageItemDto
import dev.ayupi.penguinstorageexplorer.domain.model.StorageLocationDto
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset

class DetailsViewModel(
    private val id: Int,
    private val apiRepository: ApiRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<DetailsItemUiState>(DetailsItemUiState.Loading)
    val uiState = _uiState.asStateFlow()

    var eventFlow = MutableSharedFlow<DetailsUiEvent>()
        private set

    init {
        fetchDetails()
    }

    private fun fetchDetails() {
        viewModelScope.launch {
            if (id == 0) {
                setNewUiState()
            } else {
                apiRepository.getItemById(id).onSuccess { storageItem ->
                    setUiState(storageItem)
                }.onFailure {
                    _uiState.value = DetailsItemUiState.Error(it.message ?: "Unknown error")
                }
            }
        }
    }

    private fun setUiState(item: StorageItemDto) {
        viewModelScope.launch {
            val possibleLocations = apiRepository.getAllLocations().getOrElse {
                emptyList()
            }

            _uiState.value = DetailsItemUiState.Success(
                id = item.id,
                itemName = item.itemName,
                itemStock = item.itemStock.toString(),
                itemLocation = item.location,
                itemSize = item.itemSize.toString(),
                dateOfExpiry = item.dateOfExpiry.atStartOfDay().toInstant(ZoneOffset.UTC)
                    .toEpochMilli(),
                possibleLocations = possibleLocations,
                title = "Edit ${item.itemName}"
            )
        }
    }

    private fun setNewUiState() {
        viewModelScope.launch {
            val possibleLocations = apiRepository.getAllLocations().getOrElse {
                emptyList()
            }

            _uiState.value = DetailsItemUiState.Success(
                id = 0,
                itemName = "",
                itemStock = "",
                itemLocation = null,
                itemSize = "",
                dateOfExpiry = LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC)
                    .toEpochMilli(),
                possibleLocations = possibleLocations,
                title = "Add new item"
            )
        }
    }

    fun onItemNameChanged(value: String) {
        viewModelScope.launch {
            val state = uiState.value as? DetailsItemUiState.Success
            state?.let { uiState ->
                _uiState.update { uiState.copy(itemName = value) }
            }
        }
    }

    fun onItemSizeChanged(value: String) {
        viewModelScope.launch {
            val state = uiState.value as? DetailsItemUiState.Success
            state?.let { uiState ->
                _uiState.update { uiState.copy(itemSize = value) }
            }
        }
    }

    fun onItemStockChanged(value: String) {
        viewModelScope.launch {
            val state = uiState.value as? DetailsItemUiState.Success
            state?.let { uiState ->
                _uiState.update { uiState.copy(itemStock = value) }
            }
        }
    }

    fun onDateOfExpiryChanged(value: Long) {
        viewModelScope.launch {
            val state = uiState.value as? DetailsItemUiState.Success
            state?.let { uiState ->
                _uiState.update { uiState.copy(dateOfExpiry = value) }
            }
        }
    }

    fun onLocationSelected(location: StorageLocationDto) {
        viewModelScope.launch {
            val state = uiState.value as? DetailsItemUiState.Success
            state?.let { uiState ->
                _uiState.update { uiState.copy(itemLocation = location) }
            }
        }
    }

    fun onSaveButtonClicked() {
        viewModelScope.launch {
            val state = uiState.value as? DetailsItemUiState.Success
            state?.let { uiState ->
                inputValidation(uiState)
                    .onSuccess { item ->
                        apiRepository.upsertItem(item)
                            .onSuccess {
                                eventFlow.emit(DetailsUiEvent.Save)
                            }
                            .onFailure { throwable ->
                                eventFlow.emit(DetailsUiEvent.ShowSnackbar(throwable.message.toString()))
                            }
                    }
                    .onFailure {
                        eventFlow.emit(DetailsUiEvent.ShowSnackbar(it.message.toString()))
                    }
            }
        }
    }

    private fun inputValidation(uiState: DetailsItemUiState.Success): Result<StorageItemDto> {
        if (uiState.itemName.isBlank() || uiState.itemName.length < 3) return Result.failure(
            Exception("Item name must be at least 3 characters long")
        )
        if (uiState.itemStock.toIntOrNull() == null) return Result.failure(Exception("Item stock must be a number"))
        if (uiState.itemSize.toIntOrNull() == null) return Result.failure(Exception("Item size must be a number"))
        if (uiState.itemLocation == null) return Result.failure(Exception("Item location must be selected"))
//        if(uiState.dateOfExpiry < LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()) return Result.failure(Exception("Date of expiry must be in the future")))

        val item = StorageItemDto(
            id = uiState.id,
            itemName = uiState.itemName,
            itemStock = uiState.itemStock.toIntOrNull() ?: 0,
            itemSize = uiState.itemSize.toIntOrNull() ?: 0,
            location = uiState.itemLocation,
            dateOfExpiry = Instant.ofEpochMilli(uiState.dateOfExpiry).atZone(ZoneId.systemDefault())
                .toLocalDate(),
            dateOfExpiryState = DateOfExpiryState.Ok // just a placeholder doesn't matter to update the item overall
        )
        return Result.success(item)
    }
}

sealed interface DetailsItemUiState {
    data object Loading : DetailsItemUiState
    data class Success(
        val id: Int = 0,
        val itemName: String = "",
        val itemStock: String = "",
        val itemLocation: StorageLocationDto? = null,
        val itemSize: String = "",
        val dateOfExpiry: Long = LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC)
            .toEpochMilli(),
        val possibleLocations: List<StorageLocationDto> = emptyList(),
        val title: String = ""
    ) : DetailsItemUiState

    data class Error(val message: String) : DetailsItemUiState
}

sealed interface DetailsUiEvent {
    data class ShowSnackbar(val message: String) : DetailsUiEvent
    data object Save : DetailsUiEvent
}