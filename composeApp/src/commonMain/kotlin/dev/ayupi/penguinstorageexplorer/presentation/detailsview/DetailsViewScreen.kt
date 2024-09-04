package dev.ayupi.penguinstorageexplorer.presentation.detailsview

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.twotone.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.ayupi.penguinstorageexplorer.domain.model.StorageLocationDto
import dev.ayupi.penguinstorageexplorer.presentation.util.parseToLocalizedDateString
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.parameter.parametersOf

@OptIn(KoinExperimentalAPI::class)
@Composable
fun DetailsViewScreen(
    navigateToListView: () -> Unit,
    id: Int,
    onShowSnackbar: suspend (String, String?) -> Boolean
) {
    val viewModel: DetailsViewModel = koinViewModel<DetailsViewModel> {
        parametersOf(id)
    }

    val state by viewModel.uiState.collectAsState()

    DetailsContent(
        state = state,
        onBackClick = navigateToListView,
        uiEvent = viewModel.eventFlow,
        onShowSnackbar = onShowSnackbar,
        onSaveButtonClicked = viewModel::onSaveButtonClicked,
        onItemNameChanged = viewModel::onItemNameChanged,
        onItemSizeChanged = viewModel::onItemSizeChanged,
        onItemStockChanged = viewModel::onItemStockChanged,
        onDateOfExpiryChanged = viewModel::onDateOfExpiryChanged,
        onLocationSelected = viewModel::onLocationSelected,
    )

}

@Composable
fun DetailsContent(
    state: DetailsItemUiState,
    onBackClick: () -> Unit,
    uiEvent: MutableSharedFlow<DetailsUiEvent>,
    onItemNameChanged: (String) -> Unit,
    onItemSizeChanged: (String) -> Unit,
    onItemStockChanged: (String) -> Unit,
    onDateOfExpiryChanged: (Long) -> Unit,
    onLocationSelected: (StorageLocationDto) -> Unit,
    onSaveButtonClicked: () -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean
) {
    when (state) {
        is DetailsItemUiState.Error -> {}
        DetailsItemUiState.Loading -> CircularProgressIndicator()
        is DetailsItemUiState.Success -> {
            ShowLoadedContent(
                uiState = state,
                uiEvent = uiEvent,
                onItemNameChanged = onItemNameChanged,
                onItemSizeChanged = onItemSizeChanged,
                onItemStockChanged = onItemStockChanged,
                onDateOfExpiryChanged = onDateOfExpiryChanged,
                onLocationSelected = onLocationSelected,
                onNavigateBack = onBackClick,
                onSaveButtonClicked = onSaveButtonClicked,
                onShowSnackbar = onShowSnackbar
            )
        }
    }
}

@Composable
fun ShowLoadedContent(
    uiState: DetailsItemUiState.Success,
    uiEvent: MutableSharedFlow<DetailsUiEvent>,
    onItemNameChanged: (String) -> Unit,
    onItemSizeChanged: (String) -> Unit,
    onItemStockChanged: (String) -> Unit,
    onDateOfExpiryChanged: (Long) -> Unit,
    onLocationSelected: (StorageLocationDto) -> Unit,
    onNavigateBack: () -> Unit,
    onSaveButtonClicked: () -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    LaunchedEffect(true) {
        uiEvent.collectLatest {
            when (it) {
                DetailsUiEvent.Save -> {
                    onNavigateBack()
                }

                is DetailsUiEvent.ShowSnackbar -> {
                    onShowSnackbar(it.message, null)
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart
            ) {
                Text(uiState.title)
            }
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.CenterEnd) {
                IconButton(
                    modifier = Modifier.align(Alignment.CenterEnd), onClick = onNavigateBack
                ) {
                    Icon(imageVector = Icons.TwoTone.Close, contentDescription = "Close")
                }
            }
        }
        OutlinedTextField(modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Item Name") },
            singleLine = true,
            value = uiState.itemName,
            onValueChange = { onItemNameChanged(it) })
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Item Size") },
            singleLine = true,
            value = uiState.itemSize,
            onValueChange = { onItemSizeChanged(it) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Item Stock") },
            singleLine = true,
            value = uiState.itemStock,
            onValueChange = { onItemStockChanged(it) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )
        DateSelectionField(
            onDateSelected = { onDateOfExpiryChanged(it) }, date = uiState.dateOfExpiry
        )
        DropdownMenuStorageLocation(
            selectedLocation = uiState.itemLocation,
            possibleLocations = uiState.possibleLocations,
            onLocationSelected = onLocationSelected
        )
        FilledTonalButton(onClick = onSaveButtonClicked) {
            Text(text = if (uiState.id == 0) "Save" else "Update")
        }
    }
}

@Composable
fun DateSelectionField(
    onDateSelected: (Long) -> Unit,
    date: Long,
    modifier: Modifier = Modifier,
    halfSized: Boolean = false
) {
    var showDialog by remember { mutableStateOf(false) }

    Row(modifier = modifier
        .height(54.dp)
        .padding(top = 4.dp)
        .clickable { showDialog = true }
        .border(
            border = BorderStroke(1.dp, color = MaterialTheme.colorScheme.primary),
            shape = MaterialTheme.shapes.extraSmall
        ), verticalAlignment = Alignment.CenterVertically) {
        IconButton(
            modifier = Modifier
                .alpha(0.7f)
                .weight(1f),
            onClick = { showDialog = true },
        ) {
            Icon(
                imageVector = Icons.Filled.EditCalendar,
                contentDescription = Icons.Filled.EditCalendar.name
            )
        }
        Text(
            modifier = Modifier
                .padding(start = 24.dp)
                .weight(if (halfSized) 3f else 6f),
            style = MaterialTheme.typography.bodyMedium,
            text = date.parseToLocalizedDateString(),
            textAlign = TextAlign.Start
        )
    }
    if (showDialog) {
        CalendarDialog(
            onDateSelected = { date -> onDateSelected(date) },
            onClickShowDialog = {
                showDialog = it
            },
            date = date,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun CalendarDialog(
    onDateSelected: (Long) -> Unit,
    onClickShowDialog: (Boolean) -> Unit,
    date: Long,
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = date,
    )
    DatePickerDialog(
        onDismissRequest = {
            onClickShowDialog(false)
        },
        confirmButton = {
            Button(onClick = {
                datePickerState.selectedDateMillis?.let(onDateSelected)
                onClickShowDialog(false)
            }) {
                Text("Ok")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = { onClickShowDialog(false) }) {
                Text("cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuStorageLocation(
    selectedLocation: StorageLocationDto? = null,
    possibleLocations: List<StorageLocationDto>,
    onLocationSelected: (StorageLocationDto) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {
        OutlinedTextField(
            value = selectedLocation?.name ?: "Select a Location",
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            leadingIcon = {
                selectedLocation?.let {
                    ColoredCircle(color = it.color)
                }
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
        )

        ExposedDropdownMenu(
            modifier = Modifier
                .fillMaxWidth(),
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            possibleLocations.forEach { location ->
                DropdownMenuItem(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    onClick = {
                        onLocationSelected(location)
                        expanded = false
                    },
                    leadingIcon = {
                        ColoredCircle(color = location.color)
                    },
                    text = { Text(location.name) }
                )
                if (location != possibleLocations.last()) {
                    HorizontalDivider() // Verwenden von Divider für eine konsistente Trennung
                }
            }
        }
    }
}
