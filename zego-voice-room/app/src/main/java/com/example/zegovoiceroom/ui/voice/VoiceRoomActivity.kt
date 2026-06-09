package com.example.zegovoiceroom.ui.voice

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zegovoiceroom.R
import com.example.zegovoiceroom.data.UserSession
import com.example.zegovoiceroom.databinding.ActivityVoiceRoomBinding
import com.example.zegovoiceroom.zego.ZegoConfig
import com.example.zegovoiceroom.zego.ZegoExpressManager
import com.zegocloud.uikit.prebuilt.liveaudioroom.ZegoUIKitPrebuiltLiveAudioRoomConfig
import com.zegocloud.uikit.prebuilt.liveaudioroom.ZegoUIKitPrebuiltLiveAudioRoomFragment

class VoiceRoomActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVoiceRoomBinding
    private val viewModel: VoiceRoomViewModel by viewModels()
    private val seatAdapter = SeatAdapter()
    private val chatAdapter = ChatAdapter()
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.RECORD_AUDIO] == true) {
            addZegoRoomFragmentIfReady()
        } else {
            Toast.makeText(this, R.string.microphone_permission_required, Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVoiceRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val roomId = intent.getStringExtra(EXTRA_ROOM_ID).orEmpty()
        val isHost = intent.getBooleanExtra(EXTRA_IS_HOST, false)
        viewModel.load(roomId)

        setupLists()
        setupActions()
        observeState()
        requestAudioPermissionThenStartZego()

        binding.roleBadge.text = if (isHost) getString(R.string.host_role) else getString(R.string.audience_role)
    }

    private fun setupLists() = with(binding) {
        seatList.layoutManager = GridLayoutManager(this@VoiceRoomActivity, 3)
        seatList.adapter = seatAdapter
        chatList.layoutManager = LinearLayoutManager(this@VoiceRoomActivity)
        chatList.adapter = chatAdapter
    }

    private fun setupActions() = with(binding) {
        backButton.setOnClickListener { finish() }
        muteButton.setOnClickListener { viewModel.toggleMute() }
        giftButton.setOnClickListener { viewModel.sendGift(UserSession(this@VoiceRoomActivity).userId) }
        sendButton.setOnClickListener {
            viewModel.sendMessage(UserSession(this@VoiceRoomActivity).userId, chatInput.text?.toString().orEmpty())
            chatInput.text?.clear()
        }
        pkButton.setOnClickListener {
            Toast.makeText(this@VoiceRoomActivity, R.string.pk_support_message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeState() {
        viewModel.room.observe(this) { room ->
            binding.roomTitle.text = room.name
            binding.roomSubtitle.text = getString(R.string.room_host_label, room.hostName)
            binding.pkPanel.visibility = if (room.isPkEnabled) View.VISIBLE else View.GONE
        }
        viewModel.seats.observe(this, seatAdapter::submitList)
        viewModel.messages.observe(this) { messages ->
            chatAdapter.submitList(messages)
            if (messages.isNotEmpty()) binding.chatList.scrollToPosition(messages.lastIndex)
        }
        viewModel.isMuted.observe(this) { isMuted ->
            binding.muteButton.setIconResource(if (isMuted) R.drawable.ic_mic_off else R.drawable.ic_mic)
            binding.muteButton.text = getString(if (isMuted) R.string.unmute else R.string.mute)
        }
        viewModel.pkScore.observe(this) { binding.pkScore.text = it }
    }

    private fun requestAudioPermissionThenStartZego() {
        val missingPermissions = requiredPermissions().filter { permission ->
            ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED
        }
        if (missingPermissions.isEmpty()) {
            addZegoRoomFragmentIfReady()
        } else {
            permissionLauncher.launch(missingPermissions.toTypedArray())
        }
    }

    private fun requiredPermissions(): List<String> {
        return buildList {
            add(Manifest.permission.RECORD_AUDIO)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                add(Manifest.permission.BLUETOOTH_CONNECT)
            }
        }
    }

    private fun addZegoRoomFragmentIfReady() {
        val zegoReady = ZegoExpressManager().prepare(application)
        binding.zegoUnavailableNotice.visibility = if (zegoReady) View.GONE else View.VISIBLE
        if (!ZegoConfig.hasCredentials || supportFragmentManager.findFragmentByTag(ZEGO_FRAGMENT_TAG) != null) {
            return
        }

        val roomId = intent.getStringExtra(EXTRA_ROOM_ID).orEmpty()
        val userId = UserSession(this).userId.ifBlank { "guest_user" }
        val config = if (intent.getBooleanExtra(EXTRA_IS_HOST, false)) {
            ZegoUIKitPrebuiltLiveAudioRoomConfig.host()
        } else {
            ZegoUIKitPrebuiltLiveAudioRoomConfig.audience()
        }

        val fragment = ZegoUIKitPrebuiltLiveAudioRoomFragment.newInstance(
            ZegoConfig.APP_ID,
            ZegoConfig.APP_SIGN,
            userId,
            userId,
            roomId,
            config
        )
        supportFragmentManager.beginTransaction()
            .replace(R.id.zegoFragmentContainer, fragment, ZEGO_FRAGMENT_TAG)
            .commitNowAllowingStateLoss()
    }

    companion object {
        private const val EXTRA_ROOM_ID = "extra_room_id"
        private const val EXTRA_IS_HOST = "extra_is_host"
        private const val ZEGO_FRAGMENT_TAG = "zego_live_audio_room"

        fun intent(context: Context, roomId: String, asHost: Boolean): Intent {
            return Intent(context, VoiceRoomActivity::class.java)
                .putExtra(EXTRA_ROOM_ID, roomId)
                .putExtra(EXTRA_IS_HOST, asHost)
        }
    }
}
