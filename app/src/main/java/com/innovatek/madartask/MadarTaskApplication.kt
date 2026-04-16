package com.innovatek.madartask

import android.app.Application
import com.innovatek.madartask.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MadarTaskApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MadarTaskApplication)
            modules(appModule)
        }
    }
}
