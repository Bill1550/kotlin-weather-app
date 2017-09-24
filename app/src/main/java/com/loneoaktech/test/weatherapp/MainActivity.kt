package com.loneoaktech.test.weatherapp

import android.annotation.TargetApi
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.loneoaktech.test.weatherapp.updater.ForecastUpdateJobService
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import javax.inject.Inject

private const val UPDATER_JOB_ID = 142

class MainActivity : AppCompatActivity(), HasSupportFragmentInjector {

    companion object {
        val TAG_FORECAST_SUMMARY = "forecast_summary"
        val TAG_ZIP_ENTRY = "zip_entry"
    }

    // Fragment injection stuff
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = dispatchingAndroidInjector


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.visibility = View.GONE

        if (savedInstanceState == null){
            val fragment = ForecastSummaryFragment()
            supportFragmentManager.beginTransaction()
                    .add(R.id.fragment, fragment, TAG_FORECAST_SUMMARY)
                    .commit()
        }

        startUpdaterJob()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }

    fun showLocationEntryDialog(){
        ZipEntryFragment().show(supportFragmentManager, TAG_ZIP_ENTRY)
    }


    @TargetApi(Build.VERSION_CODES.O)
    private fun startUpdaterJob() {
        (getSystemService(Context.JOB_SCHEDULER_SERVICE) as? JobScheduler)?.let { js ->
            if (js.allPendingJobs.find { it.id == UPDATER_JOB_ID } == null) {
                Timber.i("Scheduling updater job)")
                js.schedule(JobInfo.Builder(UPDATER_JOB_ID, ComponentName(this@MainActivity, ForecastUpdateJobService::class.java))
                        .setPeriodic(UPDATE_JOB_PERIOD)
                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                        .applyIfAtLeastSdk(26){setRequiresBatteryNotLow(true)}
                        .build())
            }
        }
    }
}


inline fun <T> T.applyIf(condition: Boolean, block: T.()-> Unit ): T{ if (condition) block(); return this }

inline fun <T> T.applyIfAtLeastSdk(api: Int, block: T.()->Unit): T {
    if (Build.VERSION.SDK_INT>=api )  block()
    return this
}
