package com.loneoaktech.test.weatherapp

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.loneoaktech.test.weatherapp.api.DarkSkyProvider
import com.loneoaktech.test.weatherapp.model.AsyncResource
import com.loneoaktech.test.weatherapp.model.Forecast
import com.loneoaktech.test.weatherapp.model.ForecastLocation
import com.loneoaktech.test.weatherapp.ui.getWeatherIcon
import com.loneoaktech.test.weatherapp.viewmodel.LocationViewModel
import kotlinx.android.synthetic.main.fragment_forecast_summary.view.*
import kotlinx.android.synthetic.main.fragment_forecast_summary.*
import timber.log.Timber

/**
 * The "home" fragment. Shows the forecast summary and provides a button to initiate zip entry.
 *
 * Created by BillH on 9/18/2017.
 */
class ForecastSummaryFragment : Fragment() {
    private var _locationModel: LocationViewModel? = null
    private lateinit var _forecastProvider : DarkSkyProvider; // TODO remove, for test only

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _forecastProvider = DarkSkyProvider(context.applicationContext)
        return inflater.inflate(R.layout.fragment_forecast_summary, container, false).also{ rv ->
            rv.selectLocation.setOnClickListener({
                Timber.w("Click!")
                (activity as? MainActivity)?.showLocationEntryFragment()
            })

        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        _locationModel = ViewModelProviders.of(this).get(LocationViewModel::class.java)?.apply {
            selectedLocation.observe(this@ForecastSummaryFragment, Observer<ForecastLocation> {
                locationNameText.text = it?.title ?: "-----"

                it?.let { loc ->
                    _forecastProvider.getForecast(loc).observe(this@ForecastSummaryFragment, Observer { result ->
                        result?.let {
                            displayForecast(it)
                        }
                    })
                }
            })
        }
    }

    private fun displayForecast(forecastResource: AsyncResource<Forecast>) {
        when( forecastResource.status ){
            AsyncResource.Companion.Status.ERROR -> { } // display error
            AsyncResource.Companion.Status.LOADING -> {} // display spinner
            AsyncResource.Companion.Status.SUCCESS -> {
                // display results
                forecastResource.data?.currently?.let{ dp ->
                    currentSummary.text = dp.summary
                    currentTemperatureText.text = String.format("%.0f° F", dp.temperature)
                    weatherIcon.setImageResource(getWeatherIcon(dp.icon))
                }
            }
        }

    }
}