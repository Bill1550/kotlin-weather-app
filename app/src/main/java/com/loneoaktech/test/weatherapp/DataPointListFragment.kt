package com.loneoaktech.test.weatherapp

import android.arch.lifecycle.Observer
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.loneoaktech.test.weatherapp.di.Injectable
import com.loneoaktech.test.weatherapp.model.AsyncResource
import com.loneoaktech.test.weatherapp.model.Forecast
import com.loneoaktech.test.weatherapp.repository.SelectedLocationRepository
import com.loneoaktech.test.weatherapp.repository.WeatherRepository
import com.loneoaktech.test.weatherapp.ui.DataPointRecyclerViewAdapter
import com.loneoaktech.test.weatherapp.viewmodel.WeatherViewModel
import com.loneoaktech.test.weatherapp.viewmodel.WeatherViewModelFactory
import kotlinx.android.synthetic.main.fragment_forecast_data_point_list.*
import kotlinx.android.synthetic.main.fragment_forecast_data_point_list.view.*
import kotlinx.android.synthetic.main.list_item_daily_forcast.view.*
import kotlinx.android.synthetic.main.list_item_hourly_forecast.view.*
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * Display a list of forecast DataPoints
 *
 * Created by BillH on 9/24/2017.
 */
class DataPointListFragment : Fragment(), Injectable {
    enum class Period {HOURS, DAYS}



    private var _weatherViewModel: WeatherViewModel? = null

    @Inject
    lateinit var _weatherRepo: WeatherRepository

    @Inject
    lateinit var _locationRepo: SelectedLocationRepository

    private lateinit var _periodListAdapter: DataPointRecyclerViewAdapter

    private lateinit var _period : Period

    companion object {
        private val ARG_PERIOD = "period"

        fun createFragment(period: Period): DataPointListFragment =
            DataPointListFragment().apply{
                arguments = Bundle().apply{ putString(ARG_PERIOD, period.name)} }

        private val hourFormat = SimpleDateFormat("hh:mm aa", Locale.US)
        private val dayFormat = SimpleDateFormat("EEEEEE", Locale.US)
    }


    @Suppress("Destructure") // Destructuring the DataPoint class calls isn't too clean
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _period = Period.valueOf(arguments.getString(ARG_PERIOD, Period.DAYS.name))
        return inflater.inflate(R.layout.fragment_forecast_data_point_list, container, false).also { rv ->
            rv.darkSkyAttributionText.setOnClickListener({
                startActivity(Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(getString(R.string.dark_sky_url)) })
            })

            rv.periodLabel.text = when(_period){
                Period.HOURS -> getString(R.string.label_hours)
                Period.DAYS -> getString(R.string.label_days)
            }

            _periodListAdapter = when (_period){
                Period.DAYS ->  DataPointRecyclerViewAdapter(context, R.layout.list_item_daily_forcast){ iv, dp ->
                    iv.dayNameText.text = dayFormat.format(Date(dp.time))
                    iv.daySummaryText.text = dp.summary
                    iv.maxTemperatureText.text = getString(R.string.temperature_format, dp.temperatureMax)
                    iv.minTemperatureText.text = getString(R.string.temperature_format, dp.temperatureMin)
                }

                Period.HOURS -> DataPointRecyclerViewAdapter(context, R.layout.list_item_hourly_forecast){iv, dp ->
                    iv.hourNameText.text = hourFormat.format(Date(dp.time))
                    iv.hourSummaryText.text = dp.summary
                    iv.temperatureText.text = getString(R.string.temperature_format, dp.temperature)
                }
            }

            with(rv.periodRecyclerView){
                adapter = _periodListAdapter
                layoutManager = LinearLayoutManager(context)
                setHasFixedSize(true)
            }

            rv.backIcon.setOnClickListener{ activity.onBackPressed()}

            rv.loadingShade.visibility = View.INVISIBLE // don't need shade
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        _weatherViewModel = WeatherViewModelFactory(_weatherRepo, _locationRepo).create().apply {
            forecast.observe(this@DataPointListFragment, Observer { result ->
                result?.let{forecast -> displayForecast(forecast) }})
        }
    }

    private fun displayForecast(forecastResource: AsyncResource<Forecast>) {
        Timber.i("display forecast result: %s", forecastResource)
        when( forecastResource.status ){
            AsyncResource.Companion.Status.ERROR -> { } // display error
            AsyncResource.Companion.Status.LOADING -> {
                // display spinner
//                loadingShade.visibility= if(forecastResource.data==null) View.VISIBLE else View.INVISIBLE
                loadForecastViews(forecastResource.data)
            }
            AsyncResource.Companion.Status.SUCCESS -> {
                loadingShade.visibility = View.INVISIBLE
                loadForecastViews(forecastResource.data)
            }
        }
    }

    private fun loadForecastViews(forecast : Forecast?) {
        // display results
        forecast?.let { f ->
            locationNameText.text = f.location.title

            Timber.w("forecast, num days=%d, num hours=%d", forecast.daily.size,forecast.hourly.size)

            _periodListAdapter.setData(when(_period){
                Period.HOURS ->{forecast.hourly.take(DETAIL_SCREEN_HOURS_TO_DISPLAY)}
                Period.DAYS ->{forecast.daily.take(DETAIL_SCREEN_DAYS_TO_DISPLAY)}
            })
        }
    }


}