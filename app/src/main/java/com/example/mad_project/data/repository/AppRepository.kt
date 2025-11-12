package com.example.mad_project.repository

import com.example.mad_project.data.models.*
import com.example.mad_project.data.network.RetrofitInstance
import com.example.mad_project.network.ApiService

class AppRepository {
    private val apiService: ApiService = RetrofitInstance.apiService

    suspend fun login(username: String, password: String): LoginResponse? {
        return try {
            val response = apiService.login(LoginRequest(username, password))
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        } as LoginResponse?
    }

    suspend fun register(registerRequest: RegisterRequest): RegisterResponse? {
        return try {
            val response = apiService.register(registerRequest)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        } as RegisterResponse?
    }

    suspend fun getDonations(): List<Donation> {
        return try {
            val response = apiService.getDonations()
            if (response.isSuccessful) response.body() ?: emptyList() else emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun createDonation(donation: Donation): Donation? {
        return try {
            val response = apiService.createDonation(donation)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }
}