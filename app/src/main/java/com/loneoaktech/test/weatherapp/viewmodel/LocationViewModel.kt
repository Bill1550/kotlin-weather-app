package com.loneoaktech.test.weatherapp.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.preference.PreferenceManager
import com.google.gson.Gson
import com.loneoaktech.test.weatherapp.api.ForecastLocationService
import com.loneoaktech.test.weatherapp.api.SharedPreferenceLiveData
import com.loneoaktech.test.weatherapp.model.AsyncResource
import com.loneoaktech.test.weatherapp.model.ForecastLocation
import com.loneoaktech.test.weatherapp.model.ZipCode
import timber.log.Timber

/**
 * ViewModel of the selected forecast location.
 * Handles validating zip code and looking up location (to get Lat/Long for DarkSky)
 *
 * Created by BillH on 9/17/2017.
 */
const val KEY_SELECTED_LOCATION = "selected_location"

class LocationViewModel(app: Application) : AndroidViewModel(app){
    private val _gson = Gson()
    private val _locationProvider = ForecastLocationService(getApplication())

    // Use a LiveData to track the preference. Ensures that active value always matches persisted value.
    private val _selectedLocation = SharedPreferenceLiveData(getSharedPreferences(), KEY_SELECTED_LOCATION) { p, k ->
        try {
            _gson.fromJson(p.getString(k, null), ForecastLocation::class.java)
        } catch (ex: Exception) {
            null
        }
    }


    val selectedLocation: LiveData<ForecastLocation>
        get() = _selectedLocation

    /**
     *  The location back from validation/lookup, but not persisted.
     *  UI Controller (fragment) has to explicitly call selectLocation to make new value active.
     *  Ensures that persisted value only changes if fragment is still alive.
     */
    val validatedLocation: LiveData<ForecastLocation> =
            Transformations.map<AsyncResource<ForecastLocation>, ForecastLocation>(_locationProvider.selectedLocation){
            when(it.status){
                AsyncResource.Companion.Status.SUCCESS -> it.data
                else -> null
            }
        }

    val errorMessage: LiveData<String> =
            Transformations.map<AsyncResource<ForecastLocation>,String>(_locationProvider.selectedLocation) {
            when(it.status){
                AsyncResource.Companion.Status.ERROR -> it.msg  // TODO look up better message
                else ->  ""
            }
        }


     /**
     * Initiates a zip code validation
     */
    fun validateZipCode(zip: ZipCode) = _locationProvider.fromZipCode(zip)


    /**
     * sets and presists the selected location value.
     */
    fun selectLocation(location: ForecastLocation){
//        _selectedLocation.value = location

        // persist
        val locationJson = _gson.toJson(location)
        Timber.i("Location json: %s", locationJson)

        getSharedPreferences()
                .edit()
                .putString(KEY_SELECTED_LOCATION, locationJson)
                .apply()
    }

    private fun getSharedPreferences() = PreferenceManager.getDefaultSharedPreferences(getApplication())
}