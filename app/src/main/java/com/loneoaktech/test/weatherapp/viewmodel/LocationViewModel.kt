package com.loneoaktech.test.weatherapp.viewmodel

import android.app.Application
import android.arch.lifecycle.*
import android.preference.PreferenceManager
import com.google.gson.Gson
import com.loneoaktech.test.weatherapp.api.ForecastLocationService
import com.loneoaktech.test.weatherapp.api.SharedPreferenceLiveData
import com.loneoaktech.test.weatherapp.model.AsyncResource
import com.loneoaktech.test.weatherapp.model.ForecastLocation
import com.loneoaktech.test.weatherapp.model.ZipCode
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ViewModel of the selected forecast location.
 * Handles validating zip code and looking up location (to get Lat/Long for DarkSky)
 *
 * Created by BillH on 9/17/2017.
 */
const val KEY_SELECTED_LOCATION = "selected_location"

class LocationViewModel
    constructor(app: Application, val _locationProvider:ForecastLocationService)
    : AndroidViewModel(app){

    private val _gson = Gson()

//    @Inject lateinit
//    var _locationProvider : ForecastLocationService = AndroidZipLocationService(getApplication())

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
    fun validateZipCode(zip: ZipCode) = _locationProvider.selectZipCode(zip)


    /**
     * sets and persists the selected location value.
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

@Singleton
class LocationViewModelFactory
    @Inject constructor(val app: Application, private val locationProvider: ForecastLocationService)
    : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return if (modelClass==LocationViewModel::class.java)
            LocationViewModel(app, locationProvider) as T
        else throw IllegalArgumentException("Unrecogmized class")

    }
}