package com.example.mad_project.data.network

// network/ApiService.kt
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // Authentication endpoints
    @POST("api/login/")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST("api/register/")
    suspend fun register(@Body registerRequest: RegisterRequest): Response<RegisterResponse>

    // Donation endpoints
    @GET("api/donations/")
    suspend fun getDonations(): Response<List<Donation>>

    @GET("api/donations/{id}/")
    suspend fun getDonation(@Path("id") id: Int): Response<Donation>

    @POST("api/donations/")
    suspend fun createDonation(@Body donation: Donation): Response<Donation>

    @PUT("api/donations/{id}/")
    suspend fun updateDonation(@Path("id") id: Int, @Body donation: Donation): Response<Donation>

    // User endpoints
    @GET("api/users/{id}/")
    suspend fun getUser(@Path("id") id: Int): Response<User>

    @PUT("api/users/{id}/")
    suspend fun updateUser(@Path("id") id: Int, @Body user: User): Response<User>

    // MPESA endpoints
    @POST("api/mpesa/payment/")
    suspend fun initiateMpesaPayment(@Body paymentRequest: MpesaPaymentRequest): Response<MpesaTransaction>
}

// Request/Response models
data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val success: Boolean,
    val user: User? = null,
    val token: String? = null,
    val message: String? = null
)

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val isDonor: Boolean = false,
    val isBeneficiary: Boolean = false
)

data class RegisterResponse(
    val success: Boolean,
    val user: User? = null,
    val message: String? = null
)

data class MpesaPaymentRequest(
    val phoneNumber: String,
    val amount: Double,
    val donationId: Int? = null
)