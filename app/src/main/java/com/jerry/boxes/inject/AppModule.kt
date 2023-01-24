package com.jerry.boxes.inject

import androidx.datastore.preferences.preferencesDataStore
import com.jerry.boxes.datastore.AppDataStore
import com.jerry.boxes.repository.BoxesRepository
import com.jerry.boxes.ui.boxes.BoxesViewModel
import com.jerry.boxes.ui.create.CreateViewModel
import com.jerry.boxes.ui.home.HomeViewModel
import com.jerry.boxes.ui.layers.LayersEditViewModel
import com.jerry.boxes.util.CoroutineContextProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { BoxesViewModel(get(), get(), get()) }
    viewModel { HomeViewModel(get(), get()) }
    viewModel { CreateViewModel(get(), get()) }
    viewModel { LayersEditViewModel(get(), get(), get(), get()) }

    single { BoxesRepository(get(), get(), get(), get(), get()) }

    single { CoroutineScope(SupervisorJob() + get<CoroutineContextProvider>().commonPool) }

    single<CoroutineContextProvider> { CoroutineContextProvider.MainCoroutineContext }

    single { AppDataStore(get(), preferencesDataStore(name = "settings")) }
}
