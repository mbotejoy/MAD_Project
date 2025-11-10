// data/local/entities/MpesaTransactionEntity.kt
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mpesa_transactions")
data class MpesaTransactionEntity(
    @PrimaryKey
    val id: Int,
    val user: Int,
    val amount: Double,
    val phoneNumber: String,
    val transactionDate: String,
    val status: String, // "pending", "completed", "failed"
    val reference: String,
    val lastUpdated: Long = System.currentTimeMillis()
)