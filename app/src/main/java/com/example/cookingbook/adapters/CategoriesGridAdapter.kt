package com.example.cookingbook.adapters


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cookingbook.R
import com.example.cookingbook.pojo.Category


class CategoriesGridAdapter(
    private val context: Context,
    categories: List<Category>,
    categoryClickListener: Category.CategoryClickListener
) :
    RecyclerView.Adapter<CategoriesGridAdapter.ViewHolder>() {
    private val categories: List<Category>
    private val categoryClickListener: Category.CategoryClickListener
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.category_grid_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val c: Category = categories[position]
        if (c.icon != null) holder.ivIcon.setImageBitmap(c.icon)
        holder.tvCaption.setText(c.name)
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val ivIcon: ImageView
        val tvCaption: TextView
        override fun onClick(v: View) {
            categoryClickListener.onClick(categories[adapterPosition])
        }

        init {
            ivIcon = itemView.findViewById<View>(R.id.ivCategoryIcon) as ImageView
            tvCaption = itemView.findViewById<View>(R.id.tvCategoryCaption) as TextView
            itemView.setOnClickListener(this)
        }
    }

    init {
        this.categories = categories
        this.categoryClickListener = categoryClickListener
    }
}