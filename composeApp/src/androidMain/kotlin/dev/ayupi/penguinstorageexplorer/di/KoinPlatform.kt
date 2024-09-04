package dev.ayupi.penguinstorageexplorer.di

import dev.ayupi.penguinstorageexplorer.presentation.listview.ListViewViewModel
import io.ktor.client.engine.android.Android
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

actual fun platformModule() = module {
    single { Android.create() }
}