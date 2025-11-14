package com.example.mad_project.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.mad_project.R
import com.example.mad_project.adapters.DonationAdapter
import com.example.mad_project.data.models.Donation
import com.example.mad_project.ui.theme.DonationDetailsDialog
import com.example.mad_project.ui.theme.viewmodel.MainViewModel

class AvailableDonationsFragment : Fragment() {

    private val viewModel: MainViewModel by viewModels(ownerProducer = { requireActivity() })
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DonationAdapter
    private lateinit var tvEmpty: View
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_donations, container, false)
        initializeViews(view)
        setupRecyclerView()
        setupSwipeToRefresh()
        observeDonations()
        return view
    }

    private fun initializeViews(view: View) {
        recyclerView = view.findViewById(R.id.recyclerView)
        tvEmpty = view.findViewById(R.id.tvEmpty)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = DonationAdapter(emptyList()) { donation ->
            showDonationDetails(donation)
        }
        recyclerView.adapter = adapter
    }

    private fun setupSwipeToRefresh() {
        swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadDonations()
        }
    }

    private fun observeDonations() {
        viewModel.availableDonations.observe(viewLifecycleOwner, Observer { donations ->
            swipeRefreshLayout.isRefreshing = false
            adapter.updateDonations(donations)
            if (donations.isEmpty()) {
                recyclerView.visibility = View.GONE
                tvEmpty.visibility = View.VISIBLE
            } else {
                recyclerView.visibility = View.VISIBLE
                tvEmpty.visibility = View.GONE
            }
        })
    }

    private fun showDonationDetails(donation: Donation) {
        // Show donation details dialog or navigate to details activity
        DonationDetailsDialog.newInstance(donation)
            .show(parentFragmentManager, "donation_details")
    }
}
