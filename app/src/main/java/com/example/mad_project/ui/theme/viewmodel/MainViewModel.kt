package com.example.mad_project.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mad_project.data.models.Donation
import com.example.mad_project.data.models.MpesaPaymentRequest
import com.example.mad_project.data.models.RegisterRequest
import com.example.mad_project.data.models.User
import com.example.mad_project.data.repository.AppRepository
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val repository = AppRepository()

    // LiveData for current user
    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> get() = _currentUser

    // LiveData for all donations
    private val _donations = MutableLiveData<List<Donation>>(emptyList())
    val donations: LiveData<List<Donation>> get() = _donations

    // Loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    // Error messages
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    // Success messages
    private val _successMessage = MutableLiveData<String?>()
    val successMessage: LiveData<String?> get() = _successMessage

    // Computed property for available donations
    val availableDonations: LiveData<List<Donation>> = MediatorLiveData<List<Donation>>().apply {
        addSource(_donations) { donationsList ->
            value = donationsList.filter { it.status == "available" }
        }
    }

    // Computed property for user's donations
    val myDonations: LiveData<List<Donation>> = MediatorLiveData<List<Donation>>().apply {
        val updateMyDonations = {
            val currentDonations = _donations.value ?: emptyList()
            val userId = _currentUser.value?.id
            value = if (userId != null) {
                currentDonations.filter { it.donor == userId }
            } else {
                emptyList()
            }
        }

        addSource(_donations) { updateMyDonations() }
        addSource(_currentUser) { updateMyDonations() }
    }

    // Authentication methods
    fun login(username: String, password: String) {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            try {
                val result = repository.login(username, password)
                if (result is AppRepository.RepositoryResult.Success<*>) {
                    _currentUser.value = result.data as User?
                    _successMessage.value = "Login successful!"
                    loadDonations()
                } else if (result is AppRepository.RepositoryResult.Error) {
                    _errorMessage.value = result.message
                }
            } catch (e: Exception) {
                _errorMessage.value = "Login failed: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun register(registerRequest: RegisterRequest) {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            try {
                val result = repository.register(registerRequest)
                if (result is AppRepository.RepositoryResult.Success<*>) {
                    _currentUser.value = result.data as User?
                    _successMessage.value = "Registration successful!"
                } else if (result is AppRepository.RepositoryResult.Error) {
                    _errorMessage.value = result.message
                }
            } catch (e: Exception) {
                _errorMessage.value = "Registration failed: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Donation methods
    fun loadDonations() {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            try {
                val result = repository.getDonations()
                if (result is AppRepository.RepositoryResult.Success<*>) {
                    _donations.value = result.data as List<Donation>?
                } else if (result is AppRepository.RepositoryResult.Error) {
                    _errorMessage.value = result.message
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load donations: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createDonation(donation: Donation) {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            try {
                val result = repository.createDonation(donation)
                if (result is AppRepository.RepositoryResult.Success<*>) {
                    loadDonations()
                    _successMessage.value = "Donation created successfully!"
                } else if (result is AppRepository.RepositoryResult.Error) {
                    _errorMessage.value = result.message
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to create donation: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateDonation(donationId: Int, donation: Donation) {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            try {
                val result = repository.updateDonation(donationId, donation)
                if (result is AppRepository.RepositoryResult.Success<*>) {
                    loadDonations()
                    _successMessage.value = "Donation updated successfully!"
                } else if (result is AppRepository.RepositoryResult.Error) {
                    _errorMessage.value = result.message
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update donation: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // MPESA Payment
    fun makeMpesaPayment(phoneNumber: String, amount: Double, donationId: Int? = null) {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            try {
                val result = repository.initiateMpesaPayment(
                    MpesaPaymentRequest(phoneNumber, amount, donationId)
                )
                if (result is AppRepository.RepositoryResult.Success<*>) {
                    _successMessage.value = "Payment initiated! Check your phone."
                } else if (result is AppRepository.RepositoryResult.Error) {
                    _errorMessage.value = result.message
                }
            } catch (e: Exception) {
                _errorMessage.value = "Payment failed: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // User management
    fun setCurrentUser(user: User) {
        _currentUser.value = user
    }

    fun logout() {
        _currentUser.value = null
        _donations.value = emptyList()
        _successMessage.value = "Logged out successfully"
    }

    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }

    // Load sample data for testing
    fun loadSampleData() {
        _isLoading.value = true
        viewModelScope.launch {
            kotlinx.coroutines.delay(1000)

            val sampleDonations = listOf(
                Donation(
                    id = 1,
                    donor = 1,
                    amount = 50.0,
                    description = "Fresh vegetables from local farm",
                    foodType = "Vegetables",
                    quantity = 10,
                    location = "Nairobi",
                    createdAt = "2024-01-15",
                    status = "available"
                ),
                Donation(
                    id = 2,
                    donor = 2,
                    amount = 75.0,
                    description = "Assorted fruits package",
                    foodType = "Fruits",
                    quantity = 8,
                    location = "Kisumu",
                    createdAt = "2024-01-16",
                    status = "available"
                )
            )

            _donations.value = sampleDonations
            _isLoading.value = false
        }
    }
}