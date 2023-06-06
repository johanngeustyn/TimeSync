package com.opsc.timesync.ui.report

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.opsc.timesync.R

class CategoryTotalAdapter : ListAdapter<CategoryTotal, CategoryTotalViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CategoryTotal>() {
            override fun areItemsTheSame(oldItem: CategoryTotal, newItem: CategoryTotal): Boolean {
                return oldItem.totalHours == newItem.totalHours
            }

            override fun areContentsTheSame(oldItem: CategoryTotal, newItem: CategoryTotal): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryTotalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category_total, parent, false)
        return CategoryTotalViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryTotalViewHolder, position: Int) {
        val categoryTotal = getItem(position)
        holder.bind(categoryTotal)
    }
}