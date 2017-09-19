package com.loneoaktech.test.weatherapp.model

/**
 *
 * Created by BillH on 9/17/2017.
 */
data class DataPoint(
        val time: Long,
        val summary: String,
        val icon: String,
        val temperature: Double,
        val temperatureMin: Double,
        val temperatureMax: Double
)