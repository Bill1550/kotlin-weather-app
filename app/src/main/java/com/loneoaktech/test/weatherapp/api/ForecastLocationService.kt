package com.loneoaktech.test.weatherapp.api

import android.arch.lifecycle.LiveData
import com.loneoaktech.test.weatherapp.model.AsyncResource
import com.loneoaktech.test.weatherapp.model.ForecastLocation
import com.loneoaktech.test.weatherapp.model.ZipCode

/**
 * Interface for component that translates Zip Codes into locations and maintains the
 * selected (default) location.
 *
 * Created by BillH on 9/20/2017.
 */
interface ForecastLocationService {

    fun getLocationFromZipCode(zip: ZipCode) : LiveData<AsyncResource<ForecastLocation>>
}