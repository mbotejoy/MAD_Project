package com.example.mad_project.data.models

import com.example.mad_project.data.models.User

data class LoginResponse(
    val success: Boolean,
    val user: User? = null,
    val token: String? = null,
    val message: String? = null
)