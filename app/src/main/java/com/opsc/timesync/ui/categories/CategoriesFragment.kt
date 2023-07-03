package com.opsc.timesync.ui.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.opsc.timesync.databinding.FragmentCategoriesBinding

class CategoriesFragment : Fragment() {

    private var _binding: FragmentCategoriesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    val categoryAdapter = CategoryAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val categoriesViewModel =
            ViewModelProvider(this).get(CategoriesViewModel::class.java)

        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.progressBar.visibility = View.VISIBLE
        binding.contentLayout.visibility = View.GONE

        // Setup RecyclerView
        binding.categoriesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.categoriesRecyclerView.adapter = categoryAdapter

        // Observe changes in categories
        categoriesViewModel.categories.observe(viewLifecycleOwner, Observer { categories ->
            // Update RecyclerView with new categories data
            categoryAdapter.submitList(categories)
            // Hide progressBar and show content when data is loaded
            binding.progressBar.visibility = View.GONE
            binding.contentLayout.visibility = View.VISIBLE
        })

        // Load categories
        categoriesViewModel.getCategories()

        // Setup save button click listener
        binding.addCategoryButton.setOnClickListener {
            // Get value from EditText
            val categoryName = binding.categoryNameEditText.text.toString()

            // Validate inputs and Save category
            if (categoryName.isNotBlank()) {
                categoriesViewModel.addCategory(categoryName)
                // Clear input field
                binding.categoryNameEditText.text.clear()
                Toast.makeText(activity, "Category has been added!", Toast.LENGTH_SHORT).show()
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