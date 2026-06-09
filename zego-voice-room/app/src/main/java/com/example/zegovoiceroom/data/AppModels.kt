package com.example.zegovoiceroom.data

import androidx.annotation.DrawableRes
import com.example.zegovoiceroom.R

data class Banner(
    val title: String,
    val subtitle: String,
    @DrawableRes val imageRes: Int
)

data class VoiceRoom(
    val id: String,
    val name: String,
    val hostName: String,
    val onlineUsers: Int,
    @DrawableRes val coverRes: Int,
    @DrawableRes val hostAvatarRes: Int,
    val isPkEnabled: Boolean
)

data class Seat(
    val index: Int,
    val label: String,
    val userName: String?,
    val isHostSeat: Boolean,
    val isMuted: Boolean
)

data class ChatMessage(
    val sender: String,
    val message: String,
    val isGift: Boolean = false
)

data class UserProfile(
    val userId: String,
    val displayName: String,
    val level: Int,
    val coins: Int,
    val isVip: Boolean,
    @DrawableRes val avatarRes: Int
)

object SampleData {
    val banners = listOf(
        Banner("Friday PK Night", "Challenge trending hosts and win coin rain.", R.drawable.bg_banner_purple),
        Banner("Creator Picks", "Discover high-quality voice rooms curated daily.", R.drawable.bg_banner_blue),
        Banner("Gift Carnival", "Send gifts to unlock VIP room effects.", R.drawable.bg_banner_orange)
    )

    val rooms = listOf(
        VoiceRoom("room_music_001", "Lo-fi Music Lounge", "Mia", 1284, R.drawable.bg_room_cover_music, R.drawable.ic_avatar_1, true),
        VoiceRoom("room_game_002", "Gaming Squad Voice", "Kai", 892, R.drawable.bg_room_cover_game, R.drawable.ic_avatar_2, true),
        VoiceRoom("room_chat_003", "Late Night Chat", "Ava", 647, R.drawable.bg_room_cover_chat, R.drawable.ic_avatar_3, false),
        VoiceRoom("room_story_004", "Story Circle", "Noah", 431, R.drawable.bg_room_cover_story, R.drawable.ic_avatar_4, false),
        VoiceRoom("room_vip_005", "VIP Afterparty", "Luna", 2048, R.drawable.bg_room_cover_vip, R.drawable.ic_avatar_5, true)
    )

    fun profileFor(userId: String): UserProfile {
        val cleanId = userId.ifBlank { "guest" }
        return UserProfile(
            userId = cleanId,
            displayName = "Voice $cleanId",
            level = 18,
            coins = 12_450,
            isVip = true,
            avatarRes = R.drawable.ic_avatar_1
        )
    }

    fun seatsFor(room: VoiceRoom): List<Seat> {
        return buildList {
            add(Seat(0, "Host", room.hostName, isHostSeat = true, isMuted = false))
            repeat(8) { index ->
                val speaker = when (index) {
                    0 -> "Emma"
                    2 -> "Leo"
                    5 -> "Nora"
                    else -> null
                }
                add(Seat(index + 1, "Seat ${index + 1}", speaker, isHostSeat = false, isMuted = index == 2))
            }
        }
    }

    fun welcomeMessages(room: VoiceRoom): List<ChatMessage> = listOf(
        ChatMessage("System", "Welcome to ${room.name}. Be kind and enjoy the room."),
        ChatMessage(room.hostName, "The mic is open for requests."),
        ChatMessage("Emma", "Sending roses to the host!", isGift = true)
    )
}
