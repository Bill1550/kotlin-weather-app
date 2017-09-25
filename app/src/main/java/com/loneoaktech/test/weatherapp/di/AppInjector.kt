package com.loneoaktech.test.weatherapp.di

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import dagger.android.AndroidInjection
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector

/**
 *  A little extra injector plumbing to handle fragments.
 *
 * Created by BillH on 7/14/2017
 */

/**
 * Marker interface to indicate which fragments need injecting
 */
interface Injectable

object AppInjector{

    fun init(app: Application){
        app.registerActivityLifecycleCallbacks( object: Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(p0: Activity?, p1: Bundle?) {
                p0?.let{handleActivity(p0)}
            }

            override fun onActivityPaused(p0: Activity?) {}
            override fun onActivityDestroyed(p0: Activity?) {}
            override fun onActivityResumed(p0: Activity?) {}
            override fun onActivitySaveInstanceState(p0: Activity?, p1: Bundle?) {}
            override fun onActivityStarted(p0: Activity?) {}
            override fun onActivityStopped(p0: Activity?) {}
        })
    }

    private fun handleActivity(activity: Activity){
        if (activity is HasSupportFragmentInjector) {
            AndroidInjection.inject(activity)
        }

        (activity as? FragmentActivity)?.supportFragmentManager?.registerFragmentLifecycleCallbacks(
                object: FragmentManager.FragmentLifecycleCallbacks(){
                    override fun onFragmentCreated(fm: FragmentManager?, f: Fragment?, savedInstanceState: Bundle?) {
                        if (f is Injectable)
                            AndroidSupportInjection.inject(f)
                    }},
                true)

    }

}