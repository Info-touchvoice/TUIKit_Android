package com.example.zegovoiceroom.ui.room

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.zegovoiceroom.data.UserSessionManager
import com.example.zegovoiceroom.data.model.UserProfile
import com.example.zegovoiceroom.util.ZegoConfig

class VoiceRoomViewModel(application: Application) : AndroidViewModel(application) {
    fun currentProfile(): UserProfile? = UserSessionManager.getProfile(getApplication())

    fun appId(): Long = ZegoConfig.APP_ID

    fun appSign(profile: UserProfile): String {
        return profile.appSign.ifBlank { ZegoConfig.DEFAULT_APP_SIGN }
    }
}
