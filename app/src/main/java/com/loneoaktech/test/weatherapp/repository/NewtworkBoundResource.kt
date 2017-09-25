package com.loneoaktech.test.weatherapp.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.os.AsyncTask
import android.support.annotation.MainThread
import android.support.annotation.WorkerThread
import com.loneoaktech.test.weatherapp.misc.mustBeOrNull
import com.loneoaktech.test.weatherapp.model.AsyncResource
import timber.log.Timber
import java.lang.ref.WeakReference

/**
 * A LiveData that mediates between a network source and a database source.
 * Created by BillH on 9/22/2017.
 */
abstract class NetworkBoundResource<T>{

    // Called to insert the network API result into the database
    @WorkerThread
    protected abstract fun saveApiResult(apiData: T)

    // Called to see if data should be read from API
    @MainThread
    protected abstract fun shouldFetch(persistedData: T?): Boolean

    @MainThread
    protected abstract fun shouldDisplayWhileLoading(persistedData: T): Boolean

    // Called to load cached data from DB
    @WorkerThread
    protected abstract fun loadFromDb() : LiveData<T>

    // Called to start a read from the API
    @MainThread
    protected abstract fun initiateFetch() : LiveData<AsyncResource<T>>

    // Called when an API read fails. Used for updating strategy.
    @MainThread
    protected abstract fun onFetchFailed()

    fun getAsLiveData(): LiveData<AsyncResource<T>> = _result


    private val _result = AsyncResourceMediatorLiveData<T>()

    // Must be called by derived class constructor
    protected fun init() {
        _result.value = AsyncResource.loading(null)  // Display loading indication w/ no data
        val dbLiveData = loadFromDb()
        _result.addSource(dbLiveData){dbData: T?->
            _result.removeSource(dbLiveData)
            Timber.i("from DB: %s", dbData)
            if (shouldFetch(dbData)) {
                fetchFromNetwork(dbLiveData)
            }else
                _result.addSource(dbLiveData){dbNewData -> _result.value = AsyncResource.success(dbNewData)}
        }

    }

    private fun fetchFromNetwork(dbLiveData: LiveData<T>){
        val apiLiveData = initiateFetch()

        // Reattach DB data to mediator w/ loading indication. Will update immediately
        _result.addSource(dbLiveData, {newData: T? -> _result.value = AsyncResource.loading(newData?.mustBeOrNull { shouldDisplayWhileLoading(this) }) })

        _result.addSource(apiLiveData, { apiResponse ->
            _result.removeSource(apiLiveData)
            _result.removeSource(dbLiveData)
            apiResponse?.let{
                if (it.isSuccess())
                    saveResultAndReInit(it)
            }
        })
    }

    @MainThread
    private fun saveResultAndReInit(apiResponse : AsyncResource<T> ) {
        if (apiResponse.data != null) {
            AsyncLoader(this, apiResponse.data).execute()
        }
    }

    private class AsyncLoader<T>(netResource: NetworkBoundResource<T>, private val apiResponseData : T ) : AsyncTask<Void, Void, Void?>() {
        private val _ref = WeakReference(netResource)

        override fun doInBackground(vararg p0: Void?): Void? {
            _ref.get()?.saveApiResult(apiResponseData)
            return null
        }

        override fun onPostExecute(result: Void?) {
            _ref.get()?.let { res ->
                res._result.addSource(res.loadFromDb()) { newData -> res._result.value = AsyncResource.success(newData) }
            }
        }

    }

    private class AsyncResourceMediatorLiveData<T> : MediatorLiveData<AsyncResource<T>>(){
        override fun onActive() {
            super.onActive()
            Timber.i("onActive")
        }

        override fun onInactive() {
            super.onInactive()
            Timber.i("onInactive")
        }
    }

}