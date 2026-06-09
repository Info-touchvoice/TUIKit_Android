package com.example.zegovoiceroom.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.zegovoiceroom.data.Banner
import com.example.zegovoiceroom.data.UserProfile
import com.example.zegovoiceroom.data.VoiceRoom
import com.example.zegovoiceroom.data.VoiceRoomRepository

class MainViewModel : ViewModel() {
    private val repository = VoiceRoomRepository()

    private val _banners = MutableLiveData(repository.banners())
    val banners: LiveData<List<Banner>> = _banners

    private val _rooms = MutableLiveData(repository.popularRooms())
    val rooms: LiveData<List<VoiceRoom>> = _rooms

    fun search(query: String) {
        _rooms.value = repository.searchRooms(query)
    }

    fun profile(userId: String): UserProfile = com.example.zegovoiceroom.data.SampleData.profileFor(userId)
}
