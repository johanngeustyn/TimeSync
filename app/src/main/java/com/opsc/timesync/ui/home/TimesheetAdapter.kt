package com.opsc.timesync.ui.home

import Category
import android.icu.text.SimpleDateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.toObject
import com.opsc.timesync.R
import com.opsc.timesync.ui.home.Timesheet
import java.util.Locale

class TimesheetAdapter(
    private var timesheets: List<Timesheet>,
    private var categoryMap: Map<String, Category?> = emptyMap()
) : RecyclerView.Adapter<TimesheetAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_timesheet, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val timesheet = timesheets[position]
        holder.bind(timesheet)
    }

    override fun getItemCount(): Int = timesheets.size

    fun updateData(newTimesheets: List<Timesheet>, newCategoryMap: Map<String, Category>) {
        timesheets = newTimesheets
        categoryMap = newCategoryMap
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewDescription: TextView = itemView.findViewById(R.id.textViewDescription)
        private val textViewStartTime: TextView = itemView.findViewById(R.id.textViewStartTime)
        private val textViewEndTime: TextView = itemView.findViewById(R.id.textViewEndTime)
        private val textViewDate: TextView = itemView.findViewById(R.id.textViewDate)
        private val textViewCategory: TextView = itemView.findViewById(R.id.textViewCategory)

        fun bind(timesheet: Timesheet) {
            textViewDescription.text = timesheet.entryDescription

            val startTime = formatTimestamp(timesheet.startTime)
            textViewStartTime.text = "Start Time: $startTime"

            val endTime = formatTimestamp(timesheet.endTime)
            textViewEndTime.text = "End Time: $endTime"

            val date = formatDate(timesheet.date)
            textViewDate.text = "Date: $date"

            val categoryName = timesheet.categoryName
            if (categoryName != null && categoryName.isNotEmpty()) {
                textViewCategory.visibility = View.VISIBLE
                textViewCategory.text = "$categoryName"
            } else {
                textViewCategory.visibility = View.GONE
            }
        }

        private fun formatTimestamp(timestamp: Timestamp?): String {
            if (timestamp == null) {
                return ""
            }

            val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val date = timestamp.toDate()
            return dateFormat.format(date)
        }

        private fun formatDate(timestamp: Timestamp?): String {
            if (timestamp == null) {
                return ""
            }

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = timestamp.toDate()
            return dateFormat.format(date)
        }
    }
}
