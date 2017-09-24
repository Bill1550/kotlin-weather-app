package com.loneoaktech.test.weatherapp.updater

import android.app.job.JobParameters
import android.app.job.JobService
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.Observer
import com.loneoaktech.test.weatherapp.di.AppModule
import com.loneoaktech.test.weatherapp.di.DaggerAppComponent
import com.loneoaktech.test.weatherapp.di.weatherApp
import com.loneoaktech.test.weatherapp.model.AsyncResource
import com.loneoaktech.test.weatherapp.repository.SelectedLocationRepository
import com.loneoaktech.test.weatherapp.repository.WeatherRepository
import timber.log.Timber
import javax.inject.Inject

/**
 *
 * Created by BillH on 9/23/2017.
 */
class ForecastUpdateJobService : JobService(), LifecycleOwner{

    @Inject lateinit var _locationRepo: SelectedLocationRepository
    @Inject lateinit var _weatherRepo: WeatherRepository

    private val _lifeCycleRegistry = LifecycleRegistry(this)

    override fun onCreate() {
        super.onCreate()

        DaggerAppComponent.builder()
                .appModule(AppModule(weatherApp))
                .application(weatherApp)
                .build().inject(this)
    }

    override fun onStartJob(jobParams: JobParameters?): Boolean {
        Timber.i("--Updater start")
        _lifeCycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        val locationLD = _locationRepo.getSelectedLocation()
        locationLD.observe(this, Observer{ loc ->
            if (loc == null)
                jobFinished(jobParams, false)
            else
                _weatherRepo.getForecast(loc).observe(this, Observer{
                    it?.let{ async ->
                        when(async.status){
                            AsyncResource.Companion.Status.ERROR -> finishJob(jobParams)
                            AsyncResource.Companion.Status.SUCCESS -> {Timber.i("Success"); finishJob(jobParams)}
                            else -> { /* continue */}
                        }
                    }
                })
        })

        return true
    }

    override fun onStopJob(p0: JobParameters?): Boolean {
        Timber.i("updater stop")
        _lifeCycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        return false
    }

    override fun getLifecycle(): Lifecycle = _lifeCycleRegistry

    // Added to call lifCycleRegistry w/ stop.  onStopJob isn't called when jobFinished() is called.
    private fun finishJob(jobParams: JobParameters?){
        _lifeCycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        jobFinished(jobParams, true)
    }
}