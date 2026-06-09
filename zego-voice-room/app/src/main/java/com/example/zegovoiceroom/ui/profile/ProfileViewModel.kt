package com.example.zegovoiceroom.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.zegovoiceroom.data.UserSessionManager
import com.example.zegovoiceroom.data.model.UserProfile

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val _profile = MutableLiveData<UserProfile?>()
    val profile: LiveData<UserProfile?> = _profile

    init {
        refresh()
    }

    fun refresh() {
        _profile.value = UserSessionManager.getProfile(getApplication())
    }

    fun updateProfile(displayName: String, appSign: String): String? {
        val current = UserSessionManager.getProfile(getApplication()) ?: return "Please log in again"
        val error = UserSessionManager.validate(current.userId, displayName)
        if (error != null) return error

        val updated = current.copy(displayName = displayName.trim(), appSign = appSign.trim())
        UserSessionManager.saveProfile(getApplication(), updated)
        _profile.value = updated
        return null
    }

    fun logout() {
        UserSessionManager.clear(getApplication())
        _profile.value = null
    }
}
