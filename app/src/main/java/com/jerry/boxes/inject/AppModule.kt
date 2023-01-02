package com.jerry.boxes.inject

import com.jerry.boxes.ui.boxes.BoxesViewModel
import com.jerry.boxes.ui.create.CreateViewModel
import com.jerry.boxes.ui.home.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { BoxesViewModel(get(), get(), get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { CreateViewModel(get()) }
}