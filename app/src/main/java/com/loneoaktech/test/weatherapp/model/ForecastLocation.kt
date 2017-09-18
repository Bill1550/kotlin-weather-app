package com.loneoaktech.test.weatherapp.model

/**
 * Data item to encapsulate a location for a forecast
 *
 * Created by BillH on 9/16/2017.
 */
data class ForecastLocation(  // Data classes automatically generate getters/setters, toString, equals, etc
        val title: String,
        val latitude: Double,
        val longitude: Double,
        val zipCode: ZipCode
    )

