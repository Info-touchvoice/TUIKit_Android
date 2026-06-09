package com.example.zegovoiceroom.data

import android.content.Context
import com.example.zegovoiceroom.data.model.ChatMessage
import com.example.zegovoiceroom.data.model.Seat
import com.example.zegovoiceroom.data.model.UserProfile
import com.example.zegovoiceroom.data.model.UserRole
import com.example.zegovoiceroom.data.model.VoiceRoom
import org.json.JSONArray
import org.json.JSONObject

object VoiceRoomRepository {
    private const val PREFS_NAME = "zego_voice_room_rooms"
    private const val KEY_ROOMS = "rooms"
    private val validRoomPattern = Regex("[A-Za-z0-9_]+")

    fun getRooms(context: Context): List<VoiceRoom> {
        val stored = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_ROOMS, null)
        return stored?.let(::decodeRooms).takeUnless { it.isNullOrEmpty() } ?: defaultRooms()
    }

    fun upsertRoom(context: Context, room: VoiceRoom): List<VoiceRoom> {
        val rooms = getRooms(context).filterNot { it.roomId == room.roomId } + room
        saveRooms(context, rooms)
        return rooms
    }

    fun validateRoom(roomId: String, title: String): String? {
        return when {
            roomId.isBlank() -> "Room ID is required"
            !validRoomPattern.matches(roomId) -> "Room ID can only contain letters, numbers, and underscores"
            title.isBlank() -> "Room title is required"
            else -> null
        }
    }

    fun defaultSeats(profile: UserProfile?): List<Seat> {
        return buildList {
            add(Seat(index = 0, role = UserRole.HOST, occupiedBy = profile))
            repeat(7) { index ->
                add(Seat(index = index + 1, role = UserRole.AUDIENCE))
            }
        }
    }

    fun previewMessages(profile: UserProfile?): List<ChatMessage> {
        val displayName = profile?.displayName ?: "Host"
        return listOf(
            ChatMessage(1, displayName, "Welcome to the voice room."),
            ChatMessage(2, "Audience", "Hi everyone!"),
            ChatMessage(3, displayName, "Tap Host Room or Join as Audience to open ZEGO UIKit.")
        )
    }

    private fun saveRooms(context: Context, rooms: List<VoiceRoom>) {
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_ROOMS, encodeRooms(rooms))
            .apply()
    }

    private fun defaultRooms(): List<VoiceRoom> {
        return listOf(
            VoiceRoom(
                roomId = "daily_voice_lounge",
                title = "Daily Voice Lounge",
                topic = "Open chat for community members",
                hostName = "ZEGO Host",
                audienceCount = 12
            ),
            VoiceRoom(
                roomId = "music_talk",
                title = "Music Talk",
                topic = "Share favorite tracks and stories",
                hostName = "Audio Team",
                audienceCount = 8
            )
        )
    }

    private fun encodeRooms(rooms: List<VoiceRoom>): String {
        val array = JSONArray()
        rooms.forEach { room ->
            array.put(
                JSONObject()
                    .put("roomId", room.roomId)
                    .put("title", room.title)
                    .put("topic", room.topic)
                    .put("hostName", room.hostName)
                    .put("audienceCount", room.audienceCount)
            )
        }
        return array.toString()
    }

    private fun decodeRooms(raw: String): List<VoiceRoom> {
        return runCatching {
            val array = JSONArray(raw)
            List(array.length()) { index ->
                val item = array.getJSONObject(index)
                VoiceRoom(
                    roomId = item.getString("roomId"),
                    title = item.getString("title"),
                    topic = item.optString("topic"),
                    hostName = item.optString("hostName"),
                    audienceCount = item.optInt("audienceCount")
                )
            }
        }.getOrDefault(emptyList())
    }
}
