package com.loneoaktech.test.weatherapp.misc

import android.arch.lifecycle.ViewModel
import javax.inject.Inject
import android.arch.lifecycle.ViewModelProvider
import javax.inject.Provider
import javax.inject.Singleton



/**
 *
 * Created by BillH on 9/20/2017.
 */


//---------- Not currently used due to an internal compiler error in KAPT building the type map.
// created separate ViewModel factories for each VM using constructor injection.

@Singleton
class WeatherViewModelFactory
@Inject
constructor(private val creators: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>)
    : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // find a creator from the map generated by Dagger
        var creator: Provider<out ViewModel>? = creators[modelClass]
        if (creator == null) {
            for ((key, value) in creators) {
                if (modelClass.isAssignableFrom(key)) {
                    creator = value
                    break
                }
            }
        }
        if (creator == null) {
            throw IllegalArgumentException("unknown model class " + modelClass)
        }

        try {
            @Suppress("UNCHECKED_CAST")
            return creator.get() as T
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

    }
}