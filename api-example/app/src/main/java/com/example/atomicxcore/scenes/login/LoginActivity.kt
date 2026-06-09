package com.example.atomicxcore.scenes.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.atomicxcore.HomeActivity
import com.example.atomicxcore.R
import com.example.atomicxcore.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActions()
    }

    private fun setupActions() {
        binding.btnLogin.setOnClickListener {
            dismissKeyboard()
            val nickname = binding.etNickname.text?.toString()?.trim().orEmpty()
            if (nickname.isBlank()) {
                Toast.makeText(this, R.string.voice_login_error, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            enterHome()
        }

        binding.btnGuest.setOnClickListener {
            dismissKeyboard()
            enterHome()
        }

        binding.root.setOnClickListener {
            dismissKeyboard()
        }
    }

    private fun enterHome() {
        binding.progressBar.visibility = View.VISIBLE
        binding.root.postDelayed({
            binding.progressBar.visibility = View.GONE
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }, LOGIN_DELAY_MS)
    }

    private fun dismissKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        currentFocus?.let { imm.hideSoftInputFromWindow(it.windowToken, 0) }
    }

    private companion object {
        const val LOGIN_DELAY_MS = 250L
    }
}
