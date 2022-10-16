package com.example.cookingbook.helper

import android.content.Context
import android.content.SharedPreferences

abstract class PreferencesWorker(context: Context) {
    companion object {
        protected const val APP_PREFS = "CookBook_Prefs"
        @JvmStatic
        protected var prefs: SharedPreferences? = null
    }

    init {
        if (prefs == null) prefs = context.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE)
    }
}