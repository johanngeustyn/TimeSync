package com.opsc.timesync.ui.settings

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SettingsViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    val settings: MutableLiveData<HashMap<String, Any>> = MutableLiveData()

    val minGoal = MutableLiveData<Int?>()
    val maxGoal = MutableLiveData<Int?>()

    fun getSettings() {
        val userId = auth.currentUser?.uid

        if (userId != null) {
            db.collection("userSettings")
                .document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                        var minGoalValue = document.getLong("min_goal")?.toInt()
                        var maxGoalValue = document.getLong("max_goal")?.toInt()
                        minGoal.postValue(minGoalValue)
                        maxGoal.postValue(maxGoalValue)
                    } else {
                        Log.d(TAG, "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents.", exception)
                }
        }
    }



    fun saveSettings(minGoal: Int, maxGoal: Int) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val settings = hashMapOf(
                "min_goal" to minGoal,
                "max_goal" to maxGoal
            )

            db.collection("userSettings")
                .document(userId)
                .set(settings)
                .addOnSuccessListener {
                    Log.d(TAG, "DocumentSnapshot successfully written!")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error writing document", e)
                }
        }
    }

    companion object {
        const val TAG = "Goals"
    }
}