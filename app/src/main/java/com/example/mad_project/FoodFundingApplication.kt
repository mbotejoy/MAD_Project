// FoodFundingApplication.kt
import android.app.Application


class FoodFundingApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Initialize database and perform any setup
        AppDatabase.getInstance(this)
    }
}