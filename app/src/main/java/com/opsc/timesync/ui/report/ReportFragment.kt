package com.opsc.timesync.ui.report

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.opsc.timesync.databinding.FragmentReportBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ReportFragment : Fragment() {

    private var _binding: FragmentReportBinding? = null
    private val binding get() = _binding!!
    private val timeEntryAdapter = TimeEntryAdapter()
    private val categoryTotalAdapter = CategoryTotalAdapter()
    private var selectedStartDate: Timestamp? = null
    private var selectedEndDate: Timestamp? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val reportViewModel = ViewModelProvider(this).get(ReportViewModel::class.java)

        _binding = FragmentReportBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Setup RecyclerView for time entries
        binding.timeEntriesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.timeEntriesRecyclerView.adapter = timeEntryAdapter

        // Setup RecyclerView for category totals
        binding.categoryTotalsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.categoryTotalsRecyclerView.adapter = categoryTotalAdapter

        // Observe changes in time entries
        reportViewModel.timeEntries.observe(viewLifecycleOwner, Observer { timeEntries ->
            // Update RecyclerView with new time entries data
            timeEntryAdapter.submitList(timeEntries)
        })

        // Observe changes in category totals
        reportViewModel.categoryTotals.observe(viewLifecycleOwner, Observer { categoryTotals ->
            // Update RecyclerView with new category totals data
            categoryTotalAdapter.submitList(categoryTotals)
        })

        val startDateButton: Button = binding.startDateButton
        startDateButton.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    val calendar = Calendar.getInstance()
                    calendar.set(year, month, dayOfMonth)
                    selectedStartDate = Timestamp(calendar.time)
                    startDateButton.text =
                        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)
                },
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
            datePickerDialog.show()
        }

        val endDateButton: Button = binding.endDateButton
        endDateButton.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    val calendar = Calendar.getInstance()
                    calendar.set(year, month, dayOfMonth)
                    selectedEndDate = Timestamp(calendar.time)
                    endDateButton.text =
                        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)
                },
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
            datePickerDialog.show()
        }

        val filterButton: Button = binding.filterButton
        filterButton.setOnClickListener {
            if (selectedStartDate != null && selectedEndDate != null) {
                if (!selectedStartDate!!.toDate().after(selectedEndDate!!.toDate())) {
                    reportViewModel.getReport(selectedStartDate!!, selectedEndDate!!)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "End date cannot be before start date.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Please select both start and end dates.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}