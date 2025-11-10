// data/local/daos/DonationDao.kt
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DonationDao {

    @Query("SELECT * FROM donations")
    fun getAllDonations(): Flow<List<DonationEntity>>

    @Query("SELECT * FROM donations WHERE id = :donationId")
    suspend fun getDonation(donationId: Int): DonationEntity?

    @Query("SELECT * FROM donations WHERE status = 'available'")
    fun getAvailableDonations(): Flow<List<DonationEntity>>

    @Query("SELECT * FROM donations WHERE donor = :userId")
    fun getDonationsByUser(userId: Int): Flow<List<DonationEntity>>

    @Query("SELECT * FROM donations WHERE beneficiary = :userId")
    fun getClaimedDonationsByUser(userId: Int): Flow<List<DonationEntity>>

    @Query("SELECT * FROM donations WHERE isSynced = 0")
    suspend fun getUnsyncedDonations(): List<DonationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDonation(donation: DonationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllDonations(donations: List<DonationEntity>)

    @Update
    suspend fun updateDonation(donation: DonationEntity)

    @Query("DELETE FROM donations WHERE id = :donationId")
    suspend fun deleteDonation(donationId: Int)

    @Query("DELETE FROM donations")
    suspend fun clearAllDonations()

    @Query("SELECT * FROM donations")
    suspend fun getAllDonationsSync(): List<DonationEntity>
}