package com.loneoaktech.test.weatherapp.viewmodel

import android.arch.lifecycle.*
import com.loneoaktech.test.weatherapp.model.AsyncResource
import com.loneoaktech.test.weatherapp.model.Forecast
import com.loneoaktech.test.weatherapp.repository.SelectedLocationRepository
import com.loneoaktech.test.weatherapp.repository.WeatherRepository

/**
 * ViewModel for displaying a specific location's forecast
 *
 * Created by BillH on 9/19/2017.
 */
class WeatherViewModel constructor(locationRepo: SelectedLocationRepository, private val _weatherRepo : WeatherRepository) : ViewModel() {

    val forecast : LiveData<AsyncResource<Forecast>> = Transformations.switchMap(locationRepo.getSelectedLocation()){
        loc -> if (loc!=null) _weatherRepo.getForecast(loc) else MutableLiveData<AsyncResource<Forecast>>().apply{value= AsyncResource.loading()}
    }
}

class WeatherViewModelFactory
    constructor(private val weatherRep: WeatherRepository, private val locationRepo: SelectedLocationRepository)
    : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>?): T {
        if (modelClass != WeatherViewModel::class.java)
            throw IllegalArgumentException("incorrect ViewModel class, should be WeatherViewMOdel")

        @Suppress("UNCHECKED_CAST")
        return WeatherViewModel(locationRepo, weatherRep) as T
    }

    fun create() : WeatherViewModel = create(WeatherViewModel::class.java)
}