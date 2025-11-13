package com.example.mad_project.data.repository

import com.example.mad_project.data.models.*
import com.example.mad_project.data.models.RetrofitInstance

class AppRepository {

    private val apiService = RetrofitInstance.apiService

    // Define result types
    sealed class RepositoryResult<out T> {
        data class Success<T>(val data: T) : RepositoryResult<T>()
        data class Error(val message: String) : RepositoryResult<Nothing>()
    }

    suspend fun login(username: String, password: String): RepositoryResult<LoginResponse> {
        return try {
            val response = apiService.login(LoginRequest(username, password))
            if (response.isSuccessful) {
                response.body()?.let { RepositoryResult.Success(it) }
                    ?: RepositoryResult.Error("Empty response")
            } else {
                RepositoryResult.Error("Login failed: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            RepositoryResult.Error("Network error: ${e.message}")
        }
    }

    suspend fun register(registerRequest: RegisterRequest): RepositoryResult<RegisterResponse> {
        return try {
            val response = apiService.register(registerRequest)
            if (response.isSuccessful) {
                response.body()?.let { RepositoryResult.Success(it) }
                    ?: RepositoryResult.Error("Empty response")
            } else {
                RepositoryResult.Error("Register failed: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            RepositoryResult.Error("Network error: ${e.message}")
        }
    }

    suspend fun getDonations(): RepositoryResult<List<Donation>> {
        return try {
            val response = apiService.getDonations()
            if (response.isSuccessful) {
                RepositoryResult.Success(response.body() ?: emptyList())
            } else {
                RepositoryResult.Error("Failed: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            RepositoryResult.Error("Network error: ${e.message}")
        }
    }

    suspend fun createDonation(donation: Donation): RepositoryResult<Donation> {
        return try {
            val response = apiService.createDonation(donation)
            if (response.isSuccessful) {
                response.body()?.let { RepositoryResult.Success(it) }
                    ?: RepositoryResult.Error("Empty response")
            } else {
                RepositoryResult.Error("Failed: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            RepositoryResult.Error("Network error: ${e.message}")
        }
    }

    suspend fun updateDonation(donationId: Int, donation: Donation): RepositoryResult<Donation> {
        return try {
            val response = apiService.updateDonation(donationId, donation)
            if (response.isSuccessful) {
                response.body()?.let { RepositoryResult.Success(it) }
                    ?: RepositoryResult.Error("Empty response")
            } else {
                RepositoryResult.Error("Failed: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            RepositoryResult.Error("Network error: ${e.message}")
        }
    }

    suspend fun initiateMpesaPayment(request: MpesaPaymentRequest): RepositoryResult<String> {
        return try {
            val response = apiService.initiateMpesaPayment(request)
            if (response.isSuccessful) {
                RepositoryResult.Success("Payment initiated successfully")
            } else {
                RepositoryResult.Error("MPESA payment failed: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            RepositoryResult.Error("Network error: ${e.message}")
        }
    }
}
