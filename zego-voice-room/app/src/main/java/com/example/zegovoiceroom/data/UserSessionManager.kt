package com.example.zegovoiceroom.data

import android.content.Context
import com.example.zegovoiceroom.data.model.UserProfile

object UserSessionManager {
    private const val PREFS_NAME = "zego_voice_room_session"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_DISPLAY_NAME = "display_name"
    private const val KEY_APP_SIGN = "app_sign"
    private val validIdPattern = Regex("[A-Za-z0-9_]+")

    fun getProfile(context: Context): UserProfile? {
        val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val userId = prefs.getString(KEY_USER_ID, null).orEmpty()
        val displayName = prefs.getString(KEY_DISPLAY_NAME, null).orEmpty()
        if (userId.isBlank() || displayName.isBlank()) return null
        return UserProfile(
            userId = userId,
            displayName = displayName,
            appSign = prefs.getString(KEY_APP_SIGN, null).orEmpty()
        )
    }

    fun saveProfile(context: Context, profile: UserProfile) {
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_USER_ID, sanitizeIdentifier(profile.userId))
            .putString(KEY_DISPLAY_NAME, profile.displayName.trim())
            .putString(KEY_APP_SIGN, profile.appSign.trim())
            .apply()
    }

    fun clear(context: Context) {
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .clear()
            .apply()
    }

    fun validate(userId: String, displayName: String): String? {
        return when {
            userId.isBlank() -> "User ID is required"
            !validIdPattern.matches(userId) -> "User ID can only contain letters, numbers, and underscores"
            displayName.isBlank() -> "Display name is required"
            else -> null
        }
    }

    fun sanitizeIdentifier(value: String): String {
        return value.trim().replace(Regex("[^A-Za-z0-9_]"), "_")
    }
}
