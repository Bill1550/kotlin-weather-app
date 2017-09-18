package com.loneoaktech.test.weatherapp.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.preference.PreferenceManager
import com.google.gson.Gson
import com.loneoaktech.test.weatherapp.api.ForecastLocationProvider
import com.loneoaktech.test.weatherapp.misc.PrefLiveData
import com.loneoaktech.test.weatherapp.model.AsyncResource
import com.loneoaktech.test.weatherapp.model.ForecastLocation
import com.loneoaktech.test.weatherapp.model.ZipCode
import timber.log.Timber

/**
 *
 * Created by BillH on 9/17/2017.
 */
const val KEY_SELECTED_LOCATION = "selected_location"

class LocationViewModel(app: Application) : AndroidViewModel(app){
    private val _gson = Gson()
    private val _locationProvider = ForecastLocationProvider(getApplication())
    private val _selectedLocation = PrefLiveData<ForecastLocation>(getSharedPreferences(), KEY_SELECTED_LOCATION){
        p,k->
        try {
            _gson.fromJson(p.getString(k, null), ForecastLocation::class.java)
        } catch (ex: Exception){
            null
        }
    }


//    init {
//        PreferenceManager.getDefaultSharedPreferences(getApplication()).getString(KEY_SELECTED_LOCATION, null)?.let {
//            try {
//                _selectedLocation.value = _gson.fromJson<ForecastLocation>(it, ForecastLocation::class.java)
//            } catch (ex: Exception){
//                Timber.e("Error decoding persisted location: %s", ex.message)
//            }
//        }
//    }


    val selectedLocation: LiveData<ForecastLocation>
        get() = _selectedLocation

    val validatedLocation: LiveData<ForecastLocation> =
            Transformations.map<AsyncResource<ForecastLocation>, ForecastLocation>(_locationProvider.result){
            when(it.status){
                AsyncResource.Companion.Status.SUCCESS -> it.data
                else -> null
            }
        }

    val errorMessage: LiveData<String> =
            Transformations.map<AsyncResource<ForecastLocation>,String>(_locationProvider.result) {
            when(it.status){
                AsyncResource.Companion.Status.ERROR -> it.msg  // TODO look up better message
                else ->  ""
            }
        }


     /**
     * Initiates a zip code validation
     */
    fun validateZipCode(zip: ZipCode) = _locationProvider.fromZipCode(zip)


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