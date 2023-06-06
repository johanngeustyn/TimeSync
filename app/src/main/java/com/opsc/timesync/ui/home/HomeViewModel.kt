package com.opsc.timesync.ui.home


import Category
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeViewModel : ViewModel() {

    private val firestore: FirebaseFirestore = Firebase.firestore
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val userId: String = auth.currentUser?.uid ?: ""

    private val _timesheets = MutableLiveData<List<Timesheet>>()
    val timesheets: LiveData<List<Timesheet>> get() = _timesheets

    private val _categoryMap = MutableLiveData<Map<String, Category>>()
    val categoryMap: LiveData<Map<String, Category>> get() = _categoryMap

    init {
        fetchTimesheets()
    }

    fun fetchTimesheets() {
        firestore.collection("timesheetEntries")
            .whereEqualTo("user", userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                try {
                    val timesheetList = mutableListOf<Timesheet>()
                    val categoryMap = mutableMapOf<String, Category>()

                    val fetchCategoryTasks = mutableListOf<Task<DocumentSnapshot>>()

                    for (document in querySnapshot) {
                        val entry = document.toObject(Timesheet::class.java)

                        val categoryRef = entry.category
                        if (categoryRef != null) {
                            val fetchTask = fetchCategory(categoryRef)
                            fetchCategoryTasks.add(fetchTask)

                            fetchTask.continueWith { categoryTask ->
                                val categorySnapshot = categoryTask.result

                                // Extract category data manually
                                val categoryId = categorySnapshot?.id ?: ""
                                val categoryName = categorySnapshot?.get("name") as? String ?: ""
                                val category = Category(categoryId, categoryName)

                                categoryMap[categoryId] = category
                                entry.category = categoryRef  // Pass the category reference
                                entry.categoryName = categoryName  // Set the category name directly in Timesheet
                                Log.d("fetchTimesheet:","${categoryName}")
                                timesheetList.add(entry)
                                entry.photoUrl = document.getString("photoUrl")

                                if (timesheetList.size == querySnapshot.size()) {

                                    if (fetchCategoryTasks.all { it.isComplete }) {
                                        _timesheets.value = timesheetList
                                        _categoryMap.value = categoryMap
                                    }
                                }
                            }
                        } else {
                            timesheetList.add(entry)
                            entry.photoUrl = document.getString("photoUrl")
                            if (timesheetList.size == querySnapshot.size()) {
                                _timesheets.value = timesheetList
                                _categoryMap.value = categoryMap
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("fetchTimesheets:", "Exception: ${e.message}", e)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("fetchTimesheets:", "Failure: ${exception.message}", exception)
            }
    }

    private fun fetchCategory(categoryRef: DocumentReference): Task<DocumentSnapshot> {
        val completionSource = TaskCompletionSource<DocumentSnapshot>()

        categoryRef.get()
            .addOnSuccessListener { categorySnapshot ->
                completionSource.setResult(categorySnapshot)
                Log.d("fetchCategory categoryName:","${categorySnapshot.get("name")}")

            }
            .addOnFailureListener { exception ->
                completionSource.setException(exception)
            }

        return completionSource.task
    }
}
