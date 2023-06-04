package com.opsc.timesync.ui.addtimesheetentry

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.opsc.timesync.databinding.FragmentAddtimesheetentryBinding
import java.util.*

class AddTimesheetEntryFragment : Fragment(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {

    private var _binding: FragmentAddtimesheetentryBinding? = null
    private val binding get() = _binding!!

    private lateinit var showDatePickerButton: Button
    private lateinit var showStartTimePickerButton: Button
    private lateinit var showEndTimePickerButton: Button

    private lateinit var activeTimePickerButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val addTimesheetEntryViewModel =
            ViewModelProvider(this).get(AddTimesheetEntryViewModel::class.java)

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

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
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
        } else if (selectedButton == showEndTimePickerButton) {
            selectedButton.text = String.format("%d:%02d", hourOfDay, minute)
        }
    }
}
