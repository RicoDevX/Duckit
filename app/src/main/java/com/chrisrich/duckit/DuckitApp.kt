package com.chrisrich.duckit

import android.app.Application
import com.chrisrich.duckit.di.duckItModule
import com.chrisrich.duckit.di.networkModule
import com.chrisrich.duckit.di.repositoryModule
import com.chrisrich.duckit.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class DuckitApp : Application(){
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@DuckitApp)
            androidLogger()
            modules(duckItModule, networkModule, viewModelModule, repositoryModule)
        }
    }
}