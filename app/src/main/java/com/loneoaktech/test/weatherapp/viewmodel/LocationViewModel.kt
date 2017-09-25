package com.loneoaktech.test.weatherapp.viewmodel

import android.app.Application
import android.arch.lifecycle.*
import com.loneoaktech.test.weatherapp.model.AsyncResource
import com.loneoaktech.test.weatherapp.model.ForecastLocation
import com.loneoaktech.test.weatherapp.model.ZipCode
import com.loneoaktech.test.weatherapp.repository.SelectedLocationRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ViewModel of the selected forecast location.
 * Handles validating zip code and looking up location (to get Lat/Long for DarkSky)
 *
 * Created by BillH on 9/17/2017.
 */

// (Injection is handled by factory below, since the ViewModelProvider maintains the VM instances
class LocationViewModel
    constructor(private val _locationRepo: SelectedLocationRepository )
    : ViewModel(){

    private val _validateResult = MediatorLiveData<AsyncResource<ForecastLocation>>()


    val selectedLocation: LiveData<ForecastLocation>
        get() = _locationRepo.getSelectedLocation()

    /**
     *  The location back from validation/lookup, but not persisted.
     *  UI Controller (fragment) has to explicitly call selectLocation to make new value active.
     *  Ensures that persisted value only changes if fragment is still alive.
     */
    val validatedLocation: LiveData<ForecastLocation> =
            Transformations.map<AsyncResource<ForecastLocation>?, ForecastLocation>(_validateResult){
            when(it?.status){
                AsyncResource.Companion.Status.SUCCESS -> it.data
                else -> null
            }
        }

    val errorMessage: LiveData<String> =
            Transformations.map<AsyncResource<ForecastLocation>?,String>(_validateResult) {
            when(it?.status){
                AsyncResource.Companion.Status.ERROR -> it.msg  // TODO look up better message
                else ->  ""
            }
        }


     /**
     * Initiates a zip code validation
     */
    fun validateZipCode(zip: ZipCode){
        val validateSource = _locationRepo.validateZipCode(zip)
        _validateResult.addSource(validateSource){
            if ((it != null) && (it.status!=AsyncResource.Companion.Status.LOADING))
                _validateResult.removeSource(validateSource)
            _validateResult.value = it
        }
     }

    fun clearValidation() { _validateResult.value = null}

    /**
     * sets and persists the selected location value.
     */
    fun selectLocation(location: ForecastLocation){
        _locationRepo.selectLocation(location)
    }
}

@Singleton
class LocationViewModelFactory
    @Inject constructor(val app: Application, private val locationPRepo: SelectedLocationRepository)
    : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return if (modelClass == LocationViewModel::class.java)
            LocationViewModel(locationPRepo) as T
        else throw IllegalArgumentException("Unrecognized viewModel class")

    }
}