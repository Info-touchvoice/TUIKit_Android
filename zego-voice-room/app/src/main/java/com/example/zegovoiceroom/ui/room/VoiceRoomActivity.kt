package com.example.zegovoiceroom.ui.room

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.zegovoiceroom.R
import com.example.zegovoiceroom.data.model.VoiceRoom
import com.example.zegovoiceroom.databinding.ActivityVoiceRoomBinding
import com.zegocloud.uikit.prebuilt.liveaudioroom.ZegoUIKitPrebuiltLiveAudioRoomConfig
import com.zegocloud.uikit.prebuilt.liveaudioroom.ZegoUIKitPrebuiltLiveAudioRoomFragment

class VoiceRoomActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVoiceRoomBinding
    private val viewModel: VoiceRoomViewModel by viewModels()
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { grants ->
            if (grants.values.all { it }) {
                attachZegoRoom()
            } else {
                Toast.makeText(this, "Microphone permission is required for voice rooms", Toast.LENGTH_LONG).show()
                finish()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVoiceRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (savedInstanceState == null) {
            ensurePermissionsThenOpenRoom()
        }
    }

    private fun ensurePermissionsThenOpenRoom() {
        val missingPermissions = requiredPermissions().filter { permission ->
            ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED
        }
        if (missingPermissions.isEmpty()) {
            attachZegoRoom()
        } else {
            permissionLauncher.launch(missingPermissions.toTypedArray())
        }
    }

    private fun attachZegoRoom() {
        val profile = viewModel.currentProfile()
        val roomId = intent.getStringExtra(EXTRA_ROOM_ID).orEmpty()
        if (profile == null || roomId.isBlank()) {
            Toast.makeText(this, "Missing room or user information", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        val config = if (intent.getBooleanExtra(EXTRA_IS_HOST, false)) {
            ZegoUIKitPrebuiltLiveAudioRoomConfig.host()
        } else {
            ZegoUIKitPrebuiltLiveAudioRoomConfig.audience()
        }

        val fragment = ZegoUIKitPrebuiltLiveAudioRoomFragment.newInstance(
            viewModel.appId(),
            viewModel.appSign(profile),
            profile.userId,
            profile.displayName,
            roomId,
            config
        )

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commitNow()
    }

    private fun requiredPermissions(): List<String> {
        return buildList {
            add(Manifest.permission.RECORD_AUDIO)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                add(Manifest.permission.BLUETOOTH_CONNECT)
            }
        }
    }

    companion object {
        private const val EXTRA_ROOM_ID = "room_id"
        private const val EXTRA_ROOM_TITLE = "room_title"
        private const val EXTRA_IS_HOST = "is_host"

        fun createIntent(context: Context, room: VoiceRoom, isHost: Boolean): Intent {
            return Intent(context, VoiceRoomActivity::class.java)
                .putExtra(EXTRA_ROOM_ID, room.roomId)
                .putExtra(EXTRA_ROOM_TITLE, room.title)
                .putExtra(EXTRA_IS_HOST, isHost)
        }
    }
}
