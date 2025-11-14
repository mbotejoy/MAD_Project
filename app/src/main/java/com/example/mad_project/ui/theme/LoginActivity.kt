package com.example.mad_project.ui.theme

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.mad_project.R
import com.example.mad_project.ui.theme.viewmodel.AuthViewModel
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var viewModel: AuthViewModel
    private lateinit var etUsername: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var tvError: TextView
    private lateinit var tvRegister: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initializeViews()
        setupViewModel()
        setupClickListeners()
        observeViewModel()
    }

    private fun initializeViews() {
        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        progressBar = findViewById(R.id.progressBar)
        tvError = findViewById(R.id.tvError)
        tvRegister = findViewById(R.id.tvRegister)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]
    }

    private fun setupClickListeners() {
        btnLogin.setOnClickListener {
            val email = etUsername.text.toString().trim() // Change variable name to email
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                showError("Please fill in all fields")
                return@setOnClickListener
            }

            viewModel.loginUser(email, password) // Now passing email
        }
        tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.authState.collect { authState ->
                progressBar.visibility = if (authState.isLoading) android.view.View.VISIBLE else android.view.View.GONE
                btnLogin.isEnabled = !authState.isLoading

                if (authState.error != null) {
                    showError(authState.error)
                } else {
                    hideError()
                }
                // Add detailed logging
                println("AUTH STATE - isLoading: ${authState.isLoading}, isSuccess: ${authState.isSuccess}, user: ${authState.user}")

                if (authState.isSuccess && authState.user != null) {
                    Toast.makeText(this@LoginActivity, "Login Successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    intent.putExtra("USER_ID", authState.user.id.toString())
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    private fun showError(message: String) {
        tvError.text = message
        tvError.visibility = android.view.View.VISIBLE
    }

    private fun hideError() {
        tvError.visibility = android.view.View.GONE
    }
}