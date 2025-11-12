package com.example.mad_project.data.models

data class MpesaPaymentRequest(
    val phoneNumber: String,
    val amount: Double,
    val donationId: Int? = null
)
