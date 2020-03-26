package com.xl4998.covid_19tracker

import android.content.Context
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import com.xl4998.covid_19tracker.data.CountryConfirmed
import com.xl4998.covid_19tracker.data.CountrySummary
import com.xl4998.covid_19tracker.fragments.ProvinceDetailsFragment
import com.xl4998.covid_19tracker.utils.Alert
import com.xl4998.covid_19tracker.web.APIService
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {
    companion object {
        const val US = "US"
    }

    // Using Kotlin Coroutines - https://developer.android.com/kotlin/coroutines
    // https://stackoverflow.com/questions/53324408/why-must-dispatchers-main-be-added-to-the-root-job-of-an-implementation-of-an
    // https://stackoverflow.com/questions/52581809/how-to-use-coroutines-globalscope-on-the-main-thread
    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    private var api: APIService? = null
    private var arrayAdapter: ArrayAdapter<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        job = Job()

        // Create APIService
        api = APIService()

        // Create ArrayAdapter for AutoCompleteTextView
        arrayAdapter = ArrayAdapter(
            this, android.R.layout.select_dialog_item, mutableListOf()
        )

        // Setup bottom bar
        bottom_bar.replaceMenu(R.menu.bottom_bar_menu)
        bottom_bar.setOnMenuItemClickListener { it ->
            when (it.itemId) {
                R.id.menu_info -> Alert(
                    this,
                    "About",
                    "All data on this application is sourced from https://covid19api.com/."
                )
            }

            true
        }

        // Setup AutoCompleteTextView
        val select: AutoCompleteTextView = location_select
        select.threshold = 1
        select.setAdapter(arrayAdapter)
        select.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            // On location selection
            // Hide keyboard
            val inputMethodManager =
                this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(
                this.currentFocus!!.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )

            // Get the user selected location
            val location = select.text.toString()

            // Get data and refresh UI
            fetchDataByLocation(location)
        }

        // Setup fab
        bottom_fab.setOnClickListener {
            fetchInitData()
            select.text = SpannableStringBuilder("")
        }

        // Prepare initial data
        fetchInitData()
    }

    /**
     *
     * Asynchronously get data from api at activity start
     */
    private fun fetchInitData() {
        launch {
            try {
                // In the main Android thread, prepare UI
                progress.visibility = View.VISIBLE
                location_select.visibility = View.GONE
                location_details.visibility = View.GONE

                // In the IO thread, fetch data
                withContext(Dispatchers.IO) {
                    // Get US locations and load them into array adapter
                    val locations = getUSLocations()
                    arrayAdapter!!.clear()
                    arrayAdapter!!.addAll(locations)
                    arrayAdapter!!.notifyDataSetChanged()

                    // Get US summary data
                    val summary = getUSSummary()

                    // Create bundle to pass into ProvinceDetailsFragment
                    val bundle = Bundle()
                    bundle.putString(
                        ProvinceDetailsFragment.TYPE,
                        ProvinceDetailsFragment.US_SUMMARY
                    )
                    bundle.putInt(ProvinceDetailsFragment.TOTAL_CONFIRMED, summary.TotalConfirmed)
                    bundle.putInt(ProvinceDetailsFragment.NEW_CONFIRMED, summary.NewConfirmed)

                    val fragment = ProvinceDetailsFragment()
                    fragment.arguments = bundle

                    // Show fragment view
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.location_details, fragment)
                        .commit()
                }
            } catch (e: Exception) {
                Log.e("Coroutine Error", e.toString())
                Alert(this@MainActivity, "Error", "An error has occurred, please try again!")
            } finally {
                progress.visibility = View.GONE
                location_select.visibility = View.VISIBLE
                location_details.visibility = View.VISIBLE
            }
        }
    }

    /**
     * Asynchronously fetch data based on the location provided
     */
    private fun fetchDataByLocation(location: String) {
        launch {
            try {
                // In the main Android thread, prepare UI
                progress.visibility = View.VISIBLE
                location_select.visibility = View.GONE
                location_details.visibility = View.GONE

                // In the IO thread, fetch data
                withContext(Dispatchers.IO) {
                    // Get the cases for the location
                    val data = getConfirmedCasesByUSLocation(location)

                    // Create bundle to pass into ProvinceDetailsFragment
                    val bundle = Bundle()
                    bundle.putString(
                        ProvinceDetailsFragment.TYPE,
                        ProvinceDetailsFragment.LOCATION_SUMMARY
                    )
                    bundle.putString(ProvinceDetailsFragment.LOCATION_NAME, location)
                    bundle.putInt(ProvinceDetailsFragment.TOTAL_CONFIRMED, data.Cases)
                    bundle.putString(ProvinceDetailsFragment.REPORTED, data.Date)

                    val fragment = ProvinceDetailsFragment()
                    fragment.arguments = bundle

                    // Show fragment view
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.location_details, fragment)
                        .commit()
                }
            } catch (e: Exception) {
                Log.e("Coroutine Error", e.toString())
                Alert(this@MainActivity, "Error", "An error has occurred, please try again!")
            } finally {
                progress.visibility = View.GONE
                location_select.visibility = View.VISIBLE
                location_details.visibility = View.VISIBLE
            }
        }
    }

    /**
     * Calls api for list of countries and provinces and filters for US location
     */
    private fun getUSLocations(): List<String> {
        val call = api!!.getCountryAndProvinces()
        val resp = call.execute()

        if (resp.isSuccessful) {
            // Filter the data for US
            val data = resp.body()

            // Get "Provinces" field
            return data!!.filter {
                it.Country == US
            }[0].Provinces
        } else {
            throw Exception("Request to API Failed!")
        }
    }

    /**
     * Calls api for summary of COVID 19 pandemic and filters for US data
     */
    private fun getUSSummary(): CountrySummary {
        val call = api!!.getSummary()
        val resp = call.execute()

        if (resp.isSuccessful) {
            // Filter the list of countries for US
            val data = resp.body()!!.Countries

            // Return summary
            return data.filter {
                it.Country == US
            }[0]
        } else {
            throw Exception("Request to API Failed!")
        }
    }

    /**
     * Calls api for summary of cases in an US location
     */
    private fun getConfirmedCasesByUSLocation(location: String): CountryConfirmed {
        val call = api!!.getConfirmedUSCases()
        val resp = call.execute()

        if (resp.isSuccessful) {
            // Filter the list of countries for US
            val data = resp.body()

            // Return summary
            return data!!.filter {
                it.Province == location
            }[0]
        } else {
            throw Exception("Request to API Failed!")
        }
    }
}
