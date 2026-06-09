package com.example.zegovoiceroom.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.zegovoiceroom.data.VoiceRoomRepository
import com.example.zegovoiceroom.data.model.VoiceRoom

class VoiceRoomListViewModel(application: Application) : AndroidViewModel(application) {
    private val _rooms = MutableLiveData<List<VoiceRoom>>()
    val rooms: LiveData<List<VoiceRoom>> = _rooms

    fun refreshRooms() {
        _rooms.value = VoiceRoomRepository.getRooms(getApplication())
    }
}
