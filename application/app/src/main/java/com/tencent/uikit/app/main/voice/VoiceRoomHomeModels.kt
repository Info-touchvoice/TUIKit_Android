package com.tencent.uikit.app.main.voice

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.tencent.uikit.app.R

data class VoiceRoomHomeUiState(
    val banners: List<VoiceRoomBanner>,
    val popularRooms: List<VoiceRoomCard>,
    val newRooms: List<VoiceRoomCard>,
    val guilds: List<VoiceRoomGuild>,
    val selectedTab: VoiceRoomHomeTab = VoiceRoomHomeTab.HOME
)

enum class VoiceRoomHomeTab {
    HOME,
    ROOMS,
    CREATE,
    GUILDS,
    PROFILE
}

data class VoiceRoomBanner(
    val title: String,
    val subtitle: String,
    val action: String,
    @DrawableRes val backgroundRes: Int
)

data class VoiceRoomCard(
    val title: String,
    val host: String,
    val tag: String,
    val listenerCount: String,
    val seatSummary: String,
    @DrawableRes val avatarBackgroundRes: Int
)

data class VoiceRoomGuild(
    val name: String,
    val memberCount: String,
    val description: String,
    @ColorRes val accentColorRes: Int
)

object VoiceRoomHomeSampleData {
    fun banners(): List<VoiceRoomBanner> = listOf(
        VoiceRoomBanner(
            title = "Late Night Voice Party",
            subtitle = "Meet new friends, grab a mic, and keep the room glowing.",
            action = "Join trending",
            backgroundRes = R.drawable.app_voice_home_banner_gradient_1
        ),
        VoiceRoomBanner(
            title = "Family Battle Hour",
            subtitle = "Guilds compete with gifts, seats, and room missions.",
            action = "Explore guilds",
            backgroundRes = R.drawable.app_voice_home_banner_gradient_2
        ),
        VoiceRoomBanner(
            title = "New Host Spotlight",
            subtitle = "Fresh rooms are open now with music, games, and chat.",
            action = "Discover rooms",
            backgroundRes = R.drawable.app_voice_home_banner_gradient_3
        )
    )

    fun popularRooms(): List<VoiceRoomCard> = listOf(
        VoiceRoomCard("MICO Lounge", "Ava", "Hot", "8.4K", "7/9 seats", R.drawable.app_voice_home_avatar_hot),
        VoiceRoomCard("Karaoke Stars", "Leo", "Music", "6.1K", "6/9 seats", R.drawable.app_voice_home_avatar_music),
        VoiceRoomCard("Game Night", "Mina", "PK", "5.7K", "8/9 seats", R.drawable.app_voice_home_avatar_game),
        VoiceRoomCard("Chill Talk", "Noah", "Chat", "4.9K", "5/9 seats", R.drawable.app_voice_home_avatar_chat)
    )

    fun newRooms(): List<VoiceRoomCard> = listOf(
        VoiceRoomCard("Fresh Mic", "Ivy", "New", "268", "2/9 seats", R.drawable.app_voice_home_avatar_new),
        VoiceRoomCard("Morning Vibes", "Sam", "Talk", "341", "3/9 seats", R.drawable.app_voice_home_avatar_chat),
        VoiceRoomCard("Open Stage", "Nia", "Sing", "512", "4/9 seats", R.drawable.app_voice_home_avatar_music)
    )

    fun guilds(): List<VoiceRoomGuild> = listOf(
        VoiceRoomGuild("Star Family", "12.8K members", "Daily room parties and PK teams.", R.color.app_voice_home_purple),
        VoiceRoomGuild("Neon Guild", "9.6K members", "Music hosts, live events, and rewards.", R.color.app_voice_home_blue),
        VoiceRoomGuild("Galaxy Crew", "7.2K members", "Friendly chat rooms for global voices.", R.color.app_voice_home_orange)
    )
}
