package com.loneoaktech.test.weatherapp.di

import android.app.Application
import com.loneoaktech.test.weatherapp.WeatherAppApplication
import com.loneoaktech.test.weatherapp.viewmodel.LocationViewModel
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
@Component(modules = arrayOf(
        AndroidSupportInjectionModule::class,
        AppModule::class,
//        ApiModule::class,
        ActivitiesModule::class
))
interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance fun application(application: Application): Builder
        fun appModule(appModule: AppModule): Builder
        fun build(): AppComponent
    }

    fun inject(app: WeatherAppApplication)
    fun inject(locationModel: LocationViewModel)
}