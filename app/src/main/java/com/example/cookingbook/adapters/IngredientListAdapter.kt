package com.example.cookingbook.adapters


import android.R
import android.R.*
import android.R.id.*
import android.R.layout.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cookingbook.ButtonRemoveClickListener
import com.example.cookingbook.pojo.Ingredient


class IngredientListAdapter(
    ingredients: MutableList<Ingredient>,
    removeButtonClickListener: ButtonRemoveClickListener
) :
    RecyclerView.Adapter<IngredientListAdapter.ViewHolder>() {
    private val ingredients: MutableList<Ingredient>
    private val removeButtonClickListener: ButtonRemoveClickListener
    fun getIngredients(): List<Ingredient> {
        return ingredients
    }

    fun add(ingredient: Ingredient) {
        ingredients.add(ingredient)
        notifyDataSetChanged()
    }

    fun remove(position: Int) {
        ingredients.removeAt(position)
        notifyDataSetChanged()
    }

    operator fun contains(ing: Ingredient?): Boolean {
        for (i in ingredients) {
            if (i.equals(ing)) return true
        }
        return false
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.ingredient_list_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvCaption.setText(ingredients[position].caption)
    }

    override fun getItemCount(): Int {
        return ingredients.size
    }

    operator fun get(position: Int): Ingredient {
        return ingredients[position]
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var tvCaption: TextView
        var btnRemove: ImageButton
        override fun onClick(v: View) {
            removeButtonClickListener.onClick(adapterPosition)
        }

        init {
            tvCaption = itemView.findViewById<View>(R.id.tvCaption) as TextView
            btnRemove = itemView.findViewById<View>(R.id.btnRemove) as ImageButton
            btnRemove.setOnClickListener(this)
        }
    }

    init {
        this.ingredients = ingredients
        this.removeButtonClickListener = removeButtonClickListener
    }
}