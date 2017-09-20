package com.loneoaktech.test.weatherapp.api

import android.arch.lifecycle.LiveData
import android.content.Context
import com.johnhiott.darkskyandroidlib.ForecastApi
import com.loneoaktech.test.weatherapp.model.AsyncResource
import com.loneoaktech.test.weatherapp.model.Forecast
import com.loneoaktech.test.weatherapp.model.ForecastLocation

/**
 * Basic weather info service interface - facilitates replacing API library
 * Created by BillH on 9/20/2017.
 */
interface WeatherProvider {
    fun getForecast(location: ForecastLocation): LiveData<AsyncResource<Forecast>>
}