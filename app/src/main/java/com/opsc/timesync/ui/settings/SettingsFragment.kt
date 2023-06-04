package com.opsc.timesync.ui.settings

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import com.opsc.timesync.R
import com.opsc.timesync.databinding.FragmentNotificationsBinding
import com.opsc.timesync.databinding.FragmentSettingsBinding
import com.opsc.timesync.ui.notifications.NotificationsViewModel

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val settingsViewModel =
            ViewModelProvider(this).get(SettingsViewModel::class.java)

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.progressBar.visibility = View.VISIBLE
        binding.contentLayout.visibility = View.GONE

        settingsViewModel.getSettings()

        // Observe changes in settings
        settingsViewModel.minGoal.observe(viewLifecycleOwner, Observer { minGoal ->
            // Update UI with minGoal value only if it is not null
            if(minGoal != null) {
                binding.minGoalEditText.setText(minGoal.toString())
            }

            // Check if both fields are ready
            if(settingsViewModel.maxGoal.value != null && settingsViewModel.minGoal.value != null){
                binding.progressBar.visibility = View.GONE
                binding.contentLayout.visibility = View.VISIBLE
            }
        })

        settingsViewModel.maxGoal.observe(viewLifecycleOwner, Observer { maxGoal ->
            // Update UI with maxGoal value only if it is not null
            if(maxGoal != null) {
                binding.maxGoalEditText.setText(maxGoal.toString())
            }

            // Check if both fields are ready
            if(settingsViewModel.maxGoal.value != null && settingsViewModel.minGoal.value != null){
                binding.progressBar.visibility = View.GONE
                binding.contentLayout.visibility = View.VISIBLE
            }
        })

        // Setup save button click listener
        binding.saveButton.setOnClickListener {
            // Get values from EditTexts
            val minGoal = binding.minGoalEditText.text.toString().toIntOrNull()
            val maxGoal = binding.maxGoalEditText.text.toString().toIntOrNull()

            // Validate inputs and Save settings
            if (minGoal != null && maxGoal != null) {
                settingsViewModel.saveSettings(minGoal, maxGoal)
                settingsViewModel.getSettings()
                Toast.makeText(activity, "Goals have been saved!", Toast.LENGTH_SHORT).show()
            } else {
                // Handle invalid input case
            }
        }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}