package com.opsc.timesync.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.opsc.timesync.R

class TimesheetAdapter(private val timesheets: List<Timesheet>) :
    RecyclerView.Adapter<TimesheetAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimesheetAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_timesheet, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: TimesheetAdapter.ViewHolder, position: Int) {
        val timesheet = timesheets[position]
        holder.textViewTitle.text = timesheet.title
        holder.textViewDescription.text = timesheet.description
    }

    override fun getItemCount(): Int = timesheets.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewTitle: TextView = itemView.findViewById(R.id.textViewTitle)
        val textViewDescription: TextView = itemView.findViewById(R.id.textViewDescription)
        val textViewDate: TextView = itemView.findViewById(R.id.textViewDate)
    }
}