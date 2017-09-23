package com.loneoaktech.test.weatherapp.db

import android.arch.lifecycle.LiveData
import com.loneoaktech.test.weatherapp.model.ForecastLocation

/**
 * Interface for store that persists the selected lcoation
 *
 * Created by BillH on 9/23/2017.
 */
interface SelectedLocationDao {
    fun loadSelectedLocation() : LiveData<ForecastLocation>

    fun selectocation(location: ForecastLocation)
}
