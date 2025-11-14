package com.example.mad_project.data.models


data class LoginResponse(
    val status: String,
    val user: User? = null,
    val token: String? = null,
    val message: String? = null,
    val error: String? = null
)