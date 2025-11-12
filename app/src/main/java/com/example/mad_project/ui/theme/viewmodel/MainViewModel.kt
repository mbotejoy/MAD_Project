package com.example.mad_project.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.launch
import com.example.mad_project.data.models.*
import com.example.mad_project.repository.AppRepository

class MainViewModel : ViewModel() {

    private val repository = AppRepository()

    // LiveData for current user
    private val _currentUser = MutableLiveData<User?>()
    val currentUser: MutableLiveData<User?> get() = _currentUser

    // LiveData for all donations
    private val _donations = MutableLiveData<List<Donation>>()
    val donations: MutableLiveData<List<Donation>> get() = _donations

    // Loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: MutableLiveData<Boolean> get() = _isLoading

    // Error messages
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: MutableLiveData<String> get() = _errorMessage as MutableLiveData<String>

    // Success messages
    private val _successMessage = MutableLiveData<String?>()
    val successMessage: MutableLiveData<String> get() = _successMessage as MutableLiveData<String>

    // Computed property for available donations
    val availableDonations: List<Donation>
        get() = _donations.value?.filter { it.status == "available" } ?: emptyList()

    // Computed property for user's donations
    val myDonations: List<Donation>
        get() = _donations.value?.filter { it.donor == _currentUser.value?.id } ?: emptyList()

    // Authentication methods
    fun login(username: String, password: String) {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            when (val result = repository.login(username, password)) {
                is AppRepository.RepositoryResult.Success -> {
                    _currentUser.value = result.data.user
                    _successMessage.value = result.data.message ?: "Login successful!"
                    loadDonations() // Load donations after login
                }
                is AppRepository.RepositoryResult.Error -> {
                    _errorMessage.value = result.message
                    _currentUser.value = null
                }
            }
            _isLoading.value = false
        }
    }

    fun register(registerRequest: RegisterRequest) {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            when (val result = repository.register(registerRequest)) {
                is AppRepository.RepositoryResult.Success -> {
                    _currentUser.value = result.data.user
                    _successMessage.value = result.data.message ?: "Registration successful!"
                }
                is AppRepository.RepositoryResult.Error -> {
                    _errorMessage.value = result.message
                    _currentUser.value = null
                }
            }
            _isLoading.value = false
        }
    }

    // Donation methods
    fun loadDonations() {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            when (val result = repository.getDonations()) {
                is AppRepository.RepositoryResult.Success -> {
                    _donations.value = result.data
                }
                is AppRepository.RepositoryResult.Error -> {
                    _errorMessage.value = result.message
                    _donations.value = emptyList()
                }
            }
            _isLoading.value = false
        }
    }

    fun createDonation(donation: Donation) {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            when (val result = repository.createDonation(donation)) {
                is AppRepository.RepositoryResult.Success -> {
                    loadDonations() // Refresh the list
                    _successMessage.value = "Donation created successfully!"
                }
                is AppRepository.RepositoryResult.Error -> {
                    _errorMessage.value = result.message
                }
            }
            _isLoading.value = false
        }
    }

    fun updateDonation(donation: Donation) {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            when (val result = repository.updateDonation(donation.id, donation)) {
                is AppRepository.RepositoryResult.Success -> {
                    loadDonations() // Refresh the list
                    _successMessage.value = "Donation updated successfully!"
                }
                is AppRepository.RepositoryResult.Error -> {
                    _errorMessage.value = result.message
                }
            }
            _isLoading.value = false
        }
    }

    // MPESA Payment
    fun makeMpesaPayment(phoneNumber: String, amount: Double, donationId: Int? = null) {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            when (val result = repository.initiateMpesaPayment(
                MpesaPaymentRequest(phoneNumber, amount, donationId)
            )) {
                is AppRepository.RepositoryResult.Success -> {
                    _successMessage.value = "Payment initiated! Check your phone."
                }
                is AppRepository.RepositoryResult.Error -> {
                    _errorMessage.value = result.message
                }
            }
            _isLoading.value = false
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

    // Clear messages
    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }

    // Load sample data for testing (remove this when your API is ready)
    fun loadSampleData() {
        _isLoading.value = true
        viewModelScope.launch {
            // Simulate network delay
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
                ),
                Donation(
                    id = 3,
                    donor = 1,
                    amount = 100.0,
                    description = "Grains and cereals",
                    foodType = "Grains",
                    quantity = 15,
                    location = "Mombasa",
                    createdAt = "2024-01-14",
                    status = "claimed"
                )
            )

            _donations.value = sampleDonations
            _isLoading.value = false
        }
    }
}