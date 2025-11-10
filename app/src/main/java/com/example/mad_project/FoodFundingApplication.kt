// FoodFundingApplication.kt
import android.app.Application
import com.example.mad_project.data.local.AppDatabase

class FoodFundingApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Initialize database and perform any setup
        AppDatabase.getInstance(this)
    }
}