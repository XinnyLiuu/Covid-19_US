package com.xl4998.covid_19tracker.data

/**
 * Data class for /countries
 */
data class CountryProvinces(
    val Country: String,
    val Slug: String,
    val Provinces: List<String>
)