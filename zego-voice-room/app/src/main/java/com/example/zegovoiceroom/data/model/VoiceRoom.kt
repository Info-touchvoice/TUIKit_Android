package com.example.zegovoiceroom.data.model

data class VoiceRoom(
    val roomId: String,
    val title: String,
    val topic: String,
    val hostName: String,
    val audienceCount: Int = 0
)
