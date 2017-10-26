package com.loneoaktech.test.weatherapp

/**
 * App configuration constants
 *
 * Created by BillH on 9/23/2017.
 */
const val MINUTE = 60*1000L
const val STALE_FORECAST_UPDATE_THRESHOLD_MS = 5*MINUTE // update forecast if older than this
const val STALE_FORECAST_DISPLAY_THRESHOLD_MS = 60*MINUTE // display forecast if younger than this while reloading
const val ACTIVE_DISPLAY_UPDATE_PERIOD_MS = 1*MINUTE    // Maybe too short, handy for testing
const val UPDATE_JOB_PERIOD = 15*MINUTE     // api 26 clamps to >= 15 minutes despite what documentation says

const val HOME_SCREEN_HOURS_TO_DISPLAY = 4
const val HOME_SCREEN_DAYS_TO_DISPLAY = 3

const val DETAIL_SCREEN_HOURS_TO_DISPLAY = 24
const val DETAIL_SCREEN_DAYS_TO_DISPLAY = 7

const val DARK_SKY_API_KEY = "c87d6c62bfb0c97670c2f79eb05c699f"  // Put your key here