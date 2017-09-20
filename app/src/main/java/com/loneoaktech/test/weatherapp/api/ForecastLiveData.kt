package com.loneoaktech.test.weatherapp.api

import android.arch.lifecycle.LiveData
import com.johnhiott.darkskyandroidlib.RequestBuilder
import com.johnhiott.darkskyandroidlib.models.Request
import com.johnhiott.darkskyandroidlib.models.WeatherResponse
import com.loneoaktech.test.weatherapp.model.AsyncResource
import com.loneoaktech.test.weatherapp.model.DataPoint
import com.loneoaktech.test.weatherapp.model.Forecast
import com.loneoaktech.test.weatherapp.model.ForecastLocation
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response

/**
 *
 * Created by BillH on 9/19/2017.
 */
class ForecastLiveData(val location: ForecastLocation) : LiveData<AsyncResource<Forecast>>() {

    init {
        loadData()
    }


    private fun loadData(){
        val weatherApi = RequestBuilder()
        val request = Request().apply{
            lat = location.latitude.toString()
            lng = location.longitude.toString()
            units = Request.Units.US
            language = Request.Language.ENGLISH     // TODO get from locale
            addExcludeBlock(Request.Block.ALERTS)
            addExcludeBlock(Request.Block.MINUTELY)
        }

        weatherApi.getWeather(request, object: Callback<WeatherResponse>{
            override fun success(weatherResponse: WeatherResponse?, response: Response?) {
                weatherResponse?.let{ resp ->
                    value = AsyncResource.success(Forecast(location,System.currentTimeMillis(), copyDataPoint(resp.currently),
                            listOf(), listOf()))
                }
            }

            override fun failure(error: RetrofitError?) {
                value = AsyncResource.error(error?.message ?: "unspecified")
            }
        })

    }

    private fun copyDataPoint(source: com.johnhiott.darkskyandroidlib.models.DataPoint?): DataPoint? {
        return source?.let { s ->
            DataPoint(s.time, s.summary, s.icon, s.temperature, s.temperatureMin, s.temperatureMax)
        }
    }
}