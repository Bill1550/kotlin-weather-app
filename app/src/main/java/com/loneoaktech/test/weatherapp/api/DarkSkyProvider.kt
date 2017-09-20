package com.loneoaktech.test.weatherapp.api

import android.arch.lifecycle.LiveData
import android.content.Context
import com.johnhiott.darkskyandroidlib.ForecastApi
import com.loneoaktech.test.weatherapp.model.AsyncResource
import com.loneoaktech.test.weatherapp.model.Forecast
import com.loneoaktech.test.weatherapp.model.ForecastLocation

/**
 *  Executes the DarkSky API requests
 *
 * Created by BillH on 9/18/2017.
 */
const val DARK_SKY_API_KEY = "c87d6c62bfb0c97670c2f79eb05c699f"

class DarkSkyProvider(appContext: Context){
    private val _appContext = appContext.applicationContext  // Ensure that it is an app context

    init{
        ForecastApi.create(DARK_SKY_API_KEY)
    }

    fun getForecast(location: ForecastLocation): LiveData<AsyncResource<Forecast>> = ForecastLiveData(location)
}
