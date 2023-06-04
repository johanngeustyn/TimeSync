package com.opsc.timesync.ui.addtimesheetentry

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.opsc.timesync.R
import com.opsc.timesync.databinding.FragmentAddtimesheetentryBinding
import java.sql.Date
import java.util.Calendar


class AddTimesheetEntryFragment : Fragment(), DatePickerDialog.OnDateSetListener {

    private var _binding: FragmentAddtimesheetentryBinding? = null
    private val binding get() = _binding!!
    private lateinit var titleEditText: EditText
    private lateinit var startDateTextView: TextView
    private lateinit var endDateTimeEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var showDatePickerButton: Button
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this)[AddTimesheetEntryViewModel::class.java]

        _binding = FragmentAddtimesheetentryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        showDatePickerButton = binding.buttonShowDatePicker

        showDatePickerButton.setOnClickListener {
            // Create a new DatePickerDialog instance
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                this,
                Calendar.YEAR,
                Calendar.MONTH,
                Calendar.DAY_OF_MONTH
            )
            datePickerDialog.datePicker.minDate = System.currentTimeMillis()

            // Show the dialog
            datePickerDialog.show()
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        // Update the startDateTimeEditText with the selected date
        binding.startDateTextView.text = String.format(
            "%d-%02d-%02d",
            year,
            month + 1,
            dayOfMonth
        )
    }
}

