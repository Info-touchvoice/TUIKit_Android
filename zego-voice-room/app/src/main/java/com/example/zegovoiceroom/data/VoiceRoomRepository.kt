package com.example.zegovoiceroom.data

class VoiceRoomRepository {
    fun banners(): List<Banner> = SampleData.banners

    fun popularRooms(): List<VoiceRoom> = SampleData.rooms.sortedByDescending { it.onlineUsers }

    fun searchRooms(query: String): List<VoiceRoom> {
        val normalizedQuery = query.trim()
        if (normalizedQuery.isBlank()) return popularRooms()

        return SampleData.rooms.filter { room ->
            room.name.contains(normalizedQuery, ignoreCase = true) ||
                room.hostName.contains(normalizedQuery, ignoreCase = true) ||
                room.id.contains(normalizedQuery, ignoreCase = true)
        }
    }

    fun room(roomId: String): VoiceRoom {
        return SampleData.rooms.firstOrNull { it.id == roomId }
            ?: SampleData.rooms.first()
    }

    fun seats(room: VoiceRoom): List<Seat> = SampleData.seatsFor(room)

    fun messages(room: VoiceRoom): List<ChatMessage> = SampleData.welcomeMessages(room)
}
