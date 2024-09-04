package dev.ayupi.penguinstorageexplorer.presentation.listview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.ayupi.penguinstorageexplorer.data.remote.ApiRepository
import dev.ayupi.penguinstorageexplorer.domain.model.DateOfExpiryState
import dev.ayupi.penguinstorageexplorer.domain.model.PSESortOrder
import dev.ayupi.penguinstorageexplorer.domain.model.StorageItemDto
import dev.ayupi.penguinstorageexplorer.domain.model.StorageLocationDto
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ListViewViewModel(
    private val apiRepository: ApiRepository
) : ViewModel() {
    private var _uiState = MutableStateFlow<ListItemsUiState>(ListItemsUiState.Loading)
    val uiState: StateFlow<ListItemsUiState> = _uiState.asStateFlow()

    var eventFlow = MutableSharedFlow<ListItemsEvent>()
        private set

    private var recentlyDeleteItems: List<StorageItemDto>? = null

    init {
        viewModelScope.launch {
            _uiState.value = ListItemsUiState.Loading
            val possibleLocations = apiRepository.getAllLocations().getOrDefault(emptyList())
            val storageItems = apiRepository.getAllItems(sort = PSESortOrder.Asc).getOrDefault(
                emptyList()
            )
            _uiState.value = ListItemsUiState.Success(
                ListItemsViewState(
                    storageLocations = possibleLocations,
                ),
                storageItems = storageItems
            )
        }
    }

    private suspend fun loadStorageItems() {
        val currentViewState = (uiState.value as? ListItemsUiState.Success)?.state
        _uiState.value = ListItemsUiState.Loading
        apiRepository.getAllItems(
            sort = currentViewState?.sortedOrder ?: PSESortOrder.Asc,
            locationId = currentViewState?.selectedLocation?.id,
            filterExpiry = currentViewState?.selectedExpiryState,
            query = currentViewState?.searchQuery
        ).onSuccess { items ->
            _uiState.update {
                ListItemsUiState.Success(
                    state = currentViewState ?: ListItemsViewState(),
                    storageItems = items
                )
            }
        }.onFailure {
            _uiState.value = ListItemsUiState.Error(it.message ?: "Unknown error")
        }
    }


    fun increaseItemCount(item: StorageItemDto) {
        viewModelScope.launch {
            val updatedItem = item.copy(itemStock = item.itemStock + 1)
            apiRepository.upsertItem(updatedItem)
                .onSuccess {
                    loadStorageItems()
                }
                .onFailure {
                    eventFlow.emit(ListItemsEvent.ShowSnackbar(it.message ?: "Unknown error", null))
                }
        }
    }


    fun decreaseItemCount(item: StorageItemDto) {
        viewModelScope.launch {
            val updatedItem = item.copy(itemStock = if(item.itemStock > 0) item.itemStock - 1 else 0)
            apiRepository.upsertItem(updatedItem)
                .onSuccess {
                    loadStorageItems()
                }
                .onFailure {
                    eventFlow.emit(ListItemsEvent.ShowSnackbar(it.message ?: "Unknown error", null))
                }

        }
    }

    fun onSearchQueryChange(query: String) {
        viewModelScope.launch {
            val currentState = uiState.value as? ListItemsUiState.Success
            currentState?.let { currentUiState ->
                val updatedViewState = currentUiState.state.copy(searchQuery = query)
                _uiState.update { currentUiState.copy(state = updatedViewState) }
            }
        }
    }

    fun onChangeSortOrder() {
        viewModelScope.launch {
            val currentState = uiState.value as? ListItemsUiState.Success
            currentState?.let { currentUiState ->
                val updatedViewState =
                    currentUiState.state.copy(sortedOrder = if (currentUiState.state.sortedOrder == PSESortOrder.Asc) PSESortOrder.Desc else PSESortOrder.Asc)
                _uiState.update { currentUiState.copy(state = updatedViewState) }
                loadStorageItems()
            }
        }
    }

    fun onFilterByStorageLocation(storageLocation: StorageLocationDto?) {
        viewModelScope.launch {
            val currentState = uiState.value as? ListItemsUiState.Success
            currentState?.let { currentUiState ->
                val updatedViewState =
                    currentUiState.state.copy(selectedLocation = storageLocation)
                _uiState.update { currentUiState.copy(state = updatedViewState) }
                loadStorageItems()
            }
        }
    }

    fun onFilterByExpiryState(expiryState: DateOfExpiryState?) {
        viewModelScope.launch {
            val currentState = uiState.value as? ListItemsUiState.Success
            currentState?.let { currentUiState ->
                val updatedViewState =
                    currentUiState.state.copy(selectedExpiryState = expiryState)
                _uiState.update { currentUiState.copy(state = updatedViewState) }
                loadStorageItems()
            }
        }
    }

    fun onSearchTriggered() {
        viewModelScope.launch {
            loadStorageItems()
        }
    }

    fun onRestoreItem() {
        viewModelScope.launch {
            recentlyDeleteItems?.let {
                it.forEach { item ->
                    apiRepository.upsertItem(item = item, restore = true)
                }
                eventFlow.emit(
                    ListItemsEvent.ShowSnackbar(
                        "${recentlyDeleteItems?.size} Item(s) restored",
                        null
                    )
                )
                recentlyDeleteItems = null
            }
            loadStorageItems()
        }
    }

    fun onDeleteItem(item: StorageItemDto) {
        viewModelScope.launch {
            val currentState = uiState.value as? ListItemsUiState.Success
            currentState?.let { currentUiState ->
                apiRepository.deleteItem(item.id).onSuccess {
                    recentlyDeleteItems = listOf(item)

                    _uiState.update {
                        currentUiState.copy(
                            state = currentUiState.state.copy(
                                storageItemsToDelete = emptySet()
                            )
                        )
                    }

                    loadStorageItems()
                    eventFlow.emit(ListItemsEvent.ShowSnackbar("${item.itemName} deleted", "Undo"))
                }
            }
        }
    }

    fun onMultiDeleteClicked() {
        viewModelScope.launch {
            val currentState = uiState.value as? ListItemsUiState.Success
            currentState?.let { currentUiState ->
                val itemSelected = currentUiState.storageItems.mapNotNull { items ->
                    items.takeIf { currentUiState.state.storageItemsToDelete.contains(it.id) }
                }
                apiRepository.batchDeleteItems(currentUiState.state.storageItemsToDelete)
                    .onSuccess {
                        recentlyDeleteItems = itemSelected
                        _uiState.update {
                            currentUiState.copy(
                                state = currentUiState.state.copy(
                                    storageItemsToDelete = emptySet()
                                )
                            )
                        }
                        loadStorageItems()
                        eventFlow.emit(
                            ListItemsEvent.ShowSnackbar(
                                "${itemSelected.size} items deleted",
                                "Undo"
                            )
                        )
                    }
                    .onFailure {
                        eventFlow.emit(
                            ListItemsEvent.ShowSnackbar(
                                "${it.message}",
                                null
                            )
                        )
                    }
            }
        }
    }

    fun onItemSelectionChanged(item: StorageItemDto) {
        viewModelScope.launch {
            val currentState = uiState.value as? ListItemsUiState.Success
            currentState?.let { currentUiState ->
                val itemSet = currentUiState.state.storageItemsToDelete.toMutableSet()
                if (itemSet.contains(item.id)) {
                    itemSet.remove(item.id)
                } else {
                    itemSet.add(item.id)
                }
                val updatedViewState = currentUiState.state.copy(storageItemsToDelete = itemSet)
                _uiState.update { currentUiState.copy(state = updatedViewState) }
            }
        }
    }
}

sealed interface ListItemsUiState {
    data object Loading : ListItemsUiState
    data class Success(val state: ListItemsViewState, val storageItems: List<StorageItemDto>) :
        ListItemsUiState

    data class Error(val message: String) : ListItemsUiState
}

data class ListItemsViewState(
    val searchQuery: String = "",
    val sortedOrder: PSESortOrder = PSESortOrder.Asc,
    val selectedExpiryState: DateOfExpiryState? = null,
    val storageLocations: List<StorageLocationDto> = emptyList(),
    val selectedLocation: StorageLocationDto? = null,
    val storageItemsToDelete: Set<Int> = emptySet(),
)

sealed interface ListItemsEvent {
    data class ShowSnackbar(val message: String, val action: String? = null) : ListItemsEvent
}