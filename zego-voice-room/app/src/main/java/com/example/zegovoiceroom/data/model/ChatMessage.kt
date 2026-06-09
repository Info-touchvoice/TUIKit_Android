package com.example.zegovoiceroom.data.model

data class ChatMessage(
    val id: Long,
    val senderName: String,
    val message: String,
    val timestampMillis: Long = System.currentTimeMillis()
)
