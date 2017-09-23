package com.loneoaktech.test.weatherapp.db

import android.content.Context
import android.arch.lifecycle.LiveData
import android.content.SharedPreferences
import com.google.gson.Gson
import com.loneoaktech.test.weatherapp.api.SharedPreferenceLiveData
import com.loneoaktech.test.weatherapp.model.Forecast
import com.loneoaktech.test.weatherapp.model.ForecastLocation
import timber.log.Timber


/**
 *
 * Created by BillH on 9/22/2017.
 */
private const val FORECAST_SHARED_PREFS = "forecast_shared_prefs"
private const val KEY_CURRENT_FORECAST = "current_forecast"




class SharedPrfsForecastDao(applicationContext: Context) : ForecastDao {
    private val _prefs = applicationContext.getSharedPreferences(FORECAST_SHARED_PREFS, Context.MODE_PRIVATE)

    override fun loadForecast(location: ForecastLocation): LiveData<Forecast>
            = ForecastSharedPrefsLiveData(location, _prefs, KEY_CURRENT_FORECAST)

    override fun insert(forecast: Forecast) {
        try {
            _prefs.edit()
                    .putString(KEY_CURRENT_FORECAST, Gson().toJson(forecast))
                    .apply()
        } catch (e: Exception) {
            Timber.e("Exception when writing forecast to prefs: %s", e.message)
        }
    }

    // Contained classes are like Java static classes, they don't hold a reference to the outer object
    private class ForecastSharedPrefsLiveData(var requestedLocation: ForecastLocation, prefs: SharedPreferences, key: String) :
        SharedPreferenceLiveData<Forecast>(prefs, key, {p, k ->
            Timber.i("Attempting to load for %s", requestedLocation)
            run {
                try {
                    Gson().fromJson(p.getString(k, null), Forecast::class.java)
                } catch (ex: Exception) {
                    Timber.e("Exception when decoding json: %s", ex.message)
                    null
                }
            }
            ?.mustBe {location==requestedLocation}
        })

}

fun<T> T.mustBe(predicate: T.()->Boolean ) : T? = if (predicate(this)) this else null