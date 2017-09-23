package com.loneoaktech.test.weatherapp.db


import android.arch.lifecycle.LiveData
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.google.gson.Gson
import com.loneoaktech.test.weatherapp.api.SharedPreferenceLiveData
import com.loneoaktech.test.weatherapp.model.ForecastLocation
import timber.log.Timber

/**
 * A simple DAO that stores the selected location in shared prefs
 *
 * Created by BillH on 9/23/2017.
 */
const val KEY_SELECTED_LOCATION = "selected_location" // shared prefs key

class SharedPrefsSelectedLocationDao(applicationContext: Context) : SelectedLocationDao {
    private val _prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)


    override fun loadSelectedLocation(): LiveData<ForecastLocation>
            = LocationSharedPrefsLiveData(_prefs, KEY_SELECTED_LOCATION)

    override fun selectocation(location: ForecastLocation) {
        try{
            _prefs.edit()
                    .putString(KEY_SELECTED_LOCATION, Gson().toJson(location))
                    .apply()
        } catch ( ex: Exception){
            Timber.e("Exception when writing location to prefs: %s", ex.message)
        }
    }

    private class LocationSharedPrefsLiveData(prefs: SharedPreferences, key: String) :
            SharedPreferenceLiveData<ForecastLocation>(prefs, key, {p, k ->
                // read prefs and convert to target value
                run {
                    try {
                        Gson().fromJson(p.getString(k, null), ForecastLocation::class.java)
                    } catch (ex: Exception){
                        Timber.e("Exception when decoding location json: %s", ex.message)
                        null
                    }
                }
            })
}