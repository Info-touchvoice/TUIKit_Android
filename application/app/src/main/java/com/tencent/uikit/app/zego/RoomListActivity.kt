package com.tencent.uikit.app.zego

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
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

class RoomListActivity : AppCompatActivity() {
    private lateinit var userIdInput: EditText
    private lateinit var userNameInput: EditText
    private lateinit var roomList: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(createContentView())
    }

    override fun onResume() {
        super.onResume()
        renderRooms()
    }

    private fun createContentView(): View {
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(24), dp(36), dp(24), dp(24))
        }

        content.addView(title("Saved rooms"))
        content.addView(
            description(
                "Rooms shown here are saved on this device when you create or join them. Enter the same room ID on another device to join that room."
            )
        )
        userIdInput = input("User ID", VoiceRoomStore.getUserId(this))
        userNameInput = input("User name", VoiceRoomStore.getUserName(this))
        content.addView(label("Join as"))
        content.addView(userIdInput)
        content.addView(userNameInput)

        roomList = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(16)
            }
        }
        content.addView(roomList)

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

    private fun renderRooms() {
        roomList.removeAllViews()
        val rooms = VoiceRoomStore.getRooms(this)
        if (rooms.isEmpty()) {
            roomList.addView(
                description("No saved rooms yet. Create or join a room from the home screen first.")
            )
            return
        }

        rooms.forEach { room ->
            roomList.addView(roomCard(room))
        }
    }

    private fun roomCard(room: VoiceRoom): View {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(14), dp(16), dp(16))
            background = GradientDrawable().apply {
                setColor(Color.rgb(248, 250, 252))
                cornerRadius = dp(12).toFloat()
                setStroke(dp(1), Color.rgb(226, 232, 240))
            }
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp(12)
            }
        }

        card.addView(TextView(this).apply {
            text = room.roomName
            textSize = 18f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.rgb(23, 23, 23))
        })
        card.addView(TextView(this).apply {
            text = "Room ID: ${room.roomId}"
            textSize = 14f
            setTextColor(Color.rgb(82, 82, 82))
            setPadding(0, dp(4), 0, dp(12))
        })

        val actions = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.END
        }
        actions.addView(actionButton("Join") {
            startVoiceRoom(room, isHost = false)
        })
        actions.addView(actionButton("Host") {
            startVoiceRoom(room, isHost = true)
        })
        card.addView(actions)
        return card
    }

    private fun startVoiceRoom(room: VoiceRoom, isHost: Boolean) {
        val userId = userIdInput.text.toString().trim()
        val userName = userNameInput.text.toString().trim()
        if (!VoiceRoomStore.isValidZegoId(userId)) {
            Toast.makeText(this, "User ID may only use letters, numbers, or underscores.", Toast.LENGTH_SHORT).show()
            return
        }
        if (!VoiceRoomStore.isValidZegoId(userName)) {
            Toast.makeText(this, "User name may only use letters, numbers, or underscores.", Toast.LENGTH_SHORT).show()
            return
        }

        VoiceRoomStore.saveUser(this, userId, userName)
        startActivity(VoiceRoomActivity.createIntent(this, room.roomId, room.roomName, isHost, userId, userName))
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

    private fun label(text: String) = TextView(this).apply {
        this.text = text
        textSize = 16f
        setTextColor(Color.rgb(64, 64, 64))
        setPadding(0, dp(16), 0, dp(8))
    }

    private fun input(hintText: String, defaultValue: String) = EditText(this).apply {
        hint = hintText
        setText(defaultValue)
        setSingleLine(true)
        setPadding(dp(12), dp(8), dp(12), dp(8))
        layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            bottomMargin = dp(8)
        }
    }

    private fun actionButton(text: String, onClick: () -> Unit) = Button(this).apply {
        this.text = text
        setAllCaps(false)
        setTextColor(Color.WHITE)
        setBackgroundColor(Color.rgb(0, 110, 255))
        setOnClickListener { onClick() }
        layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f).apply {
            marginStart = dp(8)
        }
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
