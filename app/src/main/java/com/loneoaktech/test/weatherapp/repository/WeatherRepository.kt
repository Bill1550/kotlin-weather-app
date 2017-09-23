package com.loneoaktech.test.weatherapp.repository

import android.arch.lifecycle.LiveData
import com.loneoaktech.test.weatherapp.api.WeatherApiService
import com.loneoaktech.test.weatherapp.db.ForecastDao
import com.loneoaktech.test.weatherapp.model.AsyncResource
import com.loneoaktech.test.weatherapp.model.Forecast
import com.loneoaktech.test.weatherapp.model.ForecastLocation
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

private const val STALE_FORCAST_THRESHOLD_MS = 15*60*1000  // TODO move to constant params file

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

    fun getForecast(location: ForecastLocation) : LiveData<AsyncResource<Forecast>> {
        Timber.i("Get forecast for %s", location)
        return object: NetworkBoundResource<Forecast>(){
            override fun saveApiResult(apiData: Forecast) {
                _weatherDB.insert(apiData)
            }

            override fun shouldFetch(persistedData: Forecast?): Boolean =
                    (persistedData==null) || (persistedData.location!=location)
                            || (persistedData.time < System.currentTimeMillis() - STALE_FORCAST_THRESHOLD_MS)

            override fun loadFromDb(): LiveData<Forecast> = _weatherDB.loadForecast(location)

            override fun initiateFetch(): LiveData<AsyncResource<Forecast>> = _weatherService.getForecast(location)

            override fun onFechFailed() {
                Timber.w("fetch failed")
            }

            init { init() }

        }.getAsLiveData()
    }

}