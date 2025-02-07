package com.chrisrich.duckit.di

import com.chrisrich.duckit.DuckitRepo
import com.chrisrich.duckit.DuckitViewModel
import com.chrisrich.duckit.SessionManager
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module


val duckItModule = module {
    singleOf(::DuckitRepo) { bind<DuckitRepo>() }
    singleOf(::SessionManager) { bind<SessionManager>() }
    viewModelOf(::DuckitViewModel)
}
