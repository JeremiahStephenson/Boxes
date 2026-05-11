package com.jerry.shapes.inject

import androidx.datastore.preferences.preferencesDataStore
import com.google.firebase.analytics.FirebaseAnalytics
import com.jerry.shapes.LaunchViewModel
import com.jerry.shapes.datastore.AppDataStore
import com.jerry.shapes.repository.BoxesRepository
import com.jerry.shapes.ui.boxes.BoxesViewModel
import com.jerry.shapes.ui.create.CreateViewModel
import com.jerry.shapes.ui.home.HomeViewModel
import com.jerry.shapes.ui.layers.LayersEditViewModel
import com.jerry.shapes.util.CoroutineContextProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule =
    module {
        viewModel { BoxesViewModel(get(), get(), get(), get()) }
        viewModel { HomeViewModel(get(), get()) }
        viewModel { CreateViewModel(get(), get()) }
        viewModel { LayersEditViewModel(get(), get(), get(), get()) }
        viewModel { LaunchViewModel(get()) }

        single { BoxesRepository(get(), get(), get(), get(), get(), get()) }

        single { CoroutineScope(SupervisorJob() + get<CoroutineContextProvider>().commonPool) }

        single<CoroutineContextProvider> { CoroutineContextProvider.MainCoroutineContext }

        single { AppDataStore(get(), preferencesDataStore(name = "settings")) }

        single { FirebaseAnalytics.getInstance(get()) }
    }
