package com.opsc.timesync.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.opsc.timesync.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var user: FirebaseUser
    private lateinit var recyclerView: RecyclerView
    private lateinit var timesheetAdapter: TimesheetAdapter
    private lateinit var homeViewModel: HomeViewModel

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        user = FirebaseAuth.getInstance().currentUser!!

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        timesheetAdapter = TimesheetAdapter(emptyList(), emptyMap())
        recyclerView.adapter = timesheetAdapter

        homeViewModel.timesheets.observe(viewLifecycleOwner) { timesheetList ->
            timesheetAdapter.updateData(timesheetList, homeViewModel.categoryMap.value.orEmpty())
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
