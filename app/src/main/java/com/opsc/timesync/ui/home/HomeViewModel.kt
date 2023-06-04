package com.opsc.timesync.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text
    fun getTimesheets(): LiveData<List<Timesheet>> {
        // Create a LiveData variable to store the list of timesheets.
        val timesheets = MutableLiveData<List<Timesheet>>()

        FirebaseFirestore.getInstance()
            .collection("timesheets")
            .whereEqualTo("user", FirebaseAuth.getInstance().currentUser!!.uid)
            .get()
            .addOnSuccessListener { documents ->
                // Convert the Firestore documents to a list of Timesheet objects.
                val timesheetList = documents.map { it.toObject(Timesheet::class.java) }

                // Update the LiveData variable with the list of timesheets.
                timesheets.value = timesheetList
            }

        // Return the LiveData variable.
        return timesheets
    }

}