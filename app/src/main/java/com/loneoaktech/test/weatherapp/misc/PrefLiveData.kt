package com.loneoaktech.test.weatherapp.misc

import android.arch.lifecycle.LiveData
import android.content.SharedPreferences

/**
 * Live data that wraps a string preference in a lifecycle aware observer.
 *
 * Created by BillH on 9/17/2017.
 */
class PrefLiveData<T>(private val preferences: SharedPreferences, val key: String,
                      private val map: (p: SharedPreferences, k:String)->T?) : LiveData<T>() {
    init {
        if (preferences.contains(key))
            value = map(preferences, key)
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
}
