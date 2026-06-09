package com.example.zegovoiceroom.ui.voice

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.zegovoiceroom.data.ChatMessage
import com.example.zegovoiceroom.data.Seat
import com.example.zegovoiceroom.data.VoiceRoom
import com.example.zegovoiceroom.data.VoiceRoomRepository

class VoiceRoomViewModel : ViewModel() {
    private val repository = VoiceRoomRepository()

    private val _room = MutableLiveData<VoiceRoom>()
    val room: LiveData<VoiceRoom> = _room

    private val _seats = MutableLiveData<List<Seat>>()
    val seats: LiveData<List<Seat>> = _seats

    private val _messages = MutableLiveData<List<ChatMessage>>()
    val messages: LiveData<List<ChatMessage>> = _messages

    private val _isMuted = MutableLiveData(false)
    val isMuted: LiveData<Boolean> = _isMuted

    private val _pkScore = MutableLiveData("PK 120 : 96")
    val pkScore: LiveData<String> = _pkScore

    fun load(roomId: String) {
        val loadedRoom = repository.room(roomId)
        _room.value = loadedRoom
        _seats.value = repository.seats(loadedRoom)
        _messages.value = repository.messages(loadedRoom)
    }

    fun toggleMute() {
        _isMuted.value = _isMuted.value != true
    }

    fun sendMessage(sender: String, message: String) {
        val trimmedMessage = message.trim()
        if (trimmedMessage.isBlank()) return

        val current = _messages.value.orEmpty()
        _messages.value = current + ChatMessage(sender, trimmedMessage)
    }

    fun sendGift(sender: String) {
        val current = _messages.value.orEmpty()
        _messages.value = current + ChatMessage(sender, "sent a Galaxy gift", isGift = true)
    }
}
