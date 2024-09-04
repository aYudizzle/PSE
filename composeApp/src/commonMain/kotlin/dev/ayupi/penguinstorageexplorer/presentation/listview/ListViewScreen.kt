package dev.ayupi.penguinstorageexplorer.presentation.listview

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Filter
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.ayupi.penguinstorageexplorer.domain.model.DateOfExpiryState
import dev.ayupi.penguinstorageexplorer.domain.model.PSESortOrder
import dev.ayupi.penguinstorageexplorer.domain.model.StorageItemDto
import dev.ayupi.penguinstorageexplorer.domain.model.StorageLocationDto
import dev.ayupi.penguinstorageexplorer.presentation.detailsview.ColoredCircle
import dev.ayupi.penguinstorageexplorer.ui.components.ListItem
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun ListViewScreen(
    navigateToDetails: (Int) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    val viewModel = koinViewModel<ListViewViewModel>()
    val uiState by viewModel.uiState.collectAsState()

    ListViewContent(
        uiState = uiState,
        uiEvent = viewModel.eventFlow,
        navigateToDetails = navigateToDetails,
        onIncreaseClick = viewModel::increaseItemCount,
        onDecreaseClick = viewModel::decreaseItemCount,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onChangeSortOrder = viewModel::onChangeSortOrder,
        onFilterByStorageLocation = viewModel::onFilterByStorageLocation,
        onFilterByExpiryState = viewModel::onFilterByExpiryState,
        onSearchTriggered = viewModel::onSearchTriggered,
        onShowSnackbar = onShowSnackbar,
        onRestoreItem = viewModel::onRestoreItem,
        onItemSelectionChanged = viewModel::onItemSelectionChanged,
        onMultiDeleteClicked = viewModel::onMultiDeleteClicked,
        onDeleteItem = viewModel::onDeleteItem,
    )
}

@Composable
fun ListViewContent(
    uiState: ListItemsUiState,
    uiEvent: MutableSharedFlow<ListItemsEvent>,
    navigateToDetails: (Int) -> Unit,
    onIncreaseClick: (StorageItemDto) -> Unit,
    onDecreaseClick: (StorageItemDto) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onFilterByStorageLocation: (StorageLocationDto?) -> Unit,
    onFilterByExpiryState: (DateOfExpiryState?) -> Unit,
    onSearchTriggered: () -> Unit,
    onChangeSortOrder: () -> Unit,
    onRestoreItem: () -> Unit,
    onDeleteItem: (StorageItemDto) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    onItemSelectionChanged: (StorageItemDto) -> Unit,
    onMultiDeleteClicked: () -> Unit
) {

    LaunchedEffect(true) {
        uiEvent.collectLatest { event ->
            when (event) {
                is ListItemsEvent.ShowSnackbar -> {
                    if (onShowSnackbar(event.message, event.action)) {
                        onRestoreItem()
                    }
                }
            }
        }
    }

    when (uiState) {
        is ListItemsUiState.Error -> {}
        ListItemsUiState.Loading -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
        }

        is ListItemsUiState.Success -> {
            ListViewScreenLayout(
                sortedOrder = uiState.state.sortedOrder,
                searchQuery = uiState.state.searchQuery,
                itemsToDelete = uiState.state.storageItemsToDelete,
                storageLocations = uiState.state.storageLocations,
                selectedLocation = uiState.state.selectedLocation,
                selectedExpiryState = uiState.state.selectedExpiryState,
                navigateToDetails = navigateToDetails,
                onChangeSortOrder = onChangeSortOrder,
                onSearchValueChange = onSearchQueryChange,
                onSearchTriggered = onSearchTriggered,
                onFilterByStorageLocation = onFilterByStorageLocation,
                onFilterByExpiryState = onFilterByExpiryState,
                onMultiDeleteClicked = onMultiDeleteClicked,
            ) {
                LoadedContent(
                    items = uiState.storageItems,
                    selectedItems = uiState.state.storageItemsToDelete,
                    navigateToDetails = navigateToDetails,
                    onDecreaseClick = onDecreaseClick,
                    onIncreaseClick = onIncreaseClick,
                    onItemSelectionChanged = onItemSelectionChanged,
                    onDeleteItem = onDeleteItem,
                )
            }
        }
    }
}


