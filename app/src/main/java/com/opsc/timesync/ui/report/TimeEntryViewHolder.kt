package com.opsc.timesync.ui.report

import android.icu.text.SimpleDateFormat
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.opsc.timesync.R
import java.util.Locale

class TimeEntryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val textViewDescription: TextView = itemView.findViewById(R.id.textViewDescription)
    private val textViewStartTime: TextView = itemView.findViewById(R.id.textViewStartTime)
    private val textViewEndTime: TextView = itemView.findViewById(R.id.textViewEndTime)
    private val textViewDate: TextView = itemView.findViewById(R.id.textViewDate)
    private val textViewCategory: TextView = itemView.findViewById(R.id.textViewCategory)
    private val imageViewPhoto: ImageView = itemView.findViewById(R.id.imageViewPhoto)

    fun bind(timeEntry: TimeEntry) {
        textViewDescription.text = timeEntry.entryDescription

        val startTime = formatTimestamp(timeEntry.startTime)
        textViewStartTime.text = "Start Time: $startTime"

        val endTime = formatTimestamp(timeEntry.endTime)
        textViewEndTime.text = "End Time: $endTime"

        val date = formatDate(timeEntry.date)
        textViewDate.text = "Date: $date"

        val categoryName = timeEntry.categoryName
        if (categoryName != null && categoryName.isNotEmpty()) {
            textViewCategory.visibility = View.VISIBLE
            textViewCategory.text = "$categoryName"
        } else {
            textViewCategory.visibility = View.GONE
        }

        imageViewPhoto.visibility = View.GONE
        /*val imageUrl = timeEntry.photoUrl
        class LoadImageTask(private val imageView: ImageView) : AsyncTask<String, Void, Bitmap?>() {
            override fun doInBackground(vararg params: String): Bitmap? {
                try {
                    val imageUrl = params[0]
                    val url = URL(imageUrl)
                    val connection = url.openConnection() as HttpURLConnection
                    connection.doInput = true
                    connection.connect()
                    val inputStream = connection.inputStream
                    return BitmapFactory.decodeStream(inputStream)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return null
            }

            override fun onPostExecute(result: Bitmap?) {
                if (result != null) {
                    imageView.setImageBitmap(result)
                }
            }
        }
        val loadImageTask = LoadImageTask(imageViewPhoto)
        if(imageUrl != null){
            loadImageTask.execute(imageUrl)
        } else
        {
            imageViewPhoto.visibility = View.GONE
        }*/
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