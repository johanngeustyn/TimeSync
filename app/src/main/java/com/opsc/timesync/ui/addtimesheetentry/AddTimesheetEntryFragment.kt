package com.opsc.timesync.ui.addtimesheetentry

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.datepicker.MaterialDatePicker.Builder.datePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.opsc.timesync.databinding.FragmentAddtimesheetentryBinding
import java.sql.Time
import java.sql.Timestamp
import java.util.*


class AddTimesheetEntryFragment : Fragment(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {

    private lateinit var user: FirebaseUser
    private var _binding: FragmentAddtimesheetentryBinding? = null
    private val binding get() = _binding!!

    private lateinit var showDatePickerButton: Button
    private lateinit var showStartTimePickerButton: Button
    private lateinit var showEndTimePickerButton: Button

    private lateinit var activeTimePickerButton: Button


    private lateinit var date: Date
    private lateinit var startTime: Timestamp
    private lateinit var endTime: Timestamp

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
            saveEntryToFirestore()
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
            startTime = Timestamp(calendar.timeInMillis)


        } else if (selectedButton == showEndTimePickerButton) {
            selectedButton.text = String.format("%d:%02d", hourOfDay, minute)

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
            endTime = Timestamp(calendar.timeInMillis)
        }
    }

    private fun saveEntryToFirestore() {
        val date = binding.buttonShowDatePicker.text.toString()
        val startTime = binding.buttonShowStartTimePicker.text.toString()
        val endTime = binding.buttonShowEndTimePicker.text.toString()
        val entryDescription = binding.descriptionEditText.text.toString()

        // Add your Firestore logic here to save the entry to the "timesheetentries" collection
        // For example:
        val db = FirebaseFirestore.getInstance()
        val entry = hashMapOf(
            "date" to date,
            "startTime" to startTime,
            "endTime" to endTime,
            "entryDescription" to entryDescription,
            "user" to user.uid
        )
        db.collection("timesheetEntries")
            .add(entry)
            .addOnSuccessListener {
                // Entry saved successfully
                // Handle success case, if needed
            }
            .addOnFailureListener {
                // Error occurred while saving entry
                // Handle failure case, if needed
            }
    }
}
