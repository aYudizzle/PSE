package dev.ayupi.penguinstorageexplorer.di

import dev.ayupi.penguinstorageexplorer.data.remote.ApiRepository
import dev.ayupi.penguinstorageexplorer.data.remote.ApiRepositoryImpl
import dev.ayupi.penguinstorageexplorer.presentation.detailsview.DetailsViewModel
import dev.ayupi.penguinstorageexplorer.presentation.listview.ListViewViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.core.context.startKoin
import org.koin.core.scope.get
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun getCommonModule(enableNetworkLogs: Boolean = false) = module {
    single<Json> { createJson() }
    single<HttpClient> { createHttpClient(get(), get(), enableNetworkLogs = enableNetworkLogs) }

    single<ApiRepository> { ApiRepositoryImpl(get()) }

//    viewModelOf<ListViewViewModel>(::ListViewViewModel)

    factory<ListViewViewModel> {
        ListViewViewModel(
            apiRepository = get()
        )
    }

    factory<DetailsViewModel> { (id: Int) ->
        DetailsViewModel(
            id = id,
            apiRepository = get()
        )
    }
}

fun createJson() = Json {
    isLenient = true
    ignoreUnknownKeys = true
}

fun createHttpClient(httpClientEngine: HttpClientEngine, json: Json, enableNetworkLogs: Boolean) = HttpClient(httpClientEngine) {
    install(ContentNegotiation) {
        json(json)
    }
    if(enableNetworkLogs) {
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.NONE
        }
    }
}

fun initKoin(enableNetworkLogs: Boolean = false, appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(getCommonModule(enableNetworkLogs = enableNetworkLogs), platformModule())
    }

// called by iOS etc
fun initKoin() = initKoin(enableNetworkLogs = false) {}