package com.opsc.timesync.ui.addtimesheetentry

import Category
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TimePicker
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.opsc.timesync.R
import com.opsc.timesync.databinding.FragmentAddtimesheetentryBinding
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


class AddTimesheetEntryFragment : Fragment(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {

    private lateinit var user: FirebaseUser
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private var _binding: FragmentAddtimesheetentryBinding? = null
    private val binding get() = _binding!!

    private lateinit var showDatePickerButton: Button
    private lateinit var showStartTimePickerButton: Button
    private lateinit var showEndTimePickerButton: Button

    private lateinit var activeTimePickerButton: Button

    private lateinit var date: Date
    private lateinit var startTime: Timestamp
    private lateinit var endTime: Timestamp

    private lateinit var categorySpinner: Spinner
    private lateinit var selectedCategory: Category

    private val REQUEST_IMAGE_PICK = 1
    private var selectedImageUri: Uri? = null

    private lateinit var imageViewSelectedPhoto: ImageView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val addTimesheetEntryViewModel =
            ViewModelProvider(this)[AddTimesheetEntryViewModel::class.java]
        user = FirebaseAuth.getInstance().currentUser!!

        _binding = FragmentAddtimesheetentryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        showDatePickerButton = binding.buttonShowDatePicker
        showDatePickerButton.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                this,
                Calendar.YEAR,
                Calendar.MONTH,
                Calendar.DAY_OF_MONTH
            )
            datePickerDialog.datePicker.minDate = System.currentTimeMillis()
            datePickerDialog.show()
        }

        showStartTimePickerButton = binding.buttonShowStartTimePicker
        showStartTimePickerButton.setOnClickListener {
            activeTimePickerButton = showStartTimePickerButton
            val startTimePicker = TimePickerDialog(
                requireContext(),
                this,
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                Calendar.getInstance().get(Calendar.MINUTE),
                false
            )
            startTimePicker.show()
        }

        showEndTimePickerButton = binding.buttonShowEndTimePicker
        showEndTimePickerButton.setOnClickListener {
            activeTimePickerButton = showEndTimePickerButton
            val endTimePicker = TimePickerDialog(
                requireContext(),
                this,
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                Calendar.getInstance().get(Calendar.MINUTE),
                false
            )
            endTimePicker.show()
        }

        val addEntryButton = binding.addEntry
        addEntryButton.setOnClickListener {
            // Launch a coroutine to save the entry
            lifecycleScope.launch {
                val photoUri = selectedImageUri
                if (photoUri != null) {
                    val photoUrl = uploadPhotoAndReturnUrl(photoUri)
                    if (photoUrl != null) {
                        saveEntryToFirestore(photoUrl)
                    } else {
                        // Handle the case when the photo upload failed
                    }
                } else {
                    // No photo selected, save the entry without a photo URL
                    saveEntryToFirestore(null)
                }
            }
        }



        categorySpinner = binding.dropdownCategories

        addTimesheetEntryViewModel.fetchCategories()
        addTimesheetEntryViewModel.categories.observe(viewLifecycleOwner) { categories ->
            // Update the UI with the fetched categories
            val allCategories = mutableListOf<Category>(Category("0", "None"))
            allCategories.addAll(categories)

            val adapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, allCategories)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.dropdownCategories.adapter = adapter
        }

        binding.dropdownCategories.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedCategory = parent?.getItemAtPosition(position) as Category
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    selectedCategory = Category("0", "None")
                }
            }


        imageViewSelectedPhoto = binding.addedImageView
        val selectPhotoButton = binding.addImageButton
        selectPhotoButton.setOnClickListener {
            requestExternalStoragePermission()
        }

        return root
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        date = Date(calendar.timeInMillis)

        binding.buttonShowDatePicker.text = String.format(
            "%d-%02d-%02d",
            year,
            month + 1,
            dayOfMonth
        )
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        val selectedButton = activeTimePickerButton
        if (selectedButton == showStartTimePickerButton) {
            selectedButton.text = String.format("%d:%02d", hourOfDay, minute)

            val calendar = Calendar.getInstance()
            val date = binding.buttonShowDatePicker.text.toString()

            if (date != "Select Date") {
                val dateParts = date.split("-")
                calendar.set(Calendar.YEAR, dateParts[0].toInt())
                calendar.set(Calendar.MONTH, dateParts[1].toInt() - 1)
                calendar.set(Calendar.DAY_OF_MONTH, dateParts[2].toInt())
            }
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            startTime = Timestamp(calendar.timeInMillis / 1000, 0)

        } else if (selectedButton == showEndTimePickerButton) {
            selectedButton.text = String.format("%d:%02d", hourOfDay, minute)

            val calendar = Calendar.getInstance()
            val date = binding.buttonShowDatePicker.text.toString()

            if (date != "Select Date") {
                val dateParts = date.split("-")
                calendar.set(Calendar.YEAR, dateParts[0].toInt())
                calendar.set(Calendar.MONTH, dateParts[1].toInt() - 1)
                calendar.set(Calendar.DAY_OF_MONTH, dateParts[2].toInt())
            }
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            endTime = Timestamp(calendar.timeInMillis / 1000, 0)
        }
    }



    private fun saveEntryToFirestore(photoUrl: String?) {
        val date = binding.buttonShowDatePicker.text.toString()
        val startTime = binding.buttonShowStartTimePicker.text.toString()
        val endTime = binding.buttonShowEndTimePicker.text.toString()
        val entryDescription = binding.descriptionEditText.text.toString()

        // Parse date, start time, and end time into the appropriate data types
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val parsedDate = dateFormatter.parse(date)
        val startTimeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
        val parsedStartTime = startTimeFormatter.parse(startTime)
        val endTimeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
        val parsedEndTime = endTimeFormatter.parse(endTime)

        // Create Calendar instances and set them with the parsed date, start time, and end time
        val startDateCalendar = Calendar.getInstance()
        startDateCalendar.time = parsedDate
        startDateCalendar.set(Calendar.HOUR_OF_DAY, parsedStartTime.hours)
        startDateCalendar.set(Calendar.MINUTE, parsedStartTime.minutes)
        val startTimestamp = startDateCalendar.timeInMillis

        val endDateCalendar = Calendar.getInstance()
        endDateCalendar.time = parsedDate
        endDateCalendar.set(Calendar.HOUR_OF_DAY, parsedEndTime.hours)
        endDateCalendar.set(Calendar.MINUTE, parsedEndTime.minutes)
        val endTimestamp = endDateCalendar.timeInMillis

        val db = FirebaseFirestore.getInstance()

        val categoryRef = if (selectedCategory.id != "0") {
            db.collection("categories").document(selectedCategory.id)
        } else {
            null
        }

        val entry = hashMapOf(
            "date" to Timestamp(parsedDate.time / 1000, 0),
            "startTime" to Timestamp(startTimestamp / 1000, 0),
            "endTime" to Timestamp(endTimestamp / 1000, 0),
            "entryDescription" to entryDescription,
            "user" to user.uid,
            "category" to categoryRef,
            "photoUrl" to photoUrl
        )

        db.collection("timesheetEntries")
            .add(entry)
            .addOnSuccessListener {
                findNavController().navigate(R.id.navigation_home)
            }
            .addOnFailureListener {
            }
    }

    private fun openGallery() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                "android.permission.READ_EXTERNAL_STORAGE"
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf("android.permission.READ_EXTERNAL_STORAGE"),
                REQUEST_IMAGE_PICK
            )
        } else {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, REQUEST_IMAGE_PICK)
        }
    }

    private val REQUEST_CODE_PICK_FILE = 1

    private fun requestExternalStoragePermission() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "*/*"
        startActivityForResult(intent, REQUEST_CODE_PICK_FILE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            val uri: Uri? = data?.data
            if (uri != null) {
                // Update the imageViewSelectedPhoto with the selected image
                imageViewSelectedPhoto.setImageURI(uri)
                selectedImageUri = uri
            }
        }
    }



    private suspend fun uploadPhotoAndReturnUrl(imageUri: Uri): String? {
        val storageRef = FirebaseStorage.getInstance().reference
        val photoRef = storageRef.child("photos/${UUID.randomUUID()}")
        val uploadTask = photoRef.putFile(imageUri)
        Log.d("working","bruh")
        return try {
            val taskSnapshot = uploadTask.uploadTaskAwait() // Renamed the function here
            val downloadUrl = photoRef.downloadUrl.await()
            downloadUrl.toString()
        } catch (exception: Exception) {
            null
        }
    }

    // Renamed function
    private suspend fun Task<UploadTask.TaskSnapshot>.uploadTaskAwait(): UploadTask.TaskSnapshot {
        return suspendCancellableCoroutine { continuation ->
            addOnSuccessListener { snapshot ->
                if (continuation.isActive) {
                    continuation.resume(snapshot)
                }
            }.addOnFailureListener { exception ->
                if (continuation.isActive) {
                    continuation.resumeWithException(exception)
                }
            }.addOnCanceledListener {
                continuation.cancel()
            }
        }
    }
}


