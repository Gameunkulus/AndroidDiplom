package com.example.cookingbook.adapters

import android.R
import android.content.Context
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cookingbook.dao.DBShopListHelper
import com.example.cookingbook.pojo.Ingredient
import java.util.*


class RecipeIngredientAdapter(
    context: Context?,
    ingredients: List<Pair<Ingredient, String>>,
    listener: Ingredient.IngredientClickListener
) :
    RecyclerView.Adapter<RecipeIngredientAdapter.ViewHolder>() {
    private val ingredients: List<Pair<Ingredient, String>>
    private val listener: Ingredient.IngredientClickListener
    private val inShopList: MutableSet<String>
    fun isInList(ing: String): Boolean {
        return inShopList.contains(ing.lowercase(Locale.getDefault()))
    }

    fun addInList(ing: String) {
        inShopList.add(ing.lowercase(Locale.getDefault()))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.recipe_ingredient_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val caption: String = ingredients[position].first.caption
        val quantity = ingredients[position].second
        holder.tvCaption.text = caption
        holder.tvQuantity.text = quantity
        if (!inShopList.contains(caption.lowercase(Locale.getDefault()))) {
            holder.btnAddToShopList.setImageResource(R.drawable.ic_add_to_shop_list)
        } else {
            holder.btnAddToShopList.setImageResource(R.drawable.ic_in_shop_list)
        }
    }

    override fun getItemCount(): Int {
        return ingredients.size
    }

    fun removeIng(ing: String) {
        inShopList.remove(ing.lowercase(Locale.getDefault()))
        notifyDataSetChanged()
    }

    internal inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        private val tvCaption: TextView
        private val tvQuantity: TextView
        private val btnAddToShopList: ImageButton
        override fun onClick(v: View) {
            listener.onClick(ingredients[adapterPosition].first)
            notifyItemChanged(adapterPosition)
        }

        init {
            tvCaption = itemView.findViewById<View>(R.id.tvCaption) as TextView
            tvQuantity = itemView.findViewById<View>(R.id.tvQuantity) as TextView
            btnAddToShopList = itemView.findViewById<View>(R.id.btnToShopList) as ImageButton
            btnAddToShopList.setOnClickListener(this)
        }
    }

    init {
        this.ingredients = ingredients
        this.listener = listener
        inShopList = HashSet()
        for (s in DBShopListHelper(context).getAll()) {
            inShopList.add(s.lowercase(Locale.getDefault()))
        }
    }
}