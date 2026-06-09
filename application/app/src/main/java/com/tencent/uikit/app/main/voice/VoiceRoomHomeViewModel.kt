package com.tencent.uikit.app.main.voice

import androidx.lifecycle.ViewModel

class VoiceRoomHomeViewModel : ViewModel() {
    private var selectedTab: VoiceRoomHomeTab = VoiceRoomHomeTab.HOME

    fun getUiState(): VoiceRoomHomeUiState = VoiceRoomHomeUiState(
        banners = VoiceRoomHomeSampleData.banners(),
        popularRooms = VoiceRoomHomeSampleData.popularRooms(),
        newRooms = VoiceRoomHomeSampleData.newRooms(),
        guilds = VoiceRoomHomeSampleData.guilds(),
        selectedTab = selectedTab
    )

    fun selectTab(tab: VoiceRoomHomeTab): VoiceRoomHomeUiState {
        selectedTab = tab
        return getUiState()
    }
}
