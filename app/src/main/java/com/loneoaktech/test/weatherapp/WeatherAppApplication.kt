package com.loneoaktech.test.weatherapp

import com.loneoaktech.test.weatherapp.di.AppComponent
import com.loneoaktech.test.weatherapp.di.AppModule
import com.loneoaktech.test.weatherapp.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import timber.log.Timber

/**
 *  Overridden to initialize libraries.
 * Created by BillH on 9/16/2017.
 */
class WeatherAppApplication : DaggerApplication() {

    private val component: AppComponent by lazy {
        DaggerAppComponent.builder()
                .application(this)
                .appModule(AppModule(this))
                .build()
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> = component


    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG)
            Timber.plant(Timber.DebugTree())
    }
}