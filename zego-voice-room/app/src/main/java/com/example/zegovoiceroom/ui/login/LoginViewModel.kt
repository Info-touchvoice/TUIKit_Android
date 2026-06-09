package com.example.zegovoiceroom.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {
    private val _state = MutableLiveData(LoginState())
    val state: LiveData<LoginState> = _state

    fun updateUserId(userId: String) {
        _state.value = _state.value?.copy(userId = userId, error = null)
    }

    fun validate(): Boolean {
        val userId = _state.value?.userId.orEmpty().trim()
        if (userId.isBlank()) {
            _state.value = _state.value?.copy(error = "Enter a User ID to continue")
            return false
        }
        _state.value = LoginState(userId = userId)
        return true
    }
}

data class LoginState(
    val userId: String = "",
    val error: String? = null
)
