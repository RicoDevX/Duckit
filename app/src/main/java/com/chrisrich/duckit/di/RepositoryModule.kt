package com.chrisrich.duckit.di

import com.chrisrich.duckit.DuckitRepo
import org.koin.dsl.module

val repositoryModule = module {
    factory {  DuckitRepo(get()) }
}