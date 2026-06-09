package com.example.zegovoiceroom.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.zegovoiceroom.data.UserSessionManager
import com.example.zegovoiceroom.data.VoiceRoomRepository
import com.example.zegovoiceroom.data.model.ChatMessage
import com.example.zegovoiceroom.data.model.Seat
import com.example.zegovoiceroom.data.model.UserProfile
import com.example.zegovoiceroom.data.model.VoiceRoom

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val _profile = MutableLiveData<UserProfile?>()
    val profile: LiveData<UserProfile?> = _profile

    private val _seats = MutableLiveData<List<Seat>>()
    val seats: LiveData<List<Seat>> = _seats

    private val _messages = MutableLiveData<List<ChatMessage>>()
    val messages: LiveData<List<ChatMessage>> = _messages

    init {
        refresh()
    }

    fun refresh() {
        val currentProfile = UserSessionManager.getProfile(getApplication())
        _profile.value = currentProfile
        _seats.value = VoiceRoomRepository.defaultSeats(currentProfile)
        _messages.value = VoiceRoomRepository.previewMessages(currentProfile)
    }

    fun createRoom(roomId: String, title: String, topic: String): Pair<VoiceRoom?, String?> {
        val error = VoiceRoomRepository.validateRoom(roomId, title)
        if (error != null) return null to error

        val profile = UserSessionManager.getProfile(getApplication()) ?: return null to "Please log in again"
        val room = VoiceRoom(
            roomId = roomId,
            title = title.trim(),
            topic = topic.trim().ifBlank { "Live audio conversation" },
            hostName = profile.displayName,
            audienceCount = 0
        )
        VoiceRoomRepository.upsertRoom(getApplication(), room)
        return room to null
    }
}
