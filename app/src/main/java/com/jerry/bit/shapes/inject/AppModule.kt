package com.jerry.bit.shapes.inject

import androidx.datastore.preferences.preferencesDataStore
import com.google.firebase.analytics.FirebaseAnalytics
import com.jerry.bit.shapes.LaunchViewModel
import com.jerry.bit.shapes.datastore.AppDataStore
import com.jerry.bit.shapes.navigation.Navigator
import com.jerry.bit.shapes.repository.BoxesRepository
import com.jerry.bit.shapes.ui.boxes.BoxesViewModel
import com.jerry.bit.shapes.ui.create.CreateViewModel
import com.jerry.bit.shapes.ui.home.HomeNavKey
import com.jerry.bit.shapes.ui.home.HomeViewModel
import com.jerry.bit.shapes.ui.layers.LayersEditViewModel
import com.jerry.bit.shapes.util.CoroutineContextProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule =
    module {
        viewModel { BoxesViewModel(get(), get(), get(), get()) }
        viewModel { HomeViewModel(get(), get()) }
        viewModel { CreateViewModel(get()) }
        viewModel { LayersEditViewModel(get(), get(), get()) }
        viewModel { LaunchViewModel(get()) }

        single { BoxesRepository(get(), get(), get(), get(), get(), get()) }

        single { Navigator(HomeNavKey) }

        single { CoroutineScope(SupervisorJob() + get<CoroutineContextProvider>().commonPool) }

        single<CoroutineContextProvider> { CoroutineContextProvider.MainCoroutineContext }

        single { AppDataStore(get(), preferencesDataStore(name = "settings")) }

        single { FirebaseAnalytics.getInstance(get()) }
    }
