package com.loneoaktech.test.weatherapp.di

import android.app.Application
import android.content.Context
import com.loneoaktech.test.weatherapp.WeatherAppApplication
import com.loneoaktech.test.weatherapp.updater.ForecastUpdateJobService
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

/**
 * Specifies the components that have dependencies needing injection.
 *
 * Created by BillH on 9/20/2017.
 */

@Singleton
@Component(modules = [ AndroidSupportInjectionModule::class,
                       AppModule::class,
                       ActivitiesModule::class
                     ])
interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance fun application(application: Application): Builder
        fun appModule(appModule: AppModule): Builder
        fun build(): AppComponent
    }

    fun inject(app: WeatherAppApplication)
    fun inject(jobSvc: ForecastUpdateJobService)
}

val Context.weatherApp : WeatherAppApplication
    get() = this.applicationContext as WeatherAppApplication