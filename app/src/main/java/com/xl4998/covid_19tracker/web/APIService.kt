package com.xl4998.covid_19tracker.web

import com.xl4998.covid_19tracker.data.CountryConfirmed
import com.xl4998.covid_19tracker.data.CountryProvinces
import com.xl4998.covid_19tracker.data.Summary
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class APIService {
    private var api: Covid19API? = null

    companion object {
        const val COVID19_API_URL = "https://api.covid19api.com/"
    }

    // Setup retrofit
    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(COVID19_API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(Covid19API::class.java)
    }

    /**
     * Gets a summary of the COVID-19 pandemic
     */
    fun getSummary(): Call<Summary> {
        return api!!.getSummary()
    }

    /**
     * Gets a list of all confirmed US cases since day 1
     */
    fun getConfirmedUSCases(): Call<List<CountryConfirmed>> {
        return api!!.getConfirmedUSCases()
    }

    /**
     * Gets a list of countries and their provinces
     */
    fun getCountryAndProvinces(): Call<List<CountryProvinces>> {
        return api!!.getCountryAndProvinces()
    }
}