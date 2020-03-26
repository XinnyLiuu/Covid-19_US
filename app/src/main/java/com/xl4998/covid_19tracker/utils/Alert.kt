package com.xl4998.covid_19tracker.utils

import android.app.AlertDialog
import android.content.Context

class Alert(context: Context, title: String, message: String) {
    init {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .show()
    }
}