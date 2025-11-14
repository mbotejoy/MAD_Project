
package com.example.mad_project.data.models

import android.content.Context
import android.content.SharedPreferences
import com.example.mad_project.ui.theme.viewmodel.UserState
import com.google.gson.Gson

object SessionManager {

    private const val PREFS_NAME = "mad_project_prefs"
    private const val KEY_USER = "user_session"
    private lateinit var sharedPreferences: SharedPreferences
    private val gson = Gson()

    // This must be called once from the Application class
    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveUser(user: UserState) {
        val userJson = gson.toJson(user)
        sharedPreferences.edit().putString(KEY_USER, userJson).apply()
    }

    fun getUser(): UserState? {
        val userJson = sharedPreferences.getString(KEY_USER, null)
        return if (userJson != null) {
            gson.fromJson(userJson, UserState::class.java)
        } else {
            null
        }
    }

    fun clearSession() {
        sharedPreferences.edit().remove(KEY_USER).apply()
    }
}
