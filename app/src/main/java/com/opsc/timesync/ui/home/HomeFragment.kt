package com.opsc.timesync.ui.home

import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.opsc.timesync.R
import com.opsc.timesync.databinding.FragmentHomeBinding
import java.lang.Math.abs
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private lateinit var user: FirebaseUser
    private lateinit var lineChart: LineChart
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var timesheetAdapter: TimesheetAdapter
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        user = FirebaseAuth.getInstance().currentUser!!

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        lineChart = root.findViewById(R.id.lineChart)
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        timesheetAdapter = TimesheetAdapter(emptyList(), emptyMap())
        recyclerView.adapter = timesheetAdapter

        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        homeViewModel.userSettings.observe(viewLifecycleOwner) { userSettings ->
            homeViewModel.timesheets.observe(viewLifecycleOwner) { timesheetList ->
                timesheetAdapter.updateData(timesheetList, homeViewModel.categoryMap.value.orEmpty())
                if (timesheetList.isNotEmpty()) {
                    val totalHoursByDay = calculateTotalHoursByDay(timesheetList)
                    setupLineChart(totalHoursByDay, userSettings)
                }
            }
        }
        return root
    }

    private fun calculateTotalHoursByDay(timesheets: List<Timesheet>): Map<Date, Float> {
        val totalHoursByDay = HashMap<Date, Float>()

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()

        for (timesheet in timesheets) {
            val date = timesheet.date?.toDate()
            val startTime = timesheet.startTime?.toDate()
            val endTime = timesheet.endTime?.toDate()

            if (date != null && startTime != null && endTime != null) {
                calendar.time = startTime
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val day = calendar.time

                val totalHours = (endTime.time - startTime.time) / (1000 * 60 * 60).toFloat()

                if (totalHoursByDay.containsKey(day)) {
                    val currentTotalHours = totalHoursByDay[day] ?: 0f
                    totalHoursByDay[day] = currentTotalHours + totalHours
                } else {
                    totalHoursByDay[day] = totalHours
                }
            }
        }

        // Make all total hours positive
        for (key in totalHoursByDay.keys) {
            val totalHours = totalHoursByDay[key] ?: 0f
            totalHoursByDay[key] = abs(totalHours)
        }

        return totalHoursByDay
    }


    private fun setupLineChart(
        totalHoursByDay: Map<Date, Float>,
        userSettings: HomeViewModel.UserSettings
    ) {
        val entries = totalHoursByDay.entries.sortedBy { it.key }
        val xAxisLabels = entries.map { entry ->
            SimpleDateFormat("dd MMM", Locale.getDefault()).format(entry.key)
        }
        val minGoal = userSettings.minGoal
        val maxGoal = userSettings.maxGoal

        val xAxis = lineChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(xAxisLabels)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textColor = Color.WHITE
        xAxis.textSize = 16f
        val yAxis = lineChart.axisLeft
        yAxis.textColor = Color.WHITE
        yAxis.textSize = 16f
        val dataSet = LineDataSet(entries.mapIndexed { index, entry ->
            Entry(index.toFloat(), entry.value)
        }, "Total Hours")
        dataSet.color = Color.WHITE
        dataSet.valueTextColor = Color.WHITE
        dataSet.valueTextSize = 14F
        val lineData = LineData(dataSet)

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
        yAxis.removeAllLimitLines()
        for (goalLine in goalLines) {
            yAxis.addLimitLine(goalLine)
        }

        lineChart.data = lineData
        lineChart.invalidate()
    }



    private inner class DayAxisValueFormatter(private val sdf: SimpleDateFormat) :
        ValueFormatter() {
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            val date = Date(value.toLong())
            return sdf.format(date)
        }
    }
}