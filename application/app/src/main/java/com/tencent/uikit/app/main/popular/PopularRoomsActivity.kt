package com.tencent.uikit.app.main.popular

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tencent.uikit.app.R
import com.tencent.uikit.app.main.BaseActivity

class PopularRoomsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_activity_popular_rooms)
        initHeader()
        initRoomList()
    }

    private fun initHeader() {
        findViewById<TextView>(R.id.tv_popular_user_avatar).setOnClickListener { finish() }
    }

    private fun initRoomList() {
        val recyclerView = findViewById<RecyclerView>(R.id.rv_popular_rooms)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = PopularRoomsAdapter(createRooms())
        recyclerView.setHasFixedSize(true)
    }

    private fun createRooms(): List<PopularRoomData> {
        return listOf(
            PopularRoomData(
                initial = "A",
                title = "Asma Love House",
                country = "Bangladesh",
                vipLabel = "VIP Lover",
                awards = "300K",
                subtitle = "Red lips club welcomes everyone...",
                score = "3407",
                supportLabel = "ROCKET",
                isHot = false,
                avatarColors = colors("#FFE47A", "#FFB35C")
            ),
            PopularRoomData(
                initial = "R",
                title = "RAJU",
                country = "Bangladesh",
                vipLabel = "RAJU",
                awards = "500K",
                subtitle = "Give support and enjoy the room",
                score = "4564",
                supportLabel = "BOOST",
                isHot = true,
                avatarColors = colors("#42C6FF", "#3257D8")
            ),
            PopularRoomData(
                initial = "T",
                title = "Only you are mine",
                country = "Bangladesh",
                vipLabel = "Heart Owner",
                awards = "300K",
                subtitle = "Welcome to our friendly chat room",
                score = "1631",
                supportLabel = "ROCKET",
                isHot = true,
                avatarColors = colors("#F8C7D9", "#4E5564")
            ),
            PopularRoomData(
                initial = "B",
                title = "Friends Circle",
                country = "Bangladesh",
                vipLabel = "Gold Star",
                awards = "RANK",
                subtitle = "Join the new tournament event",
                score = "19017",
                supportLabel = "TEAM",
                isHot = false,
                avatarColors = colors("#111724", "#B48CFF")
            ),
            PopularRoomData(
                initial = "G",
                title = "Dream World TOP Club",
                country = "Bangladesh",
                vipLabel = "R.J.Rana.Club",
                awards = "ELITE",
                subtitle = "Songs, stories and friendly games",
                score = "18401",
                supportLabel = "BOOST",
                isHot = true,
                avatarColors = colors("#54D7FF", "#133B86")
            ),
            PopularRoomData(
                initial = "L",
                title = "Rainbow Love Room",
                country = "Bangladesh",
                vipLabel = "SK BONDHU",
                awards = "VIP",
                subtitle = "Come share love and good vibes",
                score = "63741",
                supportLabel = "GIFT",
                isHot = false,
                avatarColors = colors("#B159FF", "#FF8BD5")
            )
        )
    }

    private fun colors(startColor: String, endColor: String): IntArray {
        return intArrayOf(Color.parseColor(startColor), Color.parseColor(endColor))
    }
}
