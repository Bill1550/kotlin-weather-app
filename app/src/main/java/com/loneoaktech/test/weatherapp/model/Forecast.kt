package com.loneoaktech.test.weatherapp.model

/**
 *
 * Created by BillH on 9/17/2017.
 */
data class Forecast(
        val location: ForecastLocation,
        val time: Long,
        val currently: DataPoint?,
        val hourly: List<DataPoint>,
        val daily: List<DataPoint>
)