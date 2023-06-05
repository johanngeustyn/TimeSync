package com.opsc.timesync.ui.categories

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CategoriesViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    val categories = MutableLiveData<List<String>>()

    fun getCategories() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("categories")
                .whereEqualTo("user_id", userId)
                .get()
                .addOnSuccessListener { document ->
                    val categoryNames = document.documents.map { it.getString("name") ?: "" }
                    categories.postValue(categoryNames)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting categories.", exception)
                }
        }
    }

    fun addCategory(categoryName: String) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val category = hashMapOf("name" to categoryName, "user_id" to userId)

            db.collection("categories")
                .add(category)
                .addOnSuccessListener {
                    getCategories()
                    Log.d(TAG, "DocumentSnapshot successfully written!")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error writing document", e)
                }
        }
    }

    companion object {
        const val TAG = "Categories"
    }
}