package com.example.mad_project

data class Donation(
    val id: Int,
    val donor: Int, // User ID
    val amount: Double,
    val description: String,
    val foodType: String,
    val quantity: Int,
    val location: String,
    val createdAt: String,
    val status: String, // "available", "claimed", "delivered"
    val beneficiary: Int? = null // User ID of beneficiary
)
