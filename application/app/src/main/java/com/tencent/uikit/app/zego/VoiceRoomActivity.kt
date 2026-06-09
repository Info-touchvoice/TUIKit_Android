package com.tencent.uikit.app.zego

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tencent.uikit.app.BuildConfig
import com.tencent.uikit.app.R
import com.zegocloud.uikit.prebuilt.liveaudioroom.ZegoUIKitPrebuiltLiveAudioRoomConfig
import com.zegocloud.uikit.prebuilt.liveaudioroom.ZegoUIKitPrebuiltLiveAudioRoomFragment

class VoiceRoomActivity : AppCompatActivity() {
    private var fragmentAdded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_activity_zego_voice_room)
        if (savedInstanceState == null) {
            requestMicrophoneThenJoin()
        }
    }

    private fun requestMicrophoneThenJoin() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_RECORD_AUDIO)
            return
        }
        addLiveAudioRoomFragment()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != REQUEST_RECORD_AUDIO) {
            return
        }
        if (grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
            addLiveAudioRoomFragment()
        } else {
            Toast.makeText(this, "Microphone permission is required to speak in the room.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun addLiveAudioRoomFragment() {
        if (fragmentAdded) {
            return
        }
        fragmentAdded = true

        if (BuildConfig.ZEGO_APP_SIGN.isBlank()) {
            Toast.makeText(this, "ZEGO AppSign is empty. Configure -PzegoAppSign to connect.", Toast.LENGTH_LONG).show()
        }

        val roomId = requireExtra(EXTRA_ROOM_ID)
        val userId = requireExtra(EXTRA_USER_ID)
        val userName = requireExtra(EXTRA_USER_NAME)
        val isHost = intent.getBooleanExtra(EXTRA_IS_HOST, false)
        val config = if (isHost) {
            ZegoUIKitPrebuiltLiveAudioRoomConfig.host()
        } else {
            ZegoUIKitPrebuiltLiveAudioRoomConfig.audience()
        }

        val fragment = ZegoUIKitPrebuiltLiveAudioRoomFragment.newInstance(
            BuildConfig.ZEGO_APP_ID,
            BuildConfig.ZEGO_APP_SIGN,
            userId,
            userName,
            roomId,
            config
        )
        supportFragmentManager.beginTransaction()
            .replace(R.id.zego_room_fragment_container, fragment)
            .commitNow()
    }

    private fun requireExtra(key: String): String {
        val value = intent.getStringExtra(key)
        if (value.isNullOrBlank()) {
            throw IllegalArgumentException("Missing required extra: $key")
        }
        return value
    }

    companion object {
        private const val REQUEST_RECORD_AUDIO = 1536
        private const val EXTRA_ROOM_ID = "extra_room_id"
        private const val EXTRA_ROOM_NAME = "extra_room_name"
        private const val EXTRA_IS_HOST = "extra_is_host"
        private const val EXTRA_USER_ID = "extra_user_id"
        private const val EXTRA_USER_NAME = "extra_user_name"

        fun createIntent(
            context: Context,
            roomId: String,
            roomName: String,
            isHost: Boolean,
            userId: String,
            userName: String
        ): Intent {
            return Intent(context, VoiceRoomActivity::class.java)
                .putExtra(EXTRA_ROOM_ID, roomId)
                .putExtra(EXTRA_ROOM_NAME, roomName)
                .putExtra(EXTRA_IS_HOST, isHost)
                .putExtra(EXTRA_USER_ID, userId)
                .putExtra(EXTRA_USER_NAME, userName)
        }
    }
}
