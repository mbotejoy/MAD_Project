package com.example.mad_project.ui.theme.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mad_project.data.models.AppRepository
import com.example.mad_project.data.models.Donation
import com.example.mad_project.data.models.LoginRequest
import com.example.mad_project.data.models.RegisterRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val user: UserState? = null
)

data class UserState(
    val id: Any,
    val email: String,
    val name: String,
    val token: String?
)

class AuthViewModel : ViewModel() {
    private val repository = AppRepository()

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    // Registration function
    fun registerUser(registerRequest: RegisterRequest) {
        viewModelScope.launch {
            _authState.value = AuthState(isLoading = true)
            try {
                val response = repository.register(registerRequest)

                if (response.isSuccessful) {
                    response.body()?.let { registerResponse ->
                        if (registerResponse.status == "success") {
                            // Registration successful
                            _authState.value = AuthState(
                                isSuccess = true,
                                user = UserState(
                                    id = registerResponse.user.id,
                                    email = registerResponse.user.email,
                                    name = registerResponse.user.username ?: "",
                                    token = "" // No token in register response
                                )
                            )
                        } else {
                            _authState.value = AuthState(
                                error = registerResponse.message ?: "Registration failed"
                            )
                        }
                    } ?: run {
                        _authState.value = AuthState(error = "An unexpected error occurred: Empty response body")
                    }
                } else {
                    // Handle HTTP error (400, 500, etc.)
                    val errorMessage = when (response.code()) {
                        400 -> "Bad request - check your input"
                        409 -> "User already exists"
                        500 -> "Server error"
                        else -> "Registration failed: ${response.message()}"
                    }
                    _authState.value = AuthState(error = errorMessage)
                }
            } catch (e: Exception) {
                // Handle network errors
                _authState.value = AuthState(
                    error = "Network error: ${e.message ?: "Check your connection"}"
                )
            } finally {
                _authState.value = _authState.value.copy(isLoading = false)
            }
        }
    }

    // Login function
    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState(isLoading = true)
            try {
                val response = repository.login(LoginRequest(email, password))

                if (response.isSuccessful) {
                    response.body()?.let { loginResponse ->
                        if (loginResponse.token == "success") {
                            // Login successful
                            _authState.value = AuthState(
                                isSuccess = true,
                                user = UserState(
                                    id = loginResponse.user?.id ?: "" ,
                                    email = loginResponse.user?.email ?: "",
                                    name = loginResponse.user?.username ?: "",
                                    token = loginResponse.token ?: ""
                                )
                            )
                        } else {
                            // Business logic error from server
                            _authState.value = AuthState(
                                error = loginResponse.message ?: "Login failed"
                            )
                        }
                    } ?: run {
                        // Handle case where response body is null for a successful call
                        _authState.value = AuthState(error = "An unexpected error occurred: Empty response body")
                    }
                } else {
                    // Handle HTTP error
                    val errorMessage = when (response.code()) {
                        401 -> "Invalid email or password"
                        404 -> "User not found"
                        500 -> "Server error"
                        else -> "Login failed: ${response.message()}"
                    }
                    _authState.value = AuthState(error = errorMessage)
                }
            } catch (e: Exception) {
                // Handle network errors
                _authState.value = AuthState(
                    error = "Network error: ${e.message ?: "Check your connection"}"
                )
            } finally {
                _authState.value = _authState.value.copy(isLoading = false)
            }
        }
    }

    // Clear error state
    fun clearError() {
        _authState.value = AuthState(error = null)
    }

    // Reset success state
    fun resetSuccess() {
        _authState.value = AuthState(isSuccess = false)
    }

    // Logout function
    fun logout() {
        _authState.value = AuthState()
    }
}

class MainViewModel : ViewModel() {

    private val repository = AppRepository()

    private val _availableDonations = MutableLiveData<List<Donation>>()
    val availableDonations: LiveData<List<Donation>> = _availableDonations

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _successMessage = MutableLiveData<String>()
    val successMessage: LiveData<String> = _successMessage

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _currentUser = MutableLiveData<UserState>()
    val currentUser: LiveData<UserState> = _currentUser

    init {
        loadDonations()
    }

    fun loadDonations() {
        viewModelScope.launch {
            try {
                val response = repository.getDonations()
                if (response.isSuccessful) {
                    _availableDonations.postValue(response.body())
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun createDonation(donation: Donation) {
        viewModelScope.launch {
            _isLoading.postValue(true)
            try {
                val response = repository.createDonation(donation)
                if (response.isSuccessful) {
                    _successMessage.postValue("Donation created successfully")
                    loadDonations() // Refresh the list of donations
                } else {
                    _errorMessage.postValue("Failed to create donation")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("An error occurred: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}
