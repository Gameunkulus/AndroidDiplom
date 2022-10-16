package com.example.cookingbook.helper

import android.content.Context


class UpdatingHelper(context: Context?) : PreferencesWorker(context!!) {
    var lastUpdatingTime: Long
        get() = prefs!!.getLong(KEY_LAST_UPDATE, 0)
        set(lastUpdate) {
            prefs!!.edit().putLong(KEY_LAST_UPDATE, lastUpdate).apply()
        }

    companion object {
        private const val KEY_LAST_UPDATE = "last_update"
    }
}