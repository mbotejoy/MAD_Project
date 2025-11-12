package com.example.mad_project.ui.theme


import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.mad_project.R
import com.example.mad_project.data.models.Donation

class DonationDetailsDialog : DialogFragment() {

    companion object {
        private const val ARG_DONATION = "donation"

        fun newInstance(donation: Donation): DonationDetailsDialog {
            val args = Bundle().apply {
                putSerializable(ARG_DONATION, donation)
            }
            return DonationDetailsDialog().apply {
                arguments = args
            }
        }
    }

    private lateinit var donation: Donation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        donation = arguments?.getSerializable(ARG_DONATION) as Donation
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = requireActivity().layoutInflater.inflate(R.layout.dialog_donation_details, null)

        // Initialize views
        val tvFoodType: TextView = view.findViewById(R.id.tvFoodType)
        val tvDescription: TextView = view.findViewById(R.id.tvDescription)
        val tvAmount: TextView = view.findViewById(R.id.tvAmount)
        val tvQuantity: TextView = view.findViewById(R.id.tvQuantity)
        val tvLocation: TextView = view.findViewById(R.id.tvLocation)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val btnClaim: Button = view.findViewById(R.id.btnClaim)
        val btnClose: Button = view.findViewById(R.id.btnClose)

        // Set donation data
        tvFoodType.text = donation.foodType
        tvDescription.text = donation.description
        tvAmount.text = "Amount: $${donation.amount}"
        tvQuantity.text = "Quantity: ${donation.quantity}"
        tvLocation.text = "Location: ${donation.location}"
        tvStatus.text = "Status: ${donation.status}"

        // Show/hide claim button based on status
        btnClaim.visibility = if (donation.status == "available") View.VISIBLE else View.GONE

        btnClaim.setOnClickListener {
            // Handle claim action
            onClaimDonation(donation)
            dismiss()
        }

        btnClose.setOnClickListener {
            dismiss()
        }

        return AlertDialog.Builder(requireContext())
            .setView(view)
            .create()
    }

    private fun onClaimDonation(donation: Donation) {
        // Implement claim donation logic here
        // You can call a ViewModel method to update the donation status
        (requireActivity() as? OnDonationClaimListener)?.onDonationClaimed(donation)
    }

    interface OnDonationClaimListener {
        fun onDonationClaimed(donation: Donation)
    }
}