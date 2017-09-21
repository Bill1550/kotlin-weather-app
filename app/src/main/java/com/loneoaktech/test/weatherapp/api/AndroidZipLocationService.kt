package com.loneoaktech.test.weatherapp.api

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.location.Geocoder
import android.os.AsyncTask
import com.loneoaktech.test.weatherapp.di.AppContext
import com.loneoaktech.test.weatherapp.model.AsyncResource
import com.loneoaktech.test.weatherapp.model.ForecastLocation
import com.loneoaktech.test.weatherapp.model.ZipCode
import timber.log.Timber
import java.lang.ref.WeakReference

/**
 * The Android Geocoding api actually does a network access which can block, so it should
 * be run on a background thread which can create context leakage opportunities.
 * This is implemented as separate data source component to allow data injection to help
 * manage the context.
 * Also caches requests. (The google location lookup API seems to take longer than the DarkSky API)
 *
 * Created by BillH on 9/17/2017.
 */
class AndroidZipLocationService(@AppContext appContext : Context) : ForecastLocationService {
    private val _appContext = appContext.applicationContext // ensure that it is an app context.
    private val _result = MutableLiveData<AsyncResource<ForecastLocation>>()
    private val _cache = HashMap<ZipCode,ForecastLocation>()


    companion object {
        // Error codes
        const val NETWORK_ERROR = "Network Error"
        const val INVALID_ZIP = "Invalid Zip Code"
        const val GENERAL_ERROR = "General Error"
    }

    /**
     * The selected location. Set from the persisted value, can be reset by a lookup
     * initiated by a fromZipCode() call,
     */
    override val selectedLocation: LiveData<AsyncResource<ForecastLocation>> get()=_result


    /**
     * Initiates a location load from a zip code.
     */
    override fun selectZipCode(zip: ZipCode) {
        Timber.i("FromZipCode: %s", zip)

        // check cache
        _cache[zip]?.run{
            _result.postValue(AsyncResource.success(this))
            return
        }

        AsyncLoader(_appContext){_result.value=it}.execute(zip)
    }

    private class AsyncLoader constructor(context: Context, val handler: (AsyncResource<ForecastLocation>)->Unit)
        : AsyncTask<ZipCode,Int,AsyncResource<ForecastLocation>>() {
        private val _contextRef: WeakReference<Context> = WeakReference(context)

        override fun onPreExecute() {
           handler(AsyncResource.loading())
        }

        override fun doInBackground(vararg p0: ZipCode?): AsyncResource<ForecastLocation> {
            val zip = p0.firstOrNull() ?: return AsyncResource.error(INVALID_ZIP)

            val geo = Geocoder(_contextRef.get() ?: return AsyncResource.error(GENERAL_ERROR))
            try{
                val addresses = geo.getFromLocationName(zip.toString(), 1)
                if (addresses.isEmpty() )
                    return AsyncResource.error(INVALID_ZIP)

                with(addresses[0]){
                    Timber.i("address: %s", this)
                    if ((zip.toString() != postalCode) || (countryCode != "US") )
                        return AsyncResource.error(INVALID_ZIP)

                    return AsyncResource.success(ForecastLocation(getAddressLine(0),latitude, longitude, zip))
                }

            } catch (ex: Exception){
                Timber.e("Exception during reverse Geocode: %s", ex.message)
                return AsyncResource.error(NETWORK_ERROR)
            }
        }

        override fun onPostExecute(result: AsyncResource<ForecastLocation>) {
            Timber.i("onPostExecute: %s", result)
            handler(result)
        }
    }
}