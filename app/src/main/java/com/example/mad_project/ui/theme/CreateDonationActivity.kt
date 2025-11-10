package com.example.mad_project.ui.theme

// ui/CreateDonationActivity.kt
import MainViewModel
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.mad_project.Donation
import com.example.mad_project.R
import com.google.android.filament.View
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
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
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
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupClickListeners() {
        btnCreate.setOnClickListener {
            val foodType = etFoodType.text.toString().trim()
            val description = etDescription.text.toString().trim()
            val amount = etAmount.text.toString().trim()
            val quantity = etQuantity.text.toString().trim()
            val location = etLocation.text.toString().trim()

            if (validateInput(foodType, description, amount, quantity, location)) {
                val donation = Donation(
                    id = 0, // Will be set by backend
                    donor = viewModel.currentUser.value?.id ?: 0,
                    amount = amount.toDouble(),
                    description = description,
                    foodType = foodType,
                    quantity = quantity.toInt(),
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
        amount: String,
        quantity: String,
        location: String
    ): Boolean {
        if (foodType.isEmpty()) {
            etFoodType.error = "Food type is required"
            return false
        }
        if (description.isEmpty()) {
            etDescription.error = "Description is required"
            return false
        }
        if (amount.isEmpty()) {
            etAmount.error = "Amount is required"
            return false
        }
        if (quantity.isEmpty()) {
            etQuantity.error = "Quantity is required"
            return false
        }
        if (location.isEmpty()) {
            etLocation.error = "Location is required"
            return false
        }
        return true
    }
}