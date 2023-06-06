package com.opsc.timesync.ui.report

import android.annotation.SuppressLint
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.opsc.timesync.R

class CategoryTotalViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val categoryNameTextView: TextView = view.findViewById(R.id.categoryName)
    private val categoryTotalTextView: TextView = view.findViewById(R.id.totalHours)

    @SuppressLint("SetTextI18n")
    fun bind(categoryTotal: CategoryTotal) {
        categoryNameTextView.text = categoryTotal.category
        categoryTotalTextView.text = "Total: ${categoryTotal.formattedTotalTime} hours"
    }
}