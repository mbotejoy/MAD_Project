// ui/fragments/AvailableDonationsFragment.kt
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mad_project.R
import com.example.mad_project.adapters.DonationAdapter
import com.example.mad_project.data.models.Donation
import com.example.mad_project.ui.theme.DonationDetailsDialog
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.example.mad_project.viewmodel.MainViewModel

class AvailableDonationsFragment : Fragment() {

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
        observeDonations()
        return view
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

    private fun observeDonations() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.availableDonations.collectLatest { donations ->
                adapter.updateDonations(donations)
                tvEmpty.visibility = if (donations.isEmpty()) View.VISIBLE else View.GONE
            }
        }
    }

    private fun showDonationDetails(donation: Donation) {
        // Show donation details dialog or navigate to details activity
        DonationDetailsDialog.newInstance(donation)
            .show(parentFragmentManager, "donation_details")
    }
}


