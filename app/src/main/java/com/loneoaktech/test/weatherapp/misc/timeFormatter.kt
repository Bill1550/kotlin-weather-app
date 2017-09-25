package com.loneoaktech.test.weatherapp.misc

import android.content.Context
import android.text.format.DateFormat
import java.util.*

/**
 *
 * Created by BillH on 9/23/2017.
 */
fun Context.formatTimeAsHour(time: Long): String
        = DateFormat.getTimeFormat(this).format(Date(time))