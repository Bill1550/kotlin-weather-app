package com.loneoaktech.test.weatherapp

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.Observer
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import com.loneoaktech.test.weatherapp.di.Injectable
import com.loneoaktech.test.weatherapp.misc.formatTimeAsHour
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


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_forecast_summary, container, false).also{ rv ->
            rv.selectLocation.setOnClickListener({
                (activity as? MainActivity)?.showLocationEntryDialog()
            })
            rv.darkSkyAttributionText.setOnClickListener({
                startActivity( Intent(Intent.ACTION_VIEW).apply{data= Uri.parse(getString(R.string.dark_sky_url))})
            })
            rv.refreshIcon.setOnClickListener({
                _weatherViewModel?.refresh()
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
            AsyncResource.Companion.Status.LOADING -> {
                // display spinner
                loadingShade.visibility= if(forecastResource.data==null) VISIBLE else INVISIBLE
                loadForecastViews(forecastResource.data)
            }
            AsyncResource.Companion.Status.SUCCESS -> {
                loadingShade.visibility = View.INVISIBLE
                loadForecastViews(forecastResource.data)
            }
        }
    }

    @Suppress("Destructure") // destructuring "f" below seems to work against clarity
    private fun loadForecastViews(forecast : Forecast?) {
        // display results
        forecast?.let { f ->
            locationNameText.text = f.location.title
            forecastTime.text = getString(R.string.forecast_as_of_time, context.formatTimeAsHour(f.time))
            f.currently?.let { dp ->
                currentSummary.text = dp.summary
                currentTemperatureText.text = String.format("%.0f° F", dp.temperature)
                weatherIcon.setImageResource(getWeatherIcon(dp.icon))
            }


        }
    }

//    private fun startRefreshTimer() {
//        view?.removeCallbacks(refreshRunnable)
//        if (lifecycle.currentState == Lifecycle.State.RESUMED)
//            view?.postDelayed(refreshRunnable, ACTIVE_DISPLAY_UPDATE_PERIOD_MS)
//    }
//
//    private val refreshRunnable = Runnable { _weatherViewModel?.refresh(); startRefreshTimer()}
}