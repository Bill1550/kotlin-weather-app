package com.loneoaktech.test.weatherapp

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.loneoaktech.test.weatherapp.di.Injectable
import com.loneoaktech.test.weatherapp.model.AsyncResource
import com.loneoaktech.test.weatherapp.model.Forecast
import com.loneoaktech.test.weatherapp.repository.SelectedLocationRepository
import com.loneoaktech.test.weatherapp.repository.WeatherRepository
import com.loneoaktech.test.weatherapp.ui.getWeatherIcon
import com.loneoaktech.test.weatherapp.viewmodel.WeatherViewModel
import com.loneoaktech.test.weatherapp.viewmodel.WeatherViewModelFactory
import kotlinx.android.synthetic.main.fragment_forecast_summary.*
import kotlinx.android.synthetic.main.fragment_forecast_summary.view.*
import timber.log.Timber
import javax.inject.Inject

/**
 * The "home" fragment. Shows the forecast summary and provides a button to initiate zip entry.
 *
 * Created by BillH on 9/18/2017.
 */
class ForecastSummaryFragment : Fragment(), Injectable {
    private var _weatherViewModel: WeatherViewModel? = null


    @Inject
    lateinit var _weatherRepo: WeatherRepository

    @Inject
    lateinit var _locationRepo: SelectedLocationRepository

//    @Inject
//    lateinit var _locationViewModelFactory : LocationViewModelFactory

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_forecast_summary, container, false).also{ rv ->
            rv.selectLocation.setOnClickListener({
                Timber.w("Click!")
                (activity as? MainActivity)?.showLocationEntryFragment()
            })

        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        _weatherViewModel = WeatherViewModelFactory(_weatherRepo, _locationRepo).create().apply {
            forecast.observe(this@ForecastSummaryFragment, Observer { result ->
                result?.let{forecast -> displayForecast(forecast) }})
        }
    }

    private fun displayForecast(forecastResource: AsyncResource<Forecast>) {
        Timber.i("display forecast result: %s", forecastResource)
        when( forecastResource.status ){
            AsyncResource.Companion.Status.ERROR -> { } // display error
            AsyncResource.Companion.Status.LOADING -> {} // display spinner
            AsyncResource.Companion.Status.SUCCESS -> {
                // display results
                forecastResource.data?.let{ (location, _, currently) ->
                    locationNameText.text = location.title
                    currently?.let{ (_, summary, icon, temperature) ->
                        currentSummary.text = summary
                        currentTemperatureText.text = String.format("%.0f° F", temperature)
                        weatherIcon.setImageResource(getWeatherIcon(icon))
                    }
                }
            }
        }

    }
}