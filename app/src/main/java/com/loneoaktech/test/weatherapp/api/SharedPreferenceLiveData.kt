package com.loneoaktech.test.weatherapp.api

import android.arch.lifecycle.LiveData
import android.content.SharedPreferences

/**
 * Live data that wraps a string preference in a lifecycle aware observer.
 *
 * Created by BillH on 9/17/2017.
 */
open class SharedPreferenceLiveData<T>(private val preferences: SharedPreferences, private val key: String,
                                  private val map: (p: SharedPreferences, k:String)->T?) : LiveData<T>() {
    init {
        if (preferences.contains(key))
            postValue(map(preferences, key))
        else
            postValue(null)
    }

    private val _listener : SharedPreferences.OnSharedPreferenceChangeListener =
            SharedPreferences.OnSharedPreferenceChangeListener { p, k ->
                if (key == k)
                    value = map(p,key)
            }

    override fun onActive() {
        preferences.registerOnSharedPreferenceChangeListener(_listener)
    }

    override fun onInactive() {
        preferences.unregisterOnSharedPreferenceChangeListener(_listener)
    }

    // Ensure that postValue can't be overridden so that it is safely callable from constructor
    final override fun postValue(t:T?) { super.postValue(t)}
}
