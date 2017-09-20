package com.loneoaktech.test.weatherapp.di

import com.loneoaktech.test.weatherapp.ForecastSummaryFragment
import com.loneoaktech.test.weatherapp.MainActivity
import com.loneoaktech.test.weatherapp.WeatherAppApplication
import com.loneoaktech.test.weatherapp.api.ForecastLocationService
import com.loneoaktech.test.weatherapp.api.HiottDarkSkyService
import com.loneoaktech.test.weatherapp.api.WeatherApiService
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * The Dagger definitions of modules that can be injected.
 *
 * Created by BillH on 9/20/2017.
 */
//@Qualifier
//@Retention(AnnotationRetention.RUNTIME)
//annotation class AppContext


@Module
class AppModule(val app:WeatherAppApplication){

    @Provides @Singleton fun provideForecastLocationService() : ForecastLocationService = ForecastLocationService(app)

    @Provides @Singleton fun provideWeatherApiService() : WeatherApiService = HiottDarkSkyService(app)
}

@Module
abstract class ActivitiesModule {

    @Suppress("unused")
    @ContributesAndroidInjector(modules = arrayOf(FragmentBuilderModule::class))
    abstract fun contributeMainActivity(): MainActivity
}


@Module
abstract class FragmentBuilderModule {

    @Suppress("unused")
    @ContributesAndroidInjector
    abstract fun contributeForecastSummaryFragment(): ForecastSummaryFragment
}

