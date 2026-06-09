package com.example.zegovoiceroom.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.example.zegovoiceroom.data.UserSession
import com.example.zegovoiceroom.databinding.ActivityLoginBinding
import com.example.zegovoiceroom.ui.main.MainActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val session = UserSession(this)
        if (session.isLoggedIn) {
            openHome()
            return
        }

        binding.userIdInput.doAfterTextChanged { viewModel.updateUserId(it?.toString().orEmpty()) }
        binding.loginButton.setOnClickListener {
            if (viewModel.validate()) {
                session.userId = viewModel.state.value?.userId.orEmpty()
                openHome()
            }
        }

        viewModel.state.observe(this) { state ->
            binding.userIdLayout.error = state.error
        }
    }

    private fun openHome() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
