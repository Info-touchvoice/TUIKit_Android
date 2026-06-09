package com.example.zegovoiceroom.data.model

data class Seat(
    val index: Int,
    val role: UserRole,
    val occupiedBy: UserProfile? = null,
    val muted: Boolean = false
)
