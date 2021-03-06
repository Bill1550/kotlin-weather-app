package com.loneoaktech.test.weatherapp

import android.annotation.TargetApi
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.loneoaktech.test.weatherapp.updater.ForecastUpdateJobService
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

private const val UPDATER_JOB_ID = 142

class MainActivity : DaggerAppCompatActivity() {

    companion object {
        val TAG_FORECAST_SUMMARY = "forecast_summary"
        val TAG_FORECAST_DETAIL = "forecast_detail"
        val TAG_ZIP_ENTRY = "zip_entry"
    }

//    // Fragment injection stuff
//    @Inject
//    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>
//
//    override fun supportFragmentInjector(): AndroidInjector<Fragment> = dispatchingAndroidInjector


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

    // API for fragments
    fun showLocationEntryDialog(){
        ZipEntryFragment().show(supportFragmentManager, TAG_ZIP_ENTRY)
    }

    fun showPeriodList(period: DataPointListFragment.Period ){
        supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.animator.fade_in,R.animator.fade_out,R.animator.fade_in,R.animator.fade_out)
                .addToBackStack("HOME")
                .replace(R.id.fragment, DataPointListFragment.createFragment(period), TAG_FORECAST_DETAIL)
                .commit()
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



inline fun <T> T.applyIfAtLeastSdk(api: Int, block: T.()->Unit): T {
    if (Build.VERSION.SDK_INT>=api )  block()
    return this
}
