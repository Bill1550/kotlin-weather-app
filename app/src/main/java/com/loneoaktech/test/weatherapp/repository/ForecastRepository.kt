package com.loneoaktech.test.weatherapp.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.loneoaktech.test.weatherapp.model.AsyncResource
import com.loneoaktech.test.weatherapp.model.Forecast
import com.loneoaktech.test.weatherapp.model.ForecastLocation

/**
 * Implements a single source for forecast data, mediating between the network API and the local store.
 * Created by BillH on 9/20/2017.
 */
class ForecastRepository() {

    fun getForecast(location: ForecastLocation) : LiveData<AsyncResource<Forecast>> {
        return MutableLiveData<AsyncResource<Forecast>>() // TODO replace, temp
    }

}