package com.xl4998.covid_19tracker.data

/**
 * Data class for countries at /summary
 */
data class Summary(val Countries: List<CountrySummary>)

data class CountrySummary(
    val Country: String,
    val Slug: String,
    val NewConfirmed: Int,
    val TotalConfirmed: Int,
    val NewDeaths: Int,
    val TotalDeaths: Int,
    val NewRecovered: Int,
    val TotalRecovered: Int
)