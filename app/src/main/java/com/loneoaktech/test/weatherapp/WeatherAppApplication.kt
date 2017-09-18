package com.loneoaktech.test.weatherapp

import android.app.Application
import timber.log.Timber

/**
 *  Overridden to initialize libraries.
 * Created by BillH on 9/16/2017.
 */
class WeatherAppApplication: Application(){
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG)
            Timber.plant(Timber.DebugTree())
    }
}