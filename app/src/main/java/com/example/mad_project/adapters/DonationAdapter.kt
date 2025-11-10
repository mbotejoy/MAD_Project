package com.example.mad_project.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mad_project.data.models.Donation
import com.example.mad_project.R

class DonationAdapter(
    private var donations: List<Donation>,
    private val onItemClick: (Donation) -> Unit
) : RecyclerView.Adapter<DonationAdapter.DonationViewHolder>() {

    class DonationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
        val tvLocation: TextView = itemView.findViewById(R.id.tvLocation)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val btnClaim: Button = itemView.findViewById(R.id.btnClaim)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_donation, parent, false)
        return DonationViewHolder(view)
    }

    override fun onBindViewHolder(holder: DonationViewHolder, position: Int) {
        val donation = donations[position]

        holder.tvTitle.text = donation.foodType
        holder.tvDescription.text = donation.description
        holder.tvAmount.text = "Amount: $${donation.amount}"
        holder.tvLocation.text = "Location: ${donation.location}"
        holder.tvStatus.text = "Status: ${donation.status}"

        // Show/hide claim button based on status
        holder.btnClaim.visibility = if (donation.status == "available") View.VISIBLE else View.GONE

        holder.btnClaim.setOnClickListener {
            onItemClick(donation)
        }

        holder.itemView.setOnClickListener {
            onItemClick(donation)
        }
    }

    override fun getItemCount() = donations.size

    fun updateDonations(newDonations: List<Donation>) {
        donations = newDonations
        notifyDataSetChanged()
    }
}