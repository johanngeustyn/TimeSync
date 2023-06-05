package com.opsc.timesync.ui.home

import Timesheet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.opsc.timesync.R
import java.text.SimpleDateFormat
import java.util.*

class TimesheetAdapter(private var timesheets: List<Timesheet>) :
    RecyclerView.Adapter<TimesheetAdapter.ViewHolder>() {

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

    fun updateData(newTimesheets: List<Timesheet>) {
        timesheets = newTimesheets
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewDescription: TextView = itemView.findViewById(R.id.textViewDescription)
        private val textViewStartTime: TextView = itemView.findViewById(R.id.textViewStartTime)
        private val textViewEndTime: TextView = itemView.findViewById(R.id.textViewEndTime)
        private val textViewDate: TextView = itemView.findViewById(R.id.textViewDate)


        fun bind(timesheet: Timesheet) {
            textViewDescription.text = timesheet.entryDescription

            val startTime = formatTimestamp(timesheet.startTime)
            textViewStartTime.text = "Start Time: $startTime"

            val endTime = formatTimestamp(timesheet.endTime)
            textViewEndTime.text = "End Time: $endTime"

            val date = formatDate(timesheet.date)
            textViewDate.text = "Date: $date"
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
