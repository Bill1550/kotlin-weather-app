package com.loneoaktech.test.weatherapp.misc

/**
 * A handy exnention function
 * Created by BillH on 9/24/2017.
 */
fun<T> T.mustBeOrNull(predicate: T.()->Boolean ) : T? = if (predicate(this)) this else null