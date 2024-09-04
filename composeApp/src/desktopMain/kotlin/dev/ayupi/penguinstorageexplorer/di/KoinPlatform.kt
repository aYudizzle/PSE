package dev.ayupi.penguinstorageexplorer.di

import dev.ayupi.penguinstorageexplorer.presentation.listview.ListViewViewModel
import io.ktor.client.engine.java.Java
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.scope.get
import org.koin.dsl.module

actual fun platformModule() = module {
    single { Java.create() }
}