@Composable
fun LoadedContent(
    items: List<StorageItemDto>,
    selectedItems: Set<Int>,
    navigateToDetails: (Int) -> Unit,
    onIncreaseClick: (StorageItemDto) -> Unit,
    onDecreaseClick: (StorageItemDto) -> Unit,
    onItemSelectionChanged: (StorageItemDto) -> Unit,
    onDeleteItem: (StorageItemDto) -> Unit,
) {
    val lazyListState = rememberLazyListState()
    val scrollState = rememberScrollState(0)

    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.firstVisibleItemIndex }
            .collect { index ->
                scrollState.scrollTo(index)
            }
    }

    Row {
        LazyColumn(
            modifier = Modifier.weight(1f),
            state = lazyListState,
        ) {
            items(items, key = { it.id }) { entry ->
                ListItem(
                    item = entry,
                    itemSelected = selectedItems.contains(entry.id),
                    onClick = { id -> navigateToDetails(id) },
                    onDelete = onDeleteItem,
                    onIncreaseClicked = { onIncreaseClick(entry) },
                    onDecreaseClicked = { onDecreaseClick(entry) },
                    onItemSelectionChanged = onItemSelectionChanged,
                )
            }
        }
        ScrollBarElement(lazyListState)
    }
}


@Composable
expect fun ScrollBarElement(lazyListState: LazyListState)

@Composable
fun ListViewScreenLayout(
    modifier: Modifier = Modifier,
    itemsToDelete: Set<Int>,
    storageLocations: List<StorageLocationDto>,
    selectedLocation: StorageLocationDto?,
    selectedExpiryState: DateOfExpiryState?,
    searchQuery: String,
    sortedOrder: PSESortOrder,
    navigateToDetails: (Int) -> Unit,
    onChangeSortOrder: () -> Unit,
    onSearchValueChange: (String) -> Unit,
    onSearchTriggered: () -> Unit,
    onFilterByExpiryState: (DateOfExpiryState?) -> Unit,
    onFilterByStorageLocation: (StorageLocationDto?) -> Unit,
    onMultiDeleteClicked: () -> Unit,
    content: @Composable () -> Unit
) {
    var showSearchbar by remember { mutableStateOf(false) }

    val angle by animateFloatAsState(
        targetValue = if (sortedOrder == PSESortOrder.Asc) 180f else 0f, label = "Icon Angle"
    )

    Column(
        modifier = modifier
    ) {
        AnimatedVisibility(visible = showSearchbar) {
            ListSearchBar(
                searchQuery = searchQuery,
                onSearchValueChange = onSearchValueChange,
                onSearchDismissRequest = {
                    showSearchbar = false
                },
                onSearchTriggered = onSearchTriggered
            )
        }
        AnimatedVisibility(visible = !showSearchbar) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FilterSettings(
                    onChangeSortOrder = onChangeSortOrder,
                    angle = angle,
                    selectedExpiryState = selectedExpiryState,
                    onFilterByExpiryState = onFilterByExpiryState
                )

                when (selectedLocation) {
                    null -> DropdownMenuFilterStorageLocation(
                        title = "Filter by Location",
                        possibleLocations = storageLocations,
                        onLocationSelected = onFilterByStorageLocation
                    )

                    else -> {
                        Row(
                            modifier = Modifier
                                .width(200.dp)
                                .height(54.dp)
                                .padding(top = 4.dp)
                                .clickable { onFilterByStorageLocation(null) },
                            verticalAlignment = CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                modifier = Modifier
                                    .padding(start = 10.dp), text = "Remove Filter"
                            )
                            IconButton(onClick = { onFilterByStorageLocation(null) }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
                Row {
                    IconButton(onClick = {
                        if (searchQuery.isBlank()) {
                            showSearchbar = true
                        } else {
                            onSearchValueChange("")
                            onSearchTriggered()
                        }
                    }
                    ) {
                        Icon(
                            imageVector = if (searchQuery.isNotBlank()) Icons.Default.SearchOff else Icons.Default.Search,
                            contentDescription = null
                        )
                    }
                    IconButton(onClick = {
                        navigateToDetails(0)
                    }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null
                        )
                    }
                }
            }
        }
        AnimatedVisibility(itemsToDelete.isNotEmpty()) {
            MultiDeleteBar(
                itemCount = itemsToDelete.size,
                onMultiDeleteClicked = onMultiDeleteClicked,
            )
        }
        content()
    }
}

