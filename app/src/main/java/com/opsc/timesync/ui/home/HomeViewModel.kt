import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeViewModel : ViewModel() {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val userId: String = auth.currentUser?.uid ?: ""

    private val _timesheets = MutableLiveData<List<Timesheet>>()
    val timesheets: LiveData<List<Timesheet>> get() = _timesheets
    init {
        fetchTimesheets()
    }

    fun fetchTimesheets() {
        // Fetch timesheets for the current user from Firestore
        firestore.collection("timesheetEntries")
            .whereEqualTo("user", userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                try {
                    val timesheetList = mutableListOf<Timesheet>()
                    for (document in querySnapshot) {
                        val entry = document.toObject(Timesheet::class.java)
                        timesheetList.add(entry)
                    }
                    Log.d("fetchTimesheets:", "Successful")
                    _timesheets.value = timesheetList
                } catch (e: Exception) {
                    Log.e("fetchTimesheets:", "Exception: ${e.message}", e)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("fetchTimesheets:", "Failure: ${exception.message}", exception)
            }
    }
}
