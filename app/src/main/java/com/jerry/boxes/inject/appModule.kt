package com.jerry.boxes.inject

import com.jerry.boxes.ui.boxes.BoxesViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { BoxesViewModel(get()) }
}