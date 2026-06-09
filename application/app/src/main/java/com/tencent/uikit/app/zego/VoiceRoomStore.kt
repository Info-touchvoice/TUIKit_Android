package com.tencent.uikit.app.zego

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class VoiceRoom(
    val roomId: String,
    val roomName: String,
    val createdAt: Long
)

object VoiceRoomStore {
    private const val PREFS_NAME = "zego_live_audio_room"
    private const val KEY_ROOMS = "rooms"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USER_NAME = "user_name"
    private val validZegoId = Regex("^[A-Za-z0-9_]{1,64}$")

    fun isValidZegoId(value: String): Boolean = validZegoId.matches(value)

    fun getUserId(context: Context): String {
        val stored = prefs(context).getString(KEY_USER_ID, null)
        if (!stored.isNullOrBlank()) {
            return stored
        }
        return "user_${System.currentTimeMillis() % 100000}"
    }

    fun getUserName(context: Context): String {
        val stored = prefs(context).getString(KEY_USER_NAME, null)
        if (!stored.isNullOrBlank()) {
            return stored
        }
        return getUserId(context)
    }

    fun saveUser(context: Context, userId: String, userName: String) {
        prefs(context).edit()
            .putString(KEY_USER_ID, userId)
            .putString(KEY_USER_NAME, userName)
            .apply()
    }

    fun getRooms(context: Context): List<VoiceRoom> {
        val raw = prefs(context).getString(KEY_ROOMS, "[]").orEmpty()
        return runCatching {
            val array = JSONArray(raw)
            buildList {
                for (index in 0 until array.length()) {
                    val item = array.optJSONObject(index) ?: continue
                    val roomId = item.optString("roomId")
                    if (roomId.isBlank()) {
                        continue
                    }
                    add(
                        VoiceRoom(
                            roomId = roomId,
                            roomName = item.optString("roomName", roomId),
                            createdAt = item.optLong("createdAt", 0L)
                        )
                    )
                }
            }.sortedByDescending { it.createdAt }
        }.getOrElse {
            emptyList()
        }
    }

    fun saveRoom(context: Context, room: VoiceRoom) {
        val rooms = getRooms(context).filterNot { it.roomId == room.roomId }.toMutableList()
        rooms.add(0, room)
        val array = JSONArray()
        rooms.forEach {
            array.put(
                JSONObject()
                    .put("roomId", it.roomId)
                    .put("roomName", it.roomName)
                    .put("createdAt", it.createdAt)
            )
        }
        prefs(context).edit().putString(KEY_ROOMS, array.toString()).apply()
    }

    private fun prefs(context: Context) =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}
