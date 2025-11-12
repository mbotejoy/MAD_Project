package com.example.mad_project.data.models

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val isDonor: Boolean = false,
    val isBeneficiary: Boolean = false
)
