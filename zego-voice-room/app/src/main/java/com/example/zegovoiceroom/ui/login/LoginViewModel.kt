package com.example.zegovoiceroom.ui.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.zegovoiceroom.data.UserSessionManager
import com.example.zegovoiceroom.data.model.UserProfile

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val _loginState = MutableLiveData<UserProfile?>()
    val loginState: LiveData<UserProfile?> = _loginState

    fun currentProfile(): UserProfile? = UserSessionManager.getProfile(getApplication())

    fun login(userId: String, displayName: String, appSign: String): String? {
        val sanitizedUserId = UserSessionManager.sanitizeIdentifier(userId)
        val error = UserSessionManager.validate(sanitizedUserId, displayName)
        if (error != null) return error

        val profile = UserProfile(
            userId = sanitizedUserId,
            displayName = displayName.trim(),
            appSign = appSign.trim()
        )
        UserSessionManager.saveProfile(getApplication(), profile)
        _loginState.value = profile
        return null
    }
}