@Composable
private fun FilterSettings(
    onChangeSortOrder: () -> Unit,
    angle: Float,
    selectedExpiryState: DateOfExpiryState?,
    onFilterByExpiryState: (DateOfExpiryState?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier.size(56.dp).wrapContentSize(Alignment.CenterStart),
    ) {
        IconButton(onClick = { expanded = true }) {
            Icon(imageVector = Icons.Default.FilterAlt, contentDescription = null)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text(text = if (angle == 0f) "Sort order (Z-A)" else "Sort order (A-Z)") },
                onClick = { onChangeSortOrder() },
                leadingIcon = {
                    Icon(
                        modifier = Modifier.rotate(angle),
                        imageVector = Icons.Default.FilterList,
                        contentDescription = null
                    )
                }
            )
            DropdownMenuItem(
                text = {
                    Text(
                        text = "Expired Items",
                        color = if (selectedExpiryState == DateOfExpiryState.Expired) Color.Red else Color.Black
                    )
                },
                onClick = {
                    if (selectedExpiryState == DateOfExpiryState.Expired) {
                        onFilterByExpiryState(null)
                    } else {
                        onFilterByExpiryState(DateOfExpiryState.Expired)
                    }
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.PriorityHigh,
                        contentDescription = null,
                        tint = if (selectedExpiryState == DateOfExpiryState.Expired) Color.Red else Color.Black
                    )
                }
            )
            DropdownMenuItem(
                text = {
                    Text(
                        text = "Soon to be expired",
                        color = if (selectedExpiryState == DateOfExpiryState.Warning) Color(230,195,0) else Color.Black
                    )
                },
                onClick = {
                    if (selectedExpiryState == DateOfExpiryState.Warning) {
                        onFilterByExpiryState(null)
                    } else {
                        onFilterByExpiryState(DateOfExpiryState.Warning)
                    }
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (selectedExpiryState == DateOfExpiryState.Warning)  Color(230,195,0) else Color.Black
                    )
                }
            )
        }
    }
}

@Composable
fun MultiDeleteBar(
    itemCount: Int,
    onMultiDeleteClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        verticalAlignment = CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "$itemCount item(s) selected")
        IconButton(onClick = onMultiDeleteClicked) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = null)
        }
    }
}


@Composable
fun ListSearchBar(
    searchQuery: String,
    onSearchValueChange: (String) -> Unit,
    onSearchTriggered: () -> Unit,
    onSearchDismissRequest: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        verticalAlignment = CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TextField(
            modifier = Modifier.weight(3f),
            value = searchQuery,
            onValueChange = { value -> onSearchValueChange(value) },
            singleLine = true,
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = null)
            }
        )
        Row() {
            IconButton(
                onClick = {
                    onSearchTriggered()
                }) {
                Icon(imageVector = Icons.AutoMirrored.Default.Send, contentDescription = null)
            }
            IconButton(
                onClick = {
                    onSearchDismissRequest()
                    onSearchValueChange("")
                }) {
                Icon(imageVector = Icons.Default.Close, contentDescription = null)
            }
        }
    }
}

@Composable
fun DropdownMenuFilterStorageLocation(
    title: String,
    possibleLocations: List<StorageLocationDto>,
    onLocationSelected: (StorageLocationDto) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val degrees by animateFloatAsState(
        targetValue = if (expanded) {
            0f
        } else {
            180f
        }, label = "expanded degrees"
    )

    Box(
        modifier = Modifier
            .wrapContentSize(Alignment.TopStart)
    ) {
        Row(
            modifier = Modifier
                .height(54.dp)
                .padding(top = 4.dp)
                .width(200.dp)
                .clickable { expanded = true },
            verticalAlignment = CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                modifier = Modifier
                    .padding(start = 10.dp),
                text = title
            )
            IconButton(onClick = { expanded = true }) {
                Icon(
                    modifier = Modifier
                        .rotate(degrees),
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "More"
                )
            }
        }
        DropdownMenu(modifier = Modifier
            .fillMaxWidth(0.85f)
            .align(Alignment.Center),
            expanded = expanded,
            onDismissRequest = { expanded = false }) {
            possibleLocations.forEach {
                DropdownMenuItem(modifier = Modifier.fillMaxWidth(), leadingIcon = {
                    ColoredCircle(color = it.color)
                }, text = { Text(it.name) }, onClick = {
                    onLocationSelected(it)
                    expanded = false
                })
                if (it != possibleLocations.last()) {
                    HorizontalDivider()
                }
            }
        }
    }
}