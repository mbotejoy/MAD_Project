package com.example.mad_project.ui.theme

// ui/RegisterActivity.kt
import com.example.mad_project.data.models.RegisterRequest
import com.example.mad_project.ui.theme.viewmodel.MainViewModel
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.mad_project.R

class RegisterActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var etUsername: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etFirstName: EditText
    private lateinit var etLastName: EditText
    private lateinit var etPhone: EditText
    private lateinit var radioGroup: RadioGroup
    private lateinit var radioDonor: RadioButton
    private lateinit var radioBeneficiary: RadioButton
    private lateinit var btnRegister: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        initializeViews()
        setupViewModel()
        setupClickListeners()
    }

    private fun initializeViews() {
        etUsername = findViewById(R.id.etUsername)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etFirstName = findViewById(R.id.etFirstName)
        etLastName = findViewById(R.id.etLastName)
        etPhone = findViewById(R.id.etPhone)
        radioGroup = findViewById(R.id.radioGroup)
        radioDonor = findViewById(R.id.radioDonor)
        radioBeneficiary = findViewById(R.id.radioBeneficiary)
        btnRegister = findViewById(R.id.btnRegister)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            btnRegister.isEnabled = !isLoading
        }

        viewModel.successMessage.observe(this) { message ->
            if (!message.isNullOrEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                finish() // Go back to login
            }
        }

        viewModel.errorMessage.observe(this) { message ->
            if (!message.isNullOrEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupClickListeners() {
        btnRegister.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val firstName = etFirstName.text.toString().trim()
            val lastName = etLastName.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            val isDonor = radioDonor.isChecked
            val isBeneficiary = radioBeneficiary.isChecked

            if (validateInput(username, email, password, firstName, lastName, phone)) {
                val registerRequest = RegisterRequest(
                    username = username,
                    email = email,
                    password = password,
                    firstName = firstName,
                    lastName = lastName,
                    phoneNumber = phone,
                    isDonor = isDonor,
                    isBeneficiary = isBeneficiary
                )

                viewModel.register(registerRequest)
            }
        }
    }

    private fun validateInput(
        username: String,
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        phone: String
    ): Boolean {
        // Add validation logic here
        return true
    }
}