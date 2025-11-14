package com.example.mad_project

import android.app.Application
import com.example.mad_project.data.models.SessionManager

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Initialize the SessionManager with the application context
        SessionManager.init(this)
    }
}
