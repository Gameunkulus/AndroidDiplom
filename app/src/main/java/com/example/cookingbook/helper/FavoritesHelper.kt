package com.example.cookingbook.helper

import android.content.Context


class FavoritesHelper(context: Context?) : PreferencesWorker(context!!) {
    fun addToFavorite(id: Long) {
        val savedIds = favoriteRecipeIds
        if (savedIds.contains(id)) return
        savedIds.add(id)
        saveLongList(savedIds)
    }

    fun removeFromFavorites(id: Long) {
        val savedIds = favoriteRecipeIds
        if (!savedIds.contains(id)) return
        savedIds.remove(id)
        saveLongList(savedIds)
    }

    fun removeAll() {
        prefs!!.edit().putString(PREF_FAVORITES, "").apply()
    }

    fun isFavorite(id: Long): Boolean {
        return favoriteRecipeIds.contains(id)
    }

    private fun saveLongList(savedIds: List<Long>) {
        val sb = StringBuilder()
        for (sId in savedIds) {
            sb.append(sId).append(" ")
        }
        prefs!!.edit().putString(PREF_FAVORITES, sb.toString()).apply()
    }

    val favoriteRecipeIds: MutableList<Long>
        get() {
            val savedIds = prefs!!.getString(PREF_FAVORITES, "")!!
                .split(" ").toTypedArray()
            val res = ArrayList<Long>()
            for (id in savedIds) {
                if (id != "") {
                    res.add(id.toLong())
                }
            }
            return res
        }

    companion object {
        private const val PREF_FAVORITES = "favorites"
    }
}