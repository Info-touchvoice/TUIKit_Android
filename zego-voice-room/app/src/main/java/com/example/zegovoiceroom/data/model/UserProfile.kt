package com.example.zegovoiceroom.data.model

data class UserProfile(
    val userId: String,
    val displayName: String,
    val appSign: String
) {
    val initials: String
        get() = displayName.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "U"
}
