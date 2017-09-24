package com.loneoaktech.test.weatherapp.viewmodel

import android.arch.lifecycle.*
import com.loneoaktech.test.weatherapp.model.AsyncResource
import com.loneoaktech.test.weatherapp.model.Forecast
import com.loneoaktech.test.weatherapp.model.ForecastLocation
import com.loneoaktech.test.weatherapp.repository.SelectedLocationRepository
import com.loneoaktech.test.weatherapp.repository.WeatherRepository

/**
 * ViewModel for displaying a specific location's forecast
 *
 * Created by BillH on 9/19/2017.
 */
class WeatherViewModel constructor(locationRepo: SelectedLocationRepository, private val _weatherRepo : WeatherRepository) : ViewModel() {

    // class used to tag requests that should force an immediate update.
    private class FlaggedLocation(val loc: ForecastLocation?, val forceUpdate: Boolean)

    private val _selectedLocationLD = locationRepo.getSelectedLocation()
    private val _locationMirror = MediatorLiveData<FlaggedLocation>().apply{
        addSource(_selectedLocationLD){newLocation -> this.value = FlaggedLocation(newLocation, false)}
    }

    private var _refreshPending = false

    val forecast : LiveData<AsyncResource<Forecast>> = Transformations.switchMap(_locationMirror){
        fl -> if (fl?.loc!=null) _weatherRepo.getForecast(fl.loc, fl.forceUpdate) else MutableLiveData<AsyncResource<Forecast>>().apply{value= AsyncResource.loading()}
    }

    fun refresh() {
        // add and remove source, it should then update immediately
        _refreshPending=true
        _locationMirror.removeSource(_selectedLocationLD)
        _locationMirror.addSource(_selectedLocationLD){newLocation ->
            _locationMirror.value = FlaggedLocation(newLocation, _refreshPending)
            _refreshPending=false   // clear, so an update is not forced on another request, such as after a config change
        }
    }

}

class WeatherViewModelFactory
    constructor(private val weatherRep: WeatherRepository, private val locationRepo: SelectedLocationRepository)
    : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>?): T {
        if (modelClass != WeatherViewModel::class.java)
            throw IllegalArgumentException("incorrect ViewModel class, should be WeatherViewModel")

        @Suppress("UNCHECKED_CAST")
        return WeatherViewModel(locationRepo, weatherRep) as T
    }

    fun create() : WeatherViewModel = create(WeatherViewModel::class.java)
}