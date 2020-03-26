package com.xl4998.covid_19tracker.data

/**
 * Data class for countries at /dayone/country/US/status/confirmed
 */

data class CountryConfirmed(
    val Country: String,
    val Province: String,
    val Lat: Float,
    val Lon: Float,
    val Date: String,
    val Cases: Int,
    val Status: String
)