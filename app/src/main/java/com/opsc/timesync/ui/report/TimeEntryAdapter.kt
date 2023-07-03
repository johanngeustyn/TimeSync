package com.opsc.timesync.ui.report

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.opsc.timesync.R

class TimeEntryAdapter : ListAdapter<TimeEntry, TimeEntryViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TimeEntry>() {
            override fun areItemsTheSame(oldItem: TimeEntry, newItem: TimeEntry): Boolean {
                // Compare the IDs of old and new items
                return oldItem.documentId == newItem.documentId
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: TimeEntry, newItem: TimeEntry): Boolean {
                // If TimeEntry objects are data classes, this will compare all their fields
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeEntryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_timesheet, parent, false)
        return TimeEntryViewHolder(view)
    }

    override fun onBindViewHolder(holder: TimeEntryViewHolder, position: Int) {
        val timeEntry = getItem(position)
        holder.bind(timeEntry)
    }
}

