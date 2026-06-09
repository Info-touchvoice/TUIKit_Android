package com.tencent.uikit.app.zego

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tencent.uikit.app.BuildConfig
import com.tencent.uikit.app.login.LoginActivity

class HomeActivity : AppCompatActivity() {
    private lateinit var userIdInput: EditText
    private lateinit var userNameInput: EditText
    private lateinit var roomIdInput: EditText
    private lateinit var roomNameInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(createContentView())
    }

    private fun createContentView(): View {
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(24), dp(36), dp(24), dp(24))
        }

        content.addView(title("ZEGO Live Audio Room"))
        content.addView(
            description(
                "Create or join a ZEGO UIKit prebuilt voice room. Room, user ID, and user name may only use letters, numbers, and underscores."
            )
        )
        if (BuildConfig.ZEGO_APP_SIGN.isBlank()) {
            content.addView(
                warning(
                    "ZEGO AppSign is not configured. Add -PzegoAppSign=<your AppSign> when building to connect to ZEGO services."
                )
            )
        }

        userIdInput = input("User ID", VoiceRoomStore.getUserId(this))
        userNameInput = input("User name", VoiceRoomStore.getUserName(this))
        roomIdInput = input("Room ID", "room_${System.currentTimeMillis() % 100000}")
        roomNameInput = input("Room name", "Live Audio Room")

        content.addView(label("User"))
        content.addView(userIdInput)
        content.addView(userNameInput)
        content.addView(label("Room"))
        content.addView(roomIdInput)
        content.addView(roomNameInput)

        content.addView(primaryButton("Create room") {
            startVoiceRoom(isHost = true)
        })
        content.addView(primaryButton("Join room") {
            startVoiceRoom(isHost = false)
        })
        content.addView(secondaryButton("View saved rooms") {
            saveUserProfile()
            startActivity(Intent(this, RoomListActivity::class.java))
        })
        content.addView(secondaryButton("Open existing TUIKit demo") {
            startActivity(Intent(this, LoginActivity::class.java))
        })

        return ScrollView(this).apply {
            setBackgroundColor(Color.WHITE)
            addView(
                content,
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            )
        }
    }

    private fun startVoiceRoom(isHost: Boolean) {
        val userId = userIdInput.text.toString().trim()
        val userName = userNameInput.text.toString().trim()
        val roomId = roomIdInput.text.toString().trim()
        val roomName = roomNameInput.text.toString().trim().ifBlank { roomId }

        if (!validateZegoId(userId, "User ID")) {
            return
        }
        if (!validateZegoId(userName, "User name")) {
            return
        }
        if (!validateZegoId(roomId, "Room ID")) {
            return
        }

        VoiceRoomStore.saveUser(this, userId, userName)
        VoiceRoomStore.saveRoom(
            this,
            VoiceRoom(roomId = roomId, roomName = roomName, createdAt = System.currentTimeMillis())
        )
        startActivity(VoiceRoomActivity.createIntent(this, roomId, roomName, isHost, userId, userName))
    }

    private fun saveUserProfile() {
        val userId = userIdInput.text.toString().trim()
        val userName = userNameInput.text.toString().trim()
        if (VoiceRoomStore.isValidZegoId(userId) && VoiceRoomStore.isValidZegoId(userName)) {
            VoiceRoomStore.saveUser(this, userId, userName)
        }
    }

    private fun validateZegoId(value: String, label: String): Boolean {
        if (VoiceRoomStore.isValidZegoId(value)) {
            return true
        }
        Toast.makeText(this, "$label may only use 1-64 letters, numbers, or underscores.", Toast.LENGTH_SHORT).show()
        return false
    }

    private fun title(text: String) = TextView(this).apply {
        this.text = text
        textSize = 28f
        setTextColor(Color.rgb(23, 23, 23))
        setPadding(0, 0, 0, dp(12))
    }

    private fun description(text: String) = TextView(this).apply {
        this.text = text
        textSize = 15f
        setTextColor(Color.rgb(82, 82, 82))
        setPadding(0, 0, 0, dp(20))
    }

    private fun warning(text: String) = TextView(this).apply {
        this.text = text
        textSize = 14f
        setTextColor(Color.rgb(185, 28, 28))
        setPadding(0, 0, 0, dp(20))
    }

    private fun label(text: String) = TextView(this).apply {
        this.text = text
        textSize = 16f
        setTextColor(Color.rgb(64, 64, 64))
        setPadding(0, dp(16), 0, dp(8))
    }

    private fun input(hintText: String, defaultValue: String) = EditText(this).apply {
        hint = hintText
        setText(defaultValue)
        singleLine = true
        setPadding(dp(12), dp(8), dp(12), dp(8))
        layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            bottomMargin = dp(8)
        }
    }

    private fun primaryButton(text: String, onClick: () -> Unit) = button(text, onClick).apply {
        setTextColor(Color.WHITE)
        setBackgroundColor(Color.rgb(0, 110, 255))
    }

    private fun secondaryButton(text: String, onClick: () -> Unit) = button(text, onClick).apply {
        setTextColor(Color.rgb(0, 110, 255))
        setBackgroundColor(Color.rgb(239, 246, 255))
    }

    private fun button(text: String, onClick: () -> Unit) = Button(this).apply {
        this.text = text
        gravity = Gravity.CENTER
        setAllCaps(false)
        setOnClickListener { onClick() }
        layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            topMargin = dp(12)
        }
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
