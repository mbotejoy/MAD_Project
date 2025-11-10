// repository/AppRepository.kt
import android.content.Context
import com.example.mad_project.data.models.MpesaTransaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class AppRepository(private val context: Context) {
    private val apiService = RetrofitInstance.apiService
    private val database = AppDatabase.getInstance(context)
    private val userDao = database.userDao()
    private val donationDao = database.donationDao()
    private val transactionDao = database.mpesaTransactionDao()

    // User Operations
    suspend fun login(username: String, password: String): Result<LoginResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.login(LoginRequest(username, password))
                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!
                    if (loginResponse.success && loginResponse.user != null) {
                        // Save user to local database
                        val userEntity = loginResponse.user.toUserEntity()
                        userDao.insertUser(userEntity)
                        Result.success(loginResponse)
                    } else {
                        Result.failure(Exception(loginResponse.message ?: "Login failed"))
                    }
                } else {
                    Result.failure(Exception("Login failed: ${response.code()}"))
                }
            } catch (e: Exception) {
                // Try to get user from local database (offline mode)
                val localUser = userDao.getUserByUsername(username)
                if (localUser != null) {
                    // For demo purposes - in real app, you'd verify password differently
                    Result.success(LoginResponse(
                        success = true,
                        user = localUser.toUser(),
                        message = "Offline login"
                    ))
                } else {
                    Result.failure(e)
                }
            }
        }
    }

    // Donation Operations with Local Database
    suspend fun getDonations(): Result<List<Donation>> {
        return withContext(Dispatchers.IO) {
            try {
                // Try to get from network first
                val response = apiService.getDonations()
                if (response.isSuccessful) {
                    val donations = response.body() ?: emptyList()
                    // Save to local database
                    donationDao.insertAllDonations(donations.map { it.toDonationEntity() })
                    Result.success(donations)
                } else {
                    // Fallback to local database
                    val localDonations = donationDao.getAllDonationsSync()
                    Result.success(localDonations.map { it.toDonation() })
                }
            } catch (e: Exception) {
                // Fallback to local database
                val localDonations = donationDao.getAllDonationsSync()
                if (localDonations.isNotEmpty()) {
                    Result.success(localDonations.map { it.toDonation() })
                } else {
                    Result.failure(e)
                }
            }
        }
    }

    fun getDonationsFlow(): Flow<List<Donation>> {
        return donationDao.getAllDonations().map { entities ->
            entities.map { it.toDonation() }
        }
    }

    fun getAvailableDonationsFlow(): Flow<List<Donation>> {
        return donationDao.getAvailableDonations().map { entities ->
            entities.map { it.toDonation() }
        }
    }

    fun getUserDonationsFlow(userId: Int): Flow<List<Donation>> {
        return donationDao.getDonationsByUser(userId).map { entities ->
            entities.map { it.toDonation() }
        }
    }

    suspend fun createDonation(donation: Donation): Result<Donation> {
        return withContext(Dispatchers.IO) {
            try {
                // Generate temporary ID for offline use
                val tempId = System.currentTimeMillis().toInt()
                val donationEntity = donation.copy(id = tempId).toDonationEntity(isSynced = false)

                // Save to local database immediately
                donationDao.insertDonation(donationEntity)

                // Try to sync with server
                val response = apiService.createDonation(donation)
                if (response.isSuccessful && response.body() != null) {
                    val serverDonation = response.body()!!
                    // Update local database with server ID
                    donationDao.deleteDonation(tempId)
                    donationDao.insertDonation(serverDonation.toDonationEntity(isSynced = true))
                    Result.success(serverDonation)
                } else {
                    Result.success(donation.copy(id = tempId))
                }
            } catch (e: Exception) {
                // Save to local database for offline use
                val tempId = System.currentTimeMillis().toInt()
                val donationEntity = donation.copy(id = tempId).toDonationEntity(isSynced = false)
                donationDao.insertDonation(donationEntity)
                Result.success(donation.copy(id = tempId))
            }
        }
    }

    // Sync Operations
    suspend fun syncUnsyncedDonations() {
        withContext(Dispatchers.IO) {
            try {
                val unsyncedDonations = donationDao.getUnsyncedDonations()
                for (unsynced in unsyncedDonations) {
                    val donation = unsynced.toDonation()
                    val response = apiService.createDonation(donation)
                    if (response.isSuccessful && response.body() != null) {
                        val serverDonation = response.body()!!
                        // Update local database
                        donationDao.deleteDonation(unsynced.id)
                        donationDao.insertDonation(serverDonation.toDonationEntity(isSynced = true))
                    }
                }
            } catch (e: Exception) {
                // Sync failed, will try again later
            }
        }
    }

    // MPESA Transactions
    suspend fun saveMpesaTransaction(transaction: MpesaTransaction): Result<MpesaTransaction> {
        return withContext(Dispatchers.IO) {
            try {
                transactionDao.insertTransaction(transaction.toMpesaTransactionEntity())
                Result.success(transaction)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    fun getMpesaTransactionsFlow(userId: Int): Flow<List<MpesaTransaction>> {
        return transactionDao.getTransactionsByUser(userId).map { entities ->
            entities.map { it.toMpesaTransaction() }
        }
    }
}

// Extension functions for conversion between Entity and Domain models
fun User.toUserEntity(): UserEntity {
    return UserEntity(
        id = this.id,
        email = this.email,
        username = this.username,
        firstName = this.firstName,
        lastName = this.lastName,
        phoneNumber = this.phoneNumber,
        dateJoined = this.dateJoined,
        isDonor = this.isDonor,
        isBeneficiary = this.isBeneficiary
    )
}

fun UserEntity.toUser(): User {
    return User(
        id = this.id,
        email = this.email,
        username = this.username,
        firstName = this.firstName,
        lastName = this.lastName,
        phoneNumber = this.phoneNumber,
        dateJoined = this.dateJoined,
        isDonor = this.isDonor,
        isBeneficiary = this.isBeneficiary
    )
}

fun Donation.toDonationEntity(isSynced: Boolean = true): DonationEntity {
    return DonationEntity(
        id = this.id,
        donor = this.donor,
        amount = this.amount,
        description = this.description,
        foodType = this.foodType,
        quantity = this.quantity,
        location = this.location,
        createdAt = this.createdAt,
        status = this.status,
        beneficiary = this.beneficiary,
        isSynced = isSynced
    )
}

fun DonationEntity.toDonation(): Donation {
    return Donation(
        id = this.id,
        donor = this.donor,
        amount = this.amount,
        description = this.description,
        foodType = this.foodType,
        quantity = this.quantity,
        location = this.location,
        createdAt = this.createdAt,
        status = this.status,
        beneficiary = this.beneficiary
    )
}

fun MpesaTransaction.toMpesaTransactionEntity(): MpesaTransactionEntity {
    return MpesaTransactionEntity(
        id = this.id,
        user = this.user,
        amount = this.amount,
        phoneNumber = this.phoneNumber,
        transactionDate = this.transactionDate,
        status = this.status,
        reference = this.reference
    )
}

fun MpesaTransactionEntity.toMpesaTransaction(): MpesaTransaction {
    return MpesaTransaction(
        id = this.id,
        user = this.user,
        amount = this.amount,
        phoneNumber = this.phoneNumber,
        transactionDate = this.transactionDate,
        status = this.status,
        reference = this.reference
    )
}