// data/local/entities/DonationEntity.kt
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "donations")
data class DonationEntity(
    @PrimaryKey
    val id: Int,
    val donor: Int, // User ID
    val amount: Double,
    val description: String,
    val foodType: String,
    val quantity: Int,
    val location: String,
    val createdAt: String,
    val status: String, // "available", "claimed", "delivered"
    val beneficiary: Int? = null, // User ID of beneficiary
    val isSynced: Boolean = true, // For offline creation
    val lastUpdated: Long = System.currentTimeMillis()
)