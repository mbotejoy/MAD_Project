package com.example.mad_project.ui.theme.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mad_project.data.models.AppRepository
import com.example.mad_project.data.models.Donation
import com.example.mad_project.data.models.LoginRequest
import com.example.mad_project.data.models.RegisterRequest
import com.example.mad_project.data.models.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.mad_project.data.models.User

data class AuthState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val user: UserState? = null
)

data class UserState(
    val id: Int,
    val email: String,
    val name: String,
    val token: String?
)

class AuthViewModel : ViewModel() {
    private val repository = AppRepository()

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        // If a user is already saved in the session, update the state
        val loggedInUser = SessionManager.getUser()
        if (loggedInUser != null) {
            _authState.value = AuthState(isSuccess = true, user = loggedInUser)
        }
    }

    // Registration function
    fun registerUser(registerRequest: RegisterRequest) {
        viewModelScope.launch {
            _authState.value = AuthState(isLoading = true)
            try {
                val response = repository.register(registerRequest)

                if (response.isSuccessful) {
                    response.body()?.let { registerResponse ->
                        if (registerResponse.status == "success") {
                            registerResponse.user?.let { user ->
                                val userState = UserState(
                                    id = user.id,
                                    email = user.email,
                                    name = user.username ?: "",
                                    token = "" // No token in register response
                                )
                                SessionManager.saveUser(userState) // Save user to session
                                _authState.value = AuthState(isSuccess = true, user = userState)
                            } ?: run {
                                _authState.value = AuthState(error = "Registration successful, but user data is missing.")
                            }
                        } else {
                            _authState.value = AuthState(error = registerResponse.message ?: "Registration failed")
                        }
                    }
                } else {
                    val errorMessage = "Registration failed: ${response.message()}"
                    _authState.value = AuthState(error = errorMessage)
                }
            } catch (e: Exception) {
                _authState.value = AuthState(error = "Network error: ${e.message ?: "Check your connection"}")
            } finally {
                _authState.value = _authState.value.copy(isLoading = false)
            }
        }
    }

    // Login function
    fun loginUser(usernameOrEmail: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState(isLoading = true)
            try {
                val response = repository.login(LoginRequest(usernameOrEmail, password))

                if (response.isSuccessful) {
                    response.body()?.let { loginResponse ->
                        if (loginResponse.status == "success") {
                            loginResponse.user?.let { user ->
                                val userState = UserState(
                                    id = user.id,
                                    email = user.email,
                                    name = user.username ?: "",
                                    token = loginResponse.token ?: ""
                                )
                                SessionManager.saveUser(userState) // Save user to session
                                _authState.value = AuthState(isSuccess = true, user = userState)
                            } ?: run {
                                _authState.value = AuthState(error = "Login successful, but user data is missing.")
                            }
                        } else {
                            _authState.value = AuthState(error = loginResponse.message ?: "Login failed")
                        }
                    }
                } else {
                     val errorMessage = "Login failed: ${response.message()}"
                    _authState.value = AuthState(error = errorMessage)
                }
            } catch (e: Exception) {
                _authState.value = AuthState(error = "Network error: ${e.message ?: "Check your connection"}")
            } finally {
                _authState.value = _authState.value.copy(isLoading = false)
            }
        }
    }

    fun logout() {
        SessionManager.clearSession() // Clear user from session
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

    private val _currentUser = MutableLiveData<UserState?>()
    val currentUser: LiveData<UserState?> = _currentUser

    init {
        // Load the current user from the session as soon as the ViewModel is created
        _currentUser.postValue(SessionManager.getUser())
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
                _errorMessage.postValue("Failed to load donations: ${e.message}")
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
                    _errorMessage.postValue("Failed to create donation: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("An error occurred: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}