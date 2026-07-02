package com.jerry.shapes.inject

import com.jerry.shapes.datastore.AppDataStore
import com.jerry.shapes.navigation.Navigator
import com.jerry.shapes.repository.BoxesRepository
import com.jerry.shapes.ui.boxes.BoxesViewModel
import com.jerry.shapes.ui.create.CreateViewModel
import com.jerry.shapes.ui.home.HomeNavKey
import com.jerry.shapes.ui.home.HomeViewModel
import com.jerry.shapes.ui.layers.LayersEditViewModel
import com.jerry.shapes.util.CoroutineContextProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule =
    module {
        includes(platformModule)
        
        viewModel { BoxesViewModel(get(), get(), get(), get()) }
        viewModel { HomeViewModel(get(), get()) }
        viewModel { CreateViewModel(get()) }
        viewModel { LayersEditViewModel(get(), get(), get()) }

        single { 
            BoxesRepository(
                boxesDatabase = get(),
                boxesDao = get(),
                applicationScope = get(),
                cc = get(),
                analytics = get(),
                context = get()
            ) 
        }

        single { Navigator(HomeNavKey) }

        single { CoroutineScope(SupervisorJob() + get<CoroutineContextProvider>().commonPool) }

        single<CoroutineContextProvider> { CoroutineContextProvider.MainCoroutineContext }

        single { AppDataStore(get(), get()) }
    }
