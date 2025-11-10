package com.example.mad_project

data class User(
    val id: Int,
    val email: String,
    val username: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String? = null,
    val dateJoined: String,
    val isDonor: Boolean = false,
    val isBeneficiary: Boolean = false
)