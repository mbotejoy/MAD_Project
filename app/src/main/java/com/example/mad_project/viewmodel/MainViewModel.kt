// viewmodel/MainViewModel.kt
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.MutableLiveData
import com.example.mad_project.AppRepository
import com.example.mad_project.Donation
import com.example.mad_project.MpesaPaymentRequest
import com.example.mad_project.RegisterRequest
import com.example.mad_project.User
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val repository = AppRepository

    // LiveData for UI observation
    val currentUser = MutableLiveData<User?>()
    val donations = MutableLiveData<List<Donation>>()
    val isLoading = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String>()
    val successMessage = MutableLiveData<String>()

    // Authentication
    fun login(username: String, password: String) {
        viewModelScope.launch {
            isLoading.value = true
            when (val result = repository.login(username, password)) {
                is Result.Success -> {
                    currentUser.value = result.data.user
                    errorMessage.value = null
                    successMessage.value = "Login successful!"
                }
                is Result.Failure -> {
                    errorMessage.value = result.exception.message
                    currentUser.value = null
                }
            }
            isLoading.value = false
        }
    }

    fun register(registerRequest: RegisterRequest) {
        viewModelScope.launch {
            isLoading.value = true
            when (val result = repository.register(registerRequest)) {
                is Result.Success -> {
                    currentUser.value = result.data.user
                    errorMessage.value = null
                    successMessage.value = "Registration successful!"
                }
                is Result.Failure -> {
                    errorMessage.value = result.exception.message
                    currentUser.value = null
                }
            }
            isLoading.value = false
        }
    }

    // Donations
    fun loadDonations() {
        viewModelScope.launch {
            isLoading.value = true
            when (val result = repository.getDonations()) {
                is Result.Success -> {
                    donations.value = result.data
                    errorMessage.value = null
                }
                is Result.Failure -> {
                    errorMessage.value = result.exception.message
                    donations.value = emptyList()
                }
            }
            isLoading.value = false
        }
    }

    fun createDonation(donation: Donation) {
        viewModelScope.launch {
            isLoading.value = true
            when (val result = repository.createDonation(donation)) {
                is Result.Success -> {
                    loadDonations() // Refresh list
                    successMessage.value = "Donation created successfully!"
                }
                is Result.Failure -> {
                    errorMessage.value = result.exception.message
                }
            }
            isLoading.value = false
        }
    }

    // MPESA Payment
    fun makeMpesaPayment(phoneNumber: String, amount: Double, donationId: Int? = null) {
        viewModelScope.launch {
            isLoading.value = true
            when (val result = repository.initiateMpesaPayment(
                MpesaPaymentRequest(phoneNumber, amount, donationId)
            )) {
                is Result.Success -> {
                    successMessage.value = "Payment initiated! Check your phone."
                }
                is Result.Failure -> {
                    errorMessage.value = result.exception.message
                }
            }
            isLoading.value = false
        }
    }

    fun logout() {
        currentUser.value = null
        successMessage.value = "Logged out successfully"
    }
}

// Result sealed class
sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Failure(val exception: Exception) : Result<Nothing>()
}