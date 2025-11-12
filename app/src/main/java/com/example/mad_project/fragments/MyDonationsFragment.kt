package com.example.mad_project.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mad_project.ui.theme.viewmodel.MainViewModel
import com.example.mad_project.R
import com.example.mad_project.adapters.DonationAdapter
import com.example.mad_project.data.models.Donation

class MyDonationsFragment : Fragment() {

    private val viewModel: MainViewModel by viewModels(ownerProducer = { requireActivity() })
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DonationAdapter
    private lateinit var tvEmpty: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_donations, container, false)
        initializeViews(view)
        setupRecyclerView()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeMyDonations()
    }

    private fun initializeViews(view: View) {
        recyclerView = view.findViewById(R.id.recyclerView)
        tvEmpty = view.findViewById(R.id.tvEmpty)
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = DonationAdapter(emptyList()) { donation ->
            showDonationDetails(donation)
        }
        recyclerView.adapter = adapter
    }

    private fun observeMyDonations() {
        // Observe donations and filter for current user
        viewModel.donations.observe(viewLifecycleOwner) { allDonations ->
            val myDonations = viewModel.myDonations
            adapter.updateDonations(myDonations)
            tvEmpty.visibility = if (myDonations.isEmpty()) View.VISIBLE else View.GONE
        }

        // Load donations if not already loaded
        if (viewModel.donations.value.isNullOrEmpty()) {
            viewModel.loadDonations()
        }
    }

    private fun showDonationDetails(donation: Donation) {
        Toast.makeText(
            requireContext(),
            "My Donation: ${donation.foodType}\nStatus: ${donation.status}",
            Toast.LENGTH_LONG
        ).show()
    }
}