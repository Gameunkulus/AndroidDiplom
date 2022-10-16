package com.example.cookingbook.adapters


import android.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cookbook.ButtonRemoveClickListener
import com.example.cookingbook.ButtonRemoveClickListener


class ShopListAdapter(private val lines: MutableList<String>, listener: ButtonRemoveClickListener) :
    RecyclerView.Adapter<ShopListAdapter.ViewHolder>() {
    private val listener: ButtonRemoveClickListener
    fun add(line: String) {
        lines.add(line)
        notifyDataSetChanged()
    }

    fun remove(position: Int) {
        lines.removeAt(position)
        notifyDataSetChanged()
    }

    operator fun contains(line: String): Boolean {
        return lines.contains(line)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.ingredient_list_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvCaption.text = lines[position]
    }

    override fun getItemCount(): Int {
        return lines.size
    }

    operator fun get(position: Int): String {
        return lines[position]
    }

    internal inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvCaption: TextView

        init {
            tvCaption = itemView.findViewById<View>(R.id.tvCaption) as TextView
            itemView.findViewById<View>(R.id.btnRemove).setOnClickListener {
                listener.onClick(
                    adapterPosition
                )
            }
        }
    }

    init {
        this.listener = listener
    }
}