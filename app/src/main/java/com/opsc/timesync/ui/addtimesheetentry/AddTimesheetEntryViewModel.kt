package com.opsc.timesync.ui.addtimesheetentry

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class AddTimesheetEntryViewModel : ViewModel() {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var user: FirebaseUser = FirebaseAuth.getInstance().currentUser!!

    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> = _categories


    fun fetchCategories() {
        firestore.collection("categories")
            .whereEqualTo("user_id", user.uid)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val categoriesList = mutableListOf<Category>()
                for (document in querySnapshot) {
                    val categoryId = document.id
                    val categoryName = document.getString("name")
                    val category = Category(categoryId, categoryName!!)
                    categoriesList.add(category)
                }
                _categories.value = categoriesList
            }
            .addOnFailureListener {
                // Handle the failure
            }
    }

}