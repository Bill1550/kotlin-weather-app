package com.loneoaktech.test.weatherapp.repository

import android.arch.lifecycle.LiveData
import com.loneoaktech.test.weatherapp.STALE_FORECAST_DISPLAY_THRESHOLD_MS
import com.loneoaktech.test.weatherapp.STALE_FORECAST_UPDATE_THRESHOLD_MS
import com.loneoaktech.test.weatherapp.api.WeatherApiService
import com.loneoaktech.test.weatherapp.db.ForecastDao
import com.loneoaktech.test.weatherapp.model.AsyncResource
import com.loneoaktech.test.weatherapp.model.Forecast
import com.loneoaktech.test.weatherapp.model.ForecastLocation
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Implements a single source for forecast data, mediating between the network API and the local store.
 * Created by BillH on 9/20/2017.
 */
@Singleton
class WeatherRepository
        @Inject constructor(
        val _weatherService : WeatherApiService,
        val _weatherDB : ForecastDao
) {

    fun getForecast(location: ForecastLocation, forceUpdate: Boolean = false) : LiveData<AsyncResource<Forecast>> {
        Timber.i("Get forecast for %s, force=%b", location, forceUpdate)
        return object: NetworkBoundResource<Forecast>(){
            override fun saveApiResult(apiData: Forecast) {
                _weatherDB.insert(apiData)
            }

            override fun shouldFetch(persistedData: Forecast?): Boolean {
                persistedData?.let { Timber.w("Stale=%b force=%b",  it.time < (System.currentTimeMillis() - STALE_FORECAST_UPDATE_THRESHOLD_MS), forceUpdate)}
                return forceUpdate || (persistedData == null) || (persistedData.location != location)
                        || (persistedData.time < (System.currentTimeMillis() - STALE_FORECAST_UPDATE_THRESHOLD_MS))
            }

            override fun shouldDisplayWhileLoading(persistedData: Forecast): Boolean {
                val b = persistedData.time > (System.currentTimeMillis() - STALE_FORECAST_DISPLAY_THRESHOLD_MS)
                Timber.i("Forecast age=%d, should display=%b", System.currentTimeMillis()-persistedData.time, b)
                return b
            }

            override fun loadFromDb(): LiveData<Forecast> = _weatherDB.loadForecast(location)

            override fun initiateFetch(): LiveData<AsyncResource<Forecast>> = _weatherService.getForecast(location)

            override fun onFetchFailed() {
                Timber.w("fetch failed")
            }

            init { init() }

        }.getAsLiveData()
    }

}