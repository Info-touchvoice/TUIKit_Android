package com.example.zegovoiceroom.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.zegovoiceroom.R
import com.example.zegovoiceroom.data.VoiceRoom
import com.example.zegovoiceroom.databinding.ActivityMainBinding
import com.example.zegovoiceroom.ui.home.HomeFragment
import com.example.zegovoiceroom.ui.profile.ProfileFragment
import com.example.zegovoiceroom.ui.rooms.RoomsFragment
import com.example.zegovoiceroom.ui.voice.VoiceRoomActivity

class MainActivity : AppCompatActivity(), RoomNavigator {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> showFragment(HomeFragment())
                R.id.navigation_rooms -> showFragment(RoomsFragment())
                R.id.navigation_profile -> showFragment(ProfileFragment())
                else -> false
            }
        }

        if (savedInstanceState == null) {
            binding.bottomNavigation.selectedItemId = R.id.navigation_home
        }
    }

    override fun openRoom(room: VoiceRoom, asHost: Boolean) {
        startActivity(VoiceRoomActivity.intent(this, room.id, asHost))
    }

    private fun showFragment(fragment: Fragment): Boolean {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
        return true
    }
}

interface RoomNavigator {
    fun openRoom(room: VoiceRoom, asHost: Boolean = false)
}
