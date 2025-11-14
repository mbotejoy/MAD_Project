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
    private lateinit var etFoodType: EditText
    private lateinit var etDescription: EditText
    private lateinit var etAmount: EditText
    private lateinit var etQuantity: EditText
    private lateinit var etLocation: EditText
    private lateinit var btnCreate: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var tvError: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_donation)

        initializeViews()
        setupViewModel()
        setupClickListeners()
    }

    private fun initializeViews() {
        etFoodType = findViewById(R.id.etFoodType)
        etDescription = findViewById(R.id.etDescription)
        etAmount = findViewById(R.id.etAmount)
        etQuantity = findViewById(R.id.etQuantity)
        etLocation = findViewById(R.id.etLocation)
        btnCreate = findViewById(R.id.btnCreate)
        progressBar = findViewById(R.id.progressBar)
        tvError = findViewById(R.id.tvError)
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
    }

    private fun setupClickListeners() {
        btnCreate.setOnClickListener {
            val foodType = etFoodType.text.toString().trim()
            val description = etDescription.text.toString().trim()
            val amountText = etAmount.text.toString().trim()
            val quantityText = etQuantity.text.toString().trim()
            val location = etLocation.text.toString().trim()

            if (validateInput(foodType, description, amountText, quantityText, location)) {
                val donation = Donation(
                    id = 0, // Will be set by backend
                    donor = viewModel.currentUser.value?.id ?: 0,
                    amount = amountText.toDouble(),
                    description = description,
                    foodType = foodType,
                    quantity = quantityText.toInt(),
                    location = location,
                    createdAt = Date().toString(),
                    status = "available"
                )

                viewModel.createDonation(donation)
            }
        }
    }

    private fun validateInput(
        foodType: String,
        description: String,
        amountText: String,
        quantityText: String,
        location: String
    ): Boolean {
        // Reset errors
        etFoodType.error = null
        etDescription.error = null
        etAmount.error = null
        etQuantity.error = null
        etLocation.error = null
        hideError()

        if (foodType.isEmpty()) {
            etFoodType.error = "Food type is required"
            return false
        }

        if (description.isEmpty()) {
            etDescription.error = "Description is required"
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

        if (quantityText.isEmpty()) {
            etQuantity.error = "Quantity is required"
            return false
        }

        try {
            val quantity = quantityText.toInt()
            if (quantity <= 0) {
                etQuantity.error = "Quantity must be greater than 0"
                return false
            }
        } catch (e: NumberFormatException) {
            etQuantity.error = "Please enter a valid quantity"
            return false
        }

        if (location.isEmpty()) {
            etLocation.error = "Location is required"
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