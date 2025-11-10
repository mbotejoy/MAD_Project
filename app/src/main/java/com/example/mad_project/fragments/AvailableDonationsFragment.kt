package com.example.mad_project.fragments

// ui/fragments/AvailableDonationsFragment.kt
import MainViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mad_project.R
import com.example.mad_project.adapters.DonationAdapter

class AvailableDonationsFragment : Fragment() {

    private lateinit var viewModel: MainViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DonationAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_donations, container, false)
        initializeViews(view)
        setupViewModel()
        return view
    }

    private fun initializeViews(view: View) {
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = DonationAdapter(emptyList()) { donation ->
            // Handle donation click - maybe show details or claim donation
        }
        recyclerView.adapter = adapter
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        viewModel.donations.observe(viewLifecycleOwner) { donations ->
            // Filter to show only available donations
            val availableDonations = donations.filter { it.status == "available" }
            adapter.updateDonations(availableDonations)
        }

        viewModel.loadDonations()
    }
}