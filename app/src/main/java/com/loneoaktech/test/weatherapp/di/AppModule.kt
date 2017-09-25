package com.loneoaktech.test.weatherapp.di

import com.loneoaktech.test.weatherapp.*
import com.loneoaktech.test.weatherapp.api.AndroidZipLocationService
import com.loneoaktech.test.weatherapp.api.ForecastLocationService
import com.loneoaktech.test.weatherapp.api.HiottDarkSkyService
import com.loneoaktech.test.weatherapp.api.WeatherApiService
import com.loneoaktech.test.weatherapp.db.ForecastDao
import com.loneoaktech.test.weatherapp.db.SelectedLocationDao
import com.loneoaktech.test.weatherapp.db.SharedPrefsSelectedLocationDao
import com.loneoaktech.test.weatherapp.db.SharedPrfsForecastDao
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

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class AppContext


//@Module(includes = arrayOf(ViewModelModule::class)) - not used due to an internal error in KAPT, created individual ViewModelFactories w/ constructor injection
@Module
class AppModule(val app:WeatherAppApplication){

    @Provides @Singleton fun provideForecastLocationService() : ForecastLocationService = AndroidZipLocationService(app)

    @Provides @Singleton fun provideWeatherApiService() : WeatherApiService = HiottDarkSkyService(app)

    @Provides @Singleton fun provideSelectedLocationDao() : SelectedLocationDao = SharedPrefsSelectedLocationDao(app)

    @Provides @Singleton fun provideForecastDao() : ForecastDao = SharedPrfsForecastDao(app)
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

    @Suppress("unused")
    @ContributesAndroidInjector
    abstract fun contributeZipEntryFragment(): ZipEntryFragment

    @Suppress("unused")
    @ContributesAndroidInjector
    abstract fun contributeDataPointListFragment(): DataPointListFragment
}

