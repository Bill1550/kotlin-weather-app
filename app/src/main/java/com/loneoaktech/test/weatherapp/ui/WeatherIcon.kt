package com.loneoaktech.test.weatherapp.ui

import com.loneoaktech.test.weatherapp.R

/**
 * Returns the icon for the specified icon code.
 *
 * Created by BillH on 9/19/2017.
 */
fun getWeatherIcon(name: String): Int {
    return ICON_MAP[name] ?: R.drawable.ic_cloudy
}

private var ICON_MAP = hashMapOf(
        "clear-day" to R.drawable.ic_clear_day,
        "clear-night" to R.drawable.ic_clear_night,
        "partly-cloudy-day" to R.drawable.ic_partly_cloudy_day,
        "partly-cloudy-night" to R.drawable.ic_partly_cloudy_night,
        "cloudy" to R.drawable.ic_cloudy,
        "rain" to R.drawable.ic_rain,
        "sleet" to R.drawable.ic_sleet,
        "snow" to R.drawable.ic_snow,
        "wind" to R.drawable.ic_wind,
        "fog" to R.drawable.ic_fog
)
