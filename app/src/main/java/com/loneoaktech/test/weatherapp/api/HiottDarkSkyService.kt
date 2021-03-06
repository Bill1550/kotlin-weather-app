package com.loneoaktech.test.weatherapp.api

import android.arch.lifecycle.LiveData
import com.johnhiott.darkskyandroidlib.ForecastApi
import com.johnhiott.darkskyandroidlib.RequestBuilder
import com.johnhiott.darkskyandroidlib.models.Request
import com.johnhiott.darkskyandroidlib.models.WeatherResponse
import com.loneoaktech.test.weatherapp.DARK_SKY_API_KEY
import com.loneoaktech.test.weatherapp.model.AsyncResource
import com.loneoaktech.test.weatherapp.model.DataPoint
import com.loneoaktech.test.weatherapp.model.Forecast
import com.loneoaktech.test.weatherapp.model.ForecastLocation
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response
import timber.log.Timber

/**
 *  Executes the DarkSky API requests
 *
 * Created by BillH on 9/18/2017.
 */


class HiottDarkSkyService() : WeatherApiService {

    init{
        ForecastApi.create(DARK_SKY_API_KEY)
    }

    override fun getForecast(location: ForecastLocation): LiveData<AsyncResource<Forecast>> = ForecastLiveData(location)


    // Wrapper that converts the johnhiott call to an AppResource based LiveData
    private class ForecastLiveData(val location: ForecastLocation) : LiveData<AsyncResource<Forecast>>() {

        init {
            Timber.i("initiating forecast load")
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

            weatherApi.getWeather(request, object: Callback<WeatherResponse> {
                override fun success(weatherResponse: WeatherResponse?, response: Response?) {
                    weatherResponse?.let{ resp ->
                        value = AsyncResource.success(Forecast(location,System.currentTimeMillis(), copyDataPoint(resp.currently),
                                    resp.hourly.data.mapNotNull{ sdp -> copyDataPoint(sdp)},
                                    resp.daily.data.mapNotNull{ sdp -> copyDataPoint(sdp)}))

                        Timber.i("Forecast fetched: %s", value)
                    }
                }

                override fun failure(error: RetrofitError?) {
                    value = AsyncResource.error(error?.message ?: "unspecified")
                }
            })
        }

        private fun copyDataPoint(source: com.johnhiott.darkskyandroidlib.models.DataPoint?): DataPoint? {
            return source?.let { s ->
                DataPoint(s.time*1000, s.summary, s.icon, s.temperature, s.temperatureMin, s.temperatureMax)
            }
        }
    }
}
