package com.chrisrich.duckit.app

import android.app.Application
import com.chrisrich.duckit.di.duckItModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class DuckitApp : Application(){
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@DuckitApp)
            androidLogger()
            modules(duckItModule)
        }
    }
}