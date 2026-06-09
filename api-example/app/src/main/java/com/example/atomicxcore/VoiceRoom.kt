package com.example.atomicxcore

data class VoiceRoom(
    val id: Int,
    val title: String,
    val countryCode: String,
    val vipBadge: String,
    val welcomeText: String,
    val popularity: Int,
    val pkEnabled: Boolean,
    val rankIcons: Int,
    val avatarInitials: String,
    val avatarStartColor: String,
    val avatarEndColor: String,
    val label: String
)
