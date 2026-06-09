package com.example.zegovoiceroom.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.zegovoiceroom.databinding.ActivityLoginBinding
import com.example.zegovoiceroom.ui.home.HomeActivity
import com.google.android.material.snackbar.Snackbar

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (viewModel.currentProfile() != null) {
            openHome()
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.userIdInput.setText("user_${System.currentTimeMillis().toString().takeLast(6)}")
        binding.loginButton.setOnClickListener { submitLogin() }
        viewModel.loginState.observe(this) { profile ->
            if (profile != null) openHome()
        }
    }

    private fun submitLogin() {
        binding.userIdLayout.error = null
        binding.userNameLayout.error = null

        val error = viewModel.login(
            userId = binding.userIdInput.text?.toString().orEmpty(),
            displayName = binding.userNameInput.text?.toString().orEmpty(),
            appSign = binding.appSignInput.text?.toString().orEmpty()
        )
        if (error != null) {
            Snackbar.make(binding.root, error, Snackbar.LENGTH_LONG).show()
        }
    }

    private fun openHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}
