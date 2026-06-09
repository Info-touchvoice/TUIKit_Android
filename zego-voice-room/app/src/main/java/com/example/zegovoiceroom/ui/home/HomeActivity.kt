package com.example.zegovoiceroom.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.zegovoiceroom.R
import com.example.zegovoiceroom.data.UserSessionManager
import com.example.zegovoiceroom.databinding.ActivityHomeBinding
import com.example.zegovoiceroom.ui.login.LoginActivity
import com.example.zegovoiceroom.ui.profile.ProfileFragment

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (UserSessionManager.getProfile(this) == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> showFragment(HomeFragment())
                R.id.navigation_rooms -> showFragment(VoiceRoomListFragment())
                R.id.navigation_profile -> showFragment(ProfileFragment.newInstance(showBackButton = false))
                else -> false
            }
        }

        if (savedInstanceState == null) {
            binding.bottomNavigation.selectedItemId = R.id.navigation_home
        }
    }

    private fun showFragment(fragment: Fragment): Boolean {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
        return true
    }
}
