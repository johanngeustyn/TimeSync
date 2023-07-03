package com.opsc.timesync.ui.report

import android.app.DatePickerDialog
import android.graphics.Color
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
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.Timestamp
import com.opsc.timesync.databinding.FragmentReportBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.github.mikephil.charting.formatter.ValueFormatter
import java.util.Date

class ReportFragment : Fragment() {

    private var _binding: FragmentReportBinding? = null
    private val binding get() = _binding!!
    private val timeEntryAdapter = TimeEntryAdapter()
    private val categoryTotalAdapter = CategoryTotalAdapter()
    private var selectedStartDate: Timestamp? = null
    private var selectedEndDate: Timestamp? = null
    private val format = SimpleDateFormat("dd MMM", Locale.ENGLISH)

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

        reportViewModel.userSettings.observe(viewLifecycleOwner) { userSettings ->
            // Observe changes in time entries
            reportViewModel.timeEntries.observe(viewLifecycleOwner, Observer { timeEntries ->
                // Update RecyclerView with new time entries data
                timeEntryAdapter.submitList(timeEntries)

                // Calculate total hours by day and update the chart
                val totalHoursByDay = reportViewModel.calculateTotalHoursByDay(timeEntries)
                setupLineChart(
                    totalHoursByDay,
                    userSettings
                )  // Assume you have userSettings correctly set here
            });
        }


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
                    reportViewModel.fetchUserSettings()
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

    class DateValueFormatter : ValueFormatter() {
        private val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())

        override fun getFormattedValue(value: Float): String {
            // Convert timestamp in milliseconds (the value) to a readable date
            val timestamp = value.toLong()
            return dateFormat.format(Date(timestamp))
        }
    }

    private fun setupLineChart(
        totalHoursByDay: Map<Date, Float>,
        userSettings: ReportViewModel.UserSettings
    ) {
        val entries = totalHoursByDay.entries.sortedBy { it.key }
        val xAxisLabels = entries.map { entry ->
            android.icu.text.SimpleDateFormat("dd MMM", Locale.getDefault()).format(entry.key)
        }
        val minGoal = userSettings.minGoal
        val maxGoal = userSettings.maxGoal

        val xAxis = binding.lineChart.xAxis
        xAxis.granularity = 1f  // Set granularity to 1 so it will increment by 1
        xAxis.isGranularityEnabled = true
        xAxis.valueFormatter = IndexAxisValueFormatter(xAxisLabels)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textColor = Color.WHITE
        xAxis.textSize = 16f

        val yAxisLeft = binding.lineChart.axisLeft
        yAxisLeft.granularity = 1f  // Set granularity to 1 so it will increment by 1
        yAxisLeft.isGranularityEnabled = true
        yAxisLeft.textColor = Color.WHITE
        yAxisLeft.textSize = 16f
        val dataSet = LineDataSet(entries.mapIndexed { index, entry ->
            Entry(index.toFloat(), entry.value)
        }, "Total Hours")
        dataSet.color = Color.WHITE
        dataSet.valueTextColor = Color.WHITE
        dataSet.valueTextSize = 14F

        val yAxisRight = binding.lineChart.axisRight
        yAxisRight.granularity = 1f  // Set granularity to 1 so it will increment by 1
        yAxisRight.isGranularityEnabled = true
        yAxisRight.textColor = Color.WHITE
        yAxisRight.textSize = 16f
        dataSet.color = Color.WHITE
        dataSet.valueTextColor = Color.WHITE
        dataSet.valueTextSize = 14F

        val lineData = LineData(dataSet)

        val leftAxis: YAxis = binding.lineChart.axisLeft
        leftAxis.axisMinimum = 0f  // Set y-axis minimum to 0

        val rightAxis: YAxis = binding.lineChart.axisRight
        rightAxis.axisMinimum = 0f  // Set y-axis minimum to 0

        // Add goal lines
        val goalLines = mutableListOf<LimitLine>()
        val minGoalLine = LimitLine(minGoal.toFloat(), "Min Goal")
        minGoalLine.lineWidth = 2f
        minGoalLine.lineColor = Color.RED
        minGoalLine.textColor = Color.WHITE
        minGoalLine.textSize = 16F
        goalLines.add(minGoalLine)

        val maxGoalLine = LimitLine(maxGoal.toFloat(), "Max Goal")
        maxGoalLine.lineWidth = 2f
        maxGoalLine.lineColor = Color.GREEN
        maxGoalLine.textColor = Color.WHITE
        maxGoalLine.textSize = 16F
        goalLines.add(maxGoalLine)

        // Apply goal lines to the chart
        yAxisLeft.removeAllLimitLines()
        for (goalLine in goalLines) {
            yAxisLeft.addLimitLine(goalLine)
        }

        binding.lineChart.data = lineData
        binding.lineChart.invalidate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}