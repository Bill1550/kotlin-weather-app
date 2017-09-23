package com.loneoaktech.test.weatherapp.model

/**
 * Generic class for a resouce (data item) that is loaded or validated asynchronsously
 * Created by BillH on 9/17/2017.
 */
data class AsyncResource<out T>
    constructor(val status: Status, val data: T?, val msg: String?)
{
    @Suppress("UNUSED_PARAMETER")
    companion object {
        enum class Status { SUCCESS, LOADING, ERROR }

        fun <T> success(data: T?): AsyncResource<T> = AsyncResource(Status.SUCCESS, data, null)
        fun <T> error(msg: String, data: T? = null): AsyncResource<T> = AsyncResource(Status.ERROR, null, msg)
        fun <T> loading(data: T? = null): AsyncResource<T> = AsyncResource(Status.LOADING, null, null)
    }

    fun isSuccess(): Boolean = status==Status.SUCCESS
}
