package com.loneoaktech.test.weatherapp

import android.arch.lifecycle.Observer
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import com.loneoaktech.test.weatherapp.di.Injectable
import com.loneoaktech.test.weatherapp.misc.formatTimeAsHour
import com.loneoaktech.test.weatherapp.model.AsyncResource
import com.loneoaktech.test.weatherapp.model.Forecast
import com.loneoaktech.test.weatherapp.model.ForecastLocation
import com.loneoaktech.test.weatherapp.repository.SelectedLocationRepository
import com.loneoaktech.test.weatherapp.repository.WeatherRepository
import com.loneoaktech.test.weatherapp.ui.DataPointRecyclerViewAdapter
import com.loneoaktech.test.weatherapp.ui.getWeatherIcon
import com.loneoaktech.test.weatherapp.viewmodel.WeatherViewModel
import com.loneoaktech.test.weatherapp.viewmodel.WeatherViewModelFactory
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_forecast_summary.*
import kotlinx.android.synthetic.main.fragment_forecast_summary.view.*
import kotlinx.android.synthetic.main.list_item_daily_forcast.view.*
import kotlinx.android.synthetic.main.list_item_hourly_forecast.view.*
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * The "home" fragment. Shows the forecast summary and provides a button to initiate zip entry.
 *
 * Created by BillH on 9/18/2017.
 */
class ForecastSummaryFragment : DaggerFragment() {
    private var _weatherViewModel: WeatherViewModel? = null

    @Inject
    lateinit var _weatherRepo: WeatherRepository

    @Inject
    lateinit var _locationRepo: SelectedLocationRepository

    lateinit var _hourlyAdapter: DataPointRecyclerViewAdapter
    lateinit var _dailyAdapter: DataPointRecyclerViewAdapter
    var _displayedLocation: ForecastLocation? = null

    companion object {
        private val hourFormat = SimpleDateFormat("hh:mm aa", Locale.US)
        private val dayFormat = SimpleDateFormat("EEEEEE", Locale.US)
    }

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

            // configure hours list
            _hourlyAdapter = DataPointRecyclerViewAdapter(context, R.layout.list_item_hourly_forecast){ iv, dp ->
                iv.hourNameText.text = hourFormat.format(Date(dp.time))
                iv.hourSummaryText.text = dp.summary
                iv.temperatureText.text = getString(R.string.temperature_format, dp.temperature)
                iv.setOnClickListener{ (activity as? MainActivity)?.showPeriodList(DataPointListFragment.Period.HOURS)}
            }

            with(rv.hourlyRecyclerView){
                layoutManager = LinearLayoutManager(context)
                adapter = _hourlyAdapter
                setHasFixedSize(true)
            }

            // configure days list
            _dailyAdapter = DataPointRecyclerViewAdapter(context, R.layout.list_item_daily_forcast){ iv, dp ->
                iv.dayNameText.text = dayFormat.format(Date(dp.time))
                iv.daySummaryText.text = dp.summary
                iv.maxTemperatureText.text = getString(R.string.temperature_format, dp.temperatureMax)
                iv.minTemperatureText.text = getString(R.string.temperature_format, dp.temperatureMin)
                iv.setOnClickListener{(activity as? MainActivity)?.showPeriodList(DataPointListFragment.Period.DAYS)}
            }

            with(rv.dailyRecyclerView){
                layoutManager = LinearLayoutManager(context)
                adapter = _dailyAdapter
                setHasFixedSize(true)
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        _weatherViewModel = WeatherViewModelFactory(_weatherRepo, _locationRepo).create().apply {
            forecast.observe(this@ForecastSummaryFragment, Observer { result ->
                result?.let{forecast -> displayForecast(forecast) }})
        }
    }

    override fun onStart() {
        super.onStart()

        // Display zip entry dialog if no zipcode is selected
        _weatherViewModel?.let {
            it.selectedLocation.observe(this@ForecastSummaryFragment, Observer { loc ->
                if (loc == null)
                    (activity as? MainActivity)?.showLocationEntryDialog()
            })
        }
    }

    private fun displayForecast(forecastResource: AsyncResource<Forecast>) {
        Timber.i("display forecast result: %s", forecastResource)
        when( forecastResource.status ){
            AsyncResource.Companion.Status.ERROR -> { } // display error
            AsyncResource.Companion.Status.LOADING -> {
                // display spinner
                loadingShade.visibility= if(forecastResource.data?.location!=_weatherViewModel?.selectedLocation?.value) VISIBLE else INVISIBLE
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
            _displayedLocation = f.location
            locationNameText.text = f.location.title
            forecastTime.text = getString(R.string.forecast_as_of_time, context.formatTimeAsHour(f.time))
            f.currently?.let { dp ->
                currentSummary.text = dp.summary
                currentTemperatureText.text = getString(R.string.temperature_format, dp.temperature)
                weatherIcon.setImageResource(getWeatherIcon(dp.icon))
            }
            _hourlyAdapter.setData(f.hourly.take(HOME_SCREEN_HOURS_TO_DISPLAY) )
            _dailyAdapter.setData(f.daily.take(HOME_SCREEN_DAYS_TO_DISPLAY))
        }
    }
}