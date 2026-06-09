package com.example.zegovoiceroom.data

import android.content.Context
import androidx.core.content.edit

class UserSession(context: Context) {
    private val preferences = context.getSharedPreferences("zego_voice_room_session", Context.MODE_PRIVATE)

    var userId: String
        get() = preferences.getString(KEY_USER_ID, "").orEmpty()
        set(value) = preferences.edit { putString(KEY_USER_ID, value.trim()) }

    val isLoggedIn: Boolean
        get() = userId.isNotBlank()

    fun clear() {
        preferences.edit { clear() }
    }

    private companion object {
        const val KEY_USER_ID = "user_id"
    }
}
