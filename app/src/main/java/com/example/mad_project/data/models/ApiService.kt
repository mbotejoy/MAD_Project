package com.example.mad_project.data.models

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    @POST("api/login/")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST("api/register/")
    suspend fun register(@Body registerRequest: RegisterRequest): Response<RegisterResponse>

    @GET("api/donations/")
    suspend fun getDonations(): Response<List<Donation>>

    @POST("api/donations/")
    suspend fun createDonation(@Body donation: Donation): Response<Donation>

    @PUT("api/donations/{id}/")
    suspend fun updateDonation(@Path("id") id: Int, @Body donation: Donation): Response<Donation>

    @GET("api/users/{id}/")
    suspend fun getUser(@Path("id") id: Int): Response<User>

    @POST("api/mpesa/payment/")
    suspend fun initiateMpesaPayment(@Body paymentRequest: MpesaPaymentRequest): Response<MpesaTransaction>
}