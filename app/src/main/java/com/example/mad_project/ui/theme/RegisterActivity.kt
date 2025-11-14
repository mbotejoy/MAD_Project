package com.example.mad_project.ui.theme

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.mad_project.R
import com.example.mad_project.data.models.RegisterRequest
import com.example.mad_project.ui.theme.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var viewModel: AuthViewModel
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
        observeViewModel()
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
        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.authState.collect { authState ->
                progressBar.visibility = if (authState.isLoading) View.VISIBLE else View.GONE
                btnRegister.isEnabled = !authState.isLoading

                if (authState.error != null) {
                    Toast.makeText(this@RegisterActivity, authState.error, Toast.LENGTH_SHORT).show()
                }

                if (authState.isSuccess) {
                    Toast.makeText(this@RegisterActivity, "Registration Successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
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
                viewModel.registerUser(registerRequest)
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
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}