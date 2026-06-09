package com.example.zegovoiceroom.ui.profile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.zegovoiceroom.R
import com.example.zegovoiceroom.databinding.ActivityUserProfileBinding

class UserProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.profileFragmentContainer, ProfileFragment.newInstance(showBackButton = true))
                .commit()
        }
    }
}
