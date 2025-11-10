// data/local/daos/MpesaTransactionDao.kt
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MpesaTransactionDao {

    @Query("SELECT * FROM mpesa_transactions")
    fun getAllTransactions(): Flow<List<MpesaTransactionEntity>>

    @Query("SELECT * FROM mpesa_transactions WHERE user = :userId")
    fun getTransactionsByUser(userId: Int): Flow<List<MpesaTransactionEntity>>

    @Query("SELECT * FROM mpesa_transactions WHERE id = :transactionId")
    suspend fun getTransaction(transactionId: Int): MpesaTransactionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: MpesaTransactionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTransactions(transactions: List<MpesaTransactionEntity>)

    @Update
    suspend fun updateTransaction(transaction: MpesaTransactionEntity)

    @Query("DELETE FROM mpesa_transactions WHERE id = :transactionId")
    suspend fun deleteTransaction(transactionId: Int)
}