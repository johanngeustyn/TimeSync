package com.opsc.timesync.ui.categories

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.opsc.timesync.R

class CategoryViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val nameTextView: TextView = itemView.findViewById(R.id.name)
}