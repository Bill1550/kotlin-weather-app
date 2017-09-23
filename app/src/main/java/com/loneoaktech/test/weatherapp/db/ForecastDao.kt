package com.loneoaktech.test.weatherapp.db

import android.arch.lifecycle.LiveData
import com.loneoaktech.test.weatherapp.model.Forecast
import com.loneoaktech.test.weatherapp.model.ForecastLocation

/**
 * Interface for the Forecast storage.
 * (facilites switching backing stores for testing, etc)
 *
 * Created by BillH on 9/22/2017.
 */
interface ForecastDao {

    fun loadForecast(location: ForecastLocation) : LiveData<Forecast>

    fun insert(forecast: Forecast)
}
