package com.loneoaktech.test.weatherapp.repository

import android.arch.lifecycle.LiveData
import com.loneoaktech.test.weatherapp.api.ForecastLocationService
import com.loneoaktech.test.weatherapp.db.SelectedLocationDao
import com.loneoaktech.test.weatherapp.model.AsyncResource
import com.loneoaktech.test.weatherapp.model.ForecastLocation
import com.loneoaktech.test.weatherapp.model.ZipCode
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Simple repository that maintains the selected location, and handles zip validation and lookup.
 *
 * Although the selected location could be set by a successful validation, it is decoupled to allow
 * UI the decision to maket he change.
 *
 * Created by BillH on 9/23/2017.
 */
@Singleton
class SelectedLocationRepository
    @Inject constructor(
            val _locationService : ForecastLocationService,
            val _locationDB : SelectedLocationDao
    ){

    fun getSelectedLocation() : LiveData<ForecastLocation> = _locationDB.loadSelectedLocation()

    fun validateZipCode(zip: ZipCode) : LiveData<AsyncResource<ForecastLocation>> =
        _locationService.getLocationFromZipCode(zip)

    fun selectLocation(location: ForecastLocation){
        _locationDB.selectLocation(location)
    }
}






