package com.xl4998.covid_19tracker.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.xl4998.covid_19tracker.R

/**
 * A simple [Fragment] subclass.
 */
class ProvinceDetailsFragment : Fragment() {

    companion object {
        const val TYPE: String = "TYPE"
        const val TOTAL_CONFIRMED: String = "TOTAL_CONFIRMED"
        const val NEW_CONFIRMED: String = "NEW_CONFIRMED"
        const val US_SUMMARY: String = "US"
        const val LOCATION_SUMMARY: String = "LOCATION"
        const val REPORTED: String = "REPORTED"
        const val LOCATION_NAME: String = "LOCATION_NAME"
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate and get the view
        val view = inflater.inflate(R.layout.fragment_province_details, container, false)

        // Get the values from bundle
        if (arguments != null) {
            val confirmed = arguments!!.getInt(TOTAL_CONFIRMED).toString()
            val reported = arguments!!.getString(REPORTED)

            // Check the type
            if (arguments!!.getString(TYPE) == US_SUMMARY) {
                view.findViewById<TextView>(R.id.location_name).text = "United States"
            }

            if (arguments!!.getString(TYPE) == LOCATION_SUMMARY) {
                view.findViewById<TextView>(R.id.location_name).text = arguments!!.getString(LOCATION_NAME)
            }

            view.findViewById<TextView>(R.id.cases).text = "Total Confirmed Cases: $confirmed"
            view.findViewById<TextView>(R.id.reported).text = "Last Reported: $reported"
        }

        return view
    }
}
