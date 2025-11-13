package com.example.mad_project.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Donation(
    val id: Int = 0,
    val donor: Int = 0,
    val amount: Double = 0.0,
    val description: String = "",
    val foodType: String = "",
    val quantity: Int = 0,
    val location: String = "",
    val createdAt: String = "",
    val status: String = "available"
) : Parcelable
