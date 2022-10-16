package com.example.cookingbook.adapters


import android.R
import android.R.*
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.example.cookingbook.dao.DBIngredientsHelper
import com.example.cookingbook.pojo.Ingredient


class IngAutoCompleteAdapter(private val context: Context) : BaseAdapter(),
    Filterable {
    private var mResults: List<Ingredient>
    private val dbIngredientsHelper: DBIngredientsHelper
    override fun getCount(): Int {
        return mResults.size
    }

    override fun getItem(index: Int): Ingredient {
        return mResults[index]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            val inflater = LayoutInflater.from(context)
            convertView = inflater.inflate(R.layout.ing_autocomplete_layout, parent, false)
        }
        val i: Ingredient = getItem(position)
        (convertView.findViewById<View>(R.id.text1) as TextView).setText(i.caption)
        return convertView
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val filterResults = FilterResults()
                if (constraint != null) {
                    val ings: List<Ingredient>? = findIngredients(constraint.toString())
                    filterResults.values = ings
                    filterResults.count = ings!!.size
                }
                return filterResults
            }

            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                if (results != null && results.count > 0) {
                    mResults = results.values as List<Ingredient>
                    notifyDataSetChanged()
                } else {
                    notifyDataSetInvalidated()
                }
            }
        }
    }

    private fun findIngredients(query: String): List<Ingredient>? {
        var ingredients: List<Ingredient>? = dbIngredientsHelper.getByName(query)
        if (ingredients == null) ingredients = ArrayList<Ingredient>()
        return ingredients
    }

    init {
        mResults = ArrayList<Ingredient>()
        dbIngredientsHelper = DBIngredientsHelper(context)
    }
}