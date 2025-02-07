package com.chrisrich.duckit.di

import com.chrisrich.duckit.DuckitViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule= module {
    viewModel{DuckitViewModel(get(),get())}
}