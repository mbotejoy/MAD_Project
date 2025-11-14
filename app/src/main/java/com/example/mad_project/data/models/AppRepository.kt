package com.example.mad_project.data.models

import retrofit2.Response

class AppRepository {
    private val apiService = RetrofitInstance.apiService

    // Authentication methods
    suspend fun login(loginRequest: LoginRequest): Response<LoginResponse> {
        return apiService.login(loginRequest)
    }

    suspend fun register(registerRequest: RegisterRequest): Response<RegisterResponse> {
        return apiService.register(registerRequest)
    }

    // Donation methods
    suspend fun getDonations(): Response<List<Donation>> {
        return apiService.getDonations()
    }

    suspend fun createDonation(donation: Donation): Response<Donation> {
        return apiService.createDonation(donation)
    }

    suspend fun updateDonation(id: Int, donation: Donation): Response<Donation> {
        return apiService.updateDonation(id, donation)
    }

    // User methods
    suspend fun getUser(id: Int): Response<User> {
        return apiService.getUser(id)
    }

    // Payment methods
    suspend fun initiateMpesaPayment(paymentRequest: MpesaPaymentRequest): Response<MpesaTransaction> {
        return apiService.initiateMpesaPayment(paymentRequest)
    }
}
