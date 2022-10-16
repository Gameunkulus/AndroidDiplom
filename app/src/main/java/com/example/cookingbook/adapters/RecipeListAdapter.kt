package com.example.cookingbook.adapters


import android.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cookbook.pojo.Recipe
import com.example.cookingbook.pojo.Recipe
import java.lang.String


class RecipeListAdapter(
    private val context: Context,
    recipes: List<Recipe>,
    recipeClickListener: Recipe.RecipeClickListener
) :
    RecyclerView.Adapter<RecipeListAdapter.ViewHolder>() {
    private val recipes: List<Recipe>
    private val recipeClickListener: Recipe.RecipeClickListener
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.recipe_list_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val r: Recipe = recipes[position]
        if (r.icon != null) holder.ivIcon.setImageBitmap(r.icon)
        holder.tvCaption.setText(r.name)
        holder.tvCookingTime.text = String.format("%d мин", r.cookingTime)
    }

    override fun getItemCount(): Int {
        return recipes.size
    }

    internal inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        private val ivIcon: ImageView
        private val tvCaption: TextView
        private val tvCookingTime: TextView
        override fun onClick(v: View) {
            recipeClickListener.onClick(recipes[adapterPosition])
        }

        init {
            ivIcon = itemView.findViewById<View>(R.id.ivIcon) as ImageView
            tvCaption = itemView.findViewById<View>(R.id.tvCaption) as TextView
            tvCookingTime = itemView.findViewById<View>(R.id.tvCookingTime) as TextView
            itemView.setOnClickListener(this)
        }
    }

    init {
        this.recipes = recipes
        this.recipeClickListener = recipeClickListener
    }
}