package com.madarsoft.madartask

import android.app.Application
import com.madarsoft.madartask.di.appModule
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
