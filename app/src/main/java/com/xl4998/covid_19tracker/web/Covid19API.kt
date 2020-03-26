package com.xl4998.covid_19tracker.web

import com.xl4998.covid_19tracker.data.CountryConfirmed
import com.xl4998.covid_19tracker.data.CountryProvinces
import com.xl4998.covid_19tracker.data.Summary
import retrofit2.Call
import retrofit2.http.GET

/**
 * Interface to be used by Retrofit
 */
interface Covid19API {
    @GET("summary")
    fun getSummary(): Call<Summary>

    @GET("dayone/country/US/status/confirmed")
    fun getConfirmedUSCases(): Call<List<CountryConfirmed>>

    @GET("countries")
    fun getCountryAndProvinces(): Call<List<CountryProvinces>>
}