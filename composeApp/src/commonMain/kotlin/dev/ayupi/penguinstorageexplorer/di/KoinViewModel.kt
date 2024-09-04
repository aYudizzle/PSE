package dev.ayupi.penguinstorageexplorer.di

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import org.koin.compose.currentKoinScope

@Composable
inline fun <reified T: ViewModel> koinViewModelHelper(): T {
    val scope = currentKoinScope()
    return viewModel {
        scope.get<T>()
    }
}