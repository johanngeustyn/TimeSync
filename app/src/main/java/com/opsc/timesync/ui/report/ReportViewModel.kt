package com.opsc.timesync.ui.report

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class TimeEntry(
    var documentId: String? = null,
    var date: Timestamp? = null,
    var startTime: Timestamp? = null,
    var endTime: Timestamp? = null,
    var categoryName: String? = null,
    var entryDescription: String? = null,
    //var photoUrl: String? = null
)

data class CategoryTotal(
    val category: String,
    var totalHours: Double = 0.0
) {
    val formattedTotalTime: String
        get() {
            val hours = totalHours.toInt()
            val minutes = ((totalHours - hours) * 60).toInt()
            return String.format("%d:%02d", hours, minutes)
        }
}

class ReportViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    val timeEntries = MutableLiveData<List<TimeEntry>>()
    val categoryTotals = MutableLiveData<List<CategoryTotal>>()

    fun getReport(startDate: Timestamp, endDate: Timestamp) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("timesheetEntries")
                .whereEqualTo("user", userId)
                .whereGreaterThanOrEqualTo("date", startDate)
                .whereLessThanOrEqualTo("date", endDate)
                .orderBy("date", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener { documents ->
                    val entries = mutableListOf<TimeEntry>()
                    val deferredTasks = mutableListOf<Task<DocumentSnapshot>>()

                    documents.forEach { document ->
                        Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                        val documentId = document.id
                        val date = document.getTimestamp("date")
                        val startTime = document.getTimestamp("startTime")
                        val endTime = document.getTimestamp("endTime")
                        val categoryRef = document.getDocumentReference("category")
                        val entryDescription = document.getString("entryDescription") ?: ""
                        //val photoUrl = document.getString("photoUrl") ?: ""

                        // Fetch the referenced category document
                        val task = categoryRef?.get()?.addOnSuccessListener { categoryDocument ->
                            val category = categoryDocument.getString("name") ?: ""
                            Log.d(TAG, "Success getting category: $category")

                            if (date != null && startTime != null && endTime != null) {
                                val timeEntry = TimeEntry(
                                    documentId,
                                    date,
                                    startTime,
                                    endTime,
                                    category,
                                    entryDescription
                                )
                                entries.add(timeEntry)
                            }
                        }
                        task?.let { deferredTasks.add(it) }
                    }

                    // Use Tasks.whenAll to wait for all category fetch tasks to complete
                    Tasks.whenAll(deferredTasks).addOnCompleteListener {
                        // Now we can safely post entries to LiveData
                        timeEntries.postValue(entries)
                        calculateCategoryTotals(entries)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting time entries.", exception)
                }
        }
    }


    private fun calculateCategoryTotals(entries: List<TimeEntry>) {
        val categoryMap = mutableMapOf<String, CategoryTotal>()

        entries.forEach { entry ->
            val startTimeHour = entry.startTime?.toDate()?.hours ?: 0
            val startTimeMinute = entry.startTime?.toDate()?.minutes ?: 0
            val endTimeHour = entry.endTime?.toDate()?.hours ?: 0
            val endTimeMinute = entry.endTime?.toDate()?.minutes ?: 0

            val start = startTimeHour + startTimeMinute / 60.0
            val end = endTimeHour + endTimeMinute / 60.0

            val durationHours = end - start  // replaced previous calculation
            val categoryTotal =
                categoryMap.getOrPut(entry.categoryName!!) { CategoryTotal(entry.categoryName!!) }
            categoryTotal.totalHours += durationHours
        }

        categoryTotals.postValue(categoryMap.values.toList())
    }

    companion object {
        const val TAG = "Report"
    }
}

