// viewmodel/MainViewModel.kt
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.MutableLiveData
import com.example.mad_project.data.network.MpesaPaymentRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AppRepository(application.applicationContext)

    // LiveData for UI observation
    val currentUser = MutableLiveData<User?>()
    val isLoading = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String>()
    val successMessage = MutableLiveData<String>()

    // Flows for database observations
    val donations: Flow<List<Donation>> = repository.getDonationsFlow()
    val availableDonations: Flow<List<Donation>> = repository.getAvailableDonationsFlow()

    // Authentication
    fun login(username: String, password: String) {
        viewModelScope.launch {
            isLoading.value = true
            when (val result = repository.login(username, password)) {
                is Result.Success -> {
                    currentUser.value = result.data.user
                    errorMessage.value = null
                    successMessage.value = "Login successful!"
                    // Load donations after login
                    loadDonations()
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

    // Donations with database support
    fun loadDonations() {
        viewModelScope.launch {
            isLoading.value = true
            // This will trigger the flow to update
            repository.getDonations()
            isLoading.value = false
        }
    }

    fun createDonation(donation: Donation) {
        viewModelScope.launch {
            isLoading.value = true
            when (val result = repository.createDonation(donation)) {
                is Result.Success -> {
                    successMessage.value = "Donation created successfully!"
                    // Sync any unsynced donations
                    repository.syncUnsyncedDonations()
                }
                is Result.Failure -> {
                    errorMessage.value = result.exception.message
                }
            }
            isLoading.value = false
        }
    }

    fun getUserDonationsFlow(userId: Int): Flow<List<Donation>> {
        return repository.getUserDonationsFlow(userId)
    }

    // MPESA Payment
    fun makeMpesaPayment(phoneNumber: String, amount: Double, donationId: Int? = null) {
        viewModelScope.launch {
            isLoading.value = true
            when (val result = repository.initiateMpesaPayment(
                MpesaPaymentRequest(phoneNumber, amount, donationId)
            )) {
                is Result.Success -> {
                    // Save transaction to local database
                    repository.saveMpesaTransaction(result.data)
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

    // Sync data when app starts or network available
    fun syncData() {
        viewModelScope.launch {
            repository.syncUnsyncedDonations()
        }
    }
}