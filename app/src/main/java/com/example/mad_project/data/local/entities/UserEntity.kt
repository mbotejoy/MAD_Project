// data/local/entities/UserEntity.kt
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: Int,
    val email: String,
    val username: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String? = null,
    val dateJoined: String,
    val isDonor: Boolean = false,
    val isBeneficiary: Boolean = false,
    val lastSynced: Long = System.currentTimeMillis()
)