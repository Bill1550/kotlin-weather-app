package com.loneoaktech.test.weatherapp

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.loneoaktech.test.weatherapp.model.ForecastLocation
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

        _locationModel = ViewModelProviders.of(this).get(LocationViewModel::class.java)?.apply {
            selectedLocation.observe(this@ForecastSummaryFragment, Observer<ForecastLocation> {
                locationNameText.text = it?.title ?: "-----"
            })


        }
    }
}