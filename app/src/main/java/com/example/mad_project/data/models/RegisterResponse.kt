package com.example.mad_project.data.models

data class RegisterResponse(
    val status: String,
    val message: String,
    val user: User
)

data class user(
    val id: Int,
    val email: String
)
