package com.example.mad_project.ui.theme

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.mad_project.R
import com.example.mad_project.data.models.Donation
import com.example.mad_project.ui.theme.viewmodel.MainViewModel
import java.util.*

class CreateDonationActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var etAmount: EditText
    private lateinit var btnCreate: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var tvError: TextView
    private lateinit var tvDonorName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_donation)

        initializeViews()
        setupViewModel()
        setupClickListeners()
    }

    private fun initializeViews() {
        etAmount = findViewById(R.id.etAmount)
        btnCreate = findViewById(R.id.btnCreate)
        progressBar = findViewById(R.id.progressBar)
        tvError = findViewById(R.id.tvError)
        tvDonorName = findViewById(R.id.tvDonorName)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) android.view.View.VISIBLE else android.view.View.GONE
            btnCreate.isEnabled = !isLoading
        }

        viewModel.successMessage.observe(this) { message ->
            if (!message.isNullOrEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                finish() // Close activity after successful creation
            }
        }

        viewModel.errorMessage.observe(this) { message ->
            if (!message.isNullOrEmpty()) {
                showError(message)
            } else {
                hideError()
            }
        }

        viewModel.currentUser.observe(this) { user ->
            user?.let {
                tvDonorName.text = "Donating as: ${it.name}"
            }
        }
    }

    private fun setupClickListeners() {
        btnCreate.setOnClickListener {
            val amountText = etAmount.text.toString().trim()

            if (validateInput(amountText)) {
                // Safely get the user ID. The validateInput function now checks for a logged-in user.
                val donorId = viewModel.currentUser.value!!.id

                val donation = Donation(
                    id = 0, // Will be set by backend
                    donor = donorId,
                    amount = amountText.toDouble(),
                    description = "Monetary Donation", // Default value
                    foodType = "Cash", // Default value
                    quantity = 1, // Default value for a single transaction
                    location = "N/A", // Not applicable for cash donations
                    createdAt = Date().toString(),
                    status = "available"
                )

                viewModel.createDonation(donation)
            }
        }
    }

    private fun validateInput(amountText: String): Boolean {
        // Reset errors
        etAmount.error = null
        hideError()

        if (viewModel.currentUser.value == null) {
            showError("You must be logged in to make a donation.")
            return false
        }

        if (amountText.isEmpty()) {
            etAmount.error = "Amount is required"
            return false
        }

        try {
            val amount = amountText.toDouble()
            if (amount <= 0) {
                etAmount.error = "Amount must be greater than 0"
                return false
            }
        } catch (e: NumberFormatException) {
            etAmount.error = "Please enter a valid amount"
            return false
        }

        return true
    }

    private fun showError(message: String) {
        tvError.text = message
        tvError.visibility = android.view.View.VISIBLE
    }

    private fun hideError() {
        tvError.visibility = android.view.View.GONE
    }
}
