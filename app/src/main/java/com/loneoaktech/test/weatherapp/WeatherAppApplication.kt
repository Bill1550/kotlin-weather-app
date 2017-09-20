package com.loneoaktech.test.weatherapp

import android.app.Activity
import android.app.Application
import com.loneoaktech.test.weatherapp.di.AppComponent
import com.loneoaktech.test.weatherapp.di.AppInjector
import com.loneoaktech.test.weatherapp.di.AppModule
import com.loneoaktech.test.weatherapp.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import timber.log.Timber
import javax.inject.Inject

/**
 *  Overridden to initialize libraries.
 * Created by BillH on 9/16/2017.
 */
class WeatherAppApplication : Application(), HasActivityInjector {

    // fragment injection stuff
    @Inject lateinit var activityInjector: DispatchingAndroidInjector<Activity>

    override fun activityInjector(): AndroidInjector<Activity> = activityInjector


    private val component: AppComponent by lazy {
        DaggerAppComponent.builder()
                .application(this)
                .appModule(AppModule(this))
                .build()
    }


    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG)
            Timber.plant(Timber.DebugTree())

        // inject dependencies
        component.inject(this)
        AppInjector.init(this)
    }
}