package com.example.atomicxcore

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : ViewModel() {

    private val repository = FakeRoomRepository()
    private val allRooms = repository.loadRooms()
    private val _rooms = MutableStateFlow(allRooms)

    val rooms: StateFlow<List<VoiceRoom>> = _rooms.asStateFlow()

    fun selectTab(position: Int) {
        _rooms.value = when (position) {
            POPULAR_TAB -> allRooms.sortedByDescending { it.popularity }
            DISCOVER_TAB -> allRooms.filter { it.pkEnabled } + allRooms.filterNot { it.pkEnabled }
            NEW_TAB -> allRooms.sortedByDescending { it.id }
            else -> allRooms
        }
    }

    private companion object {
        const val POPULAR_TAB = 1
        const val DISCOVER_TAB = 2
        const val NEW_TAB = 3
    }
}
