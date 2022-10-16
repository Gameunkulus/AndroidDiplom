package com.example.cookingbook.helper


import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.support.v7.widget.SearchView
import android.view.MenuItem
import android.widget.SearchView
import androidx.annotation.Nullable
import com.cookbook.pojo.Recipe
import java.util.*


object SearchHelper {
    fun filterData(recipes: List<Recipe>, s: String): List<Recipe> {
        val filterData: MutableList<Recipe> = ArrayList<Recipe>()
        for (i in recipes.indices) {
            if (recipes[i].name.toLowerCase()
                    .startsWith(s.lowercase(Locale.getDefault()))
            ) filterData.add(
                recipes[i]
            )
        }
        return filterData
    }

    @Nullable
    fun getSearchView(activity: Activity, searchItem: MenuItem?): SearchView? {
        val searchManager = activity.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        var searchView: SearchView? = null
        if (searchItem != null) {
            searchView = searchItem.actionView as SearchView
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(activity.componentName))
            searchView.setQuery("", false)
            searchView.clearFocus()
            searchView.setIconified(true)
            return searchView
        }
        return null
    }
}