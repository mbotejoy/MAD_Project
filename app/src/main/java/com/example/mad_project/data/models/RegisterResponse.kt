package com.example.mad_project.data.models

data class RegisterResponse(
    val success: Boolean,
    val user: User? = null,
    val message: String? = null,
)
