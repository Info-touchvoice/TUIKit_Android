package com.tencent.uikit.app.main

import com.tencent.uikit.app.R
import com.tencent.uikit.app.main.call.GroupCallActivity
import com.tencent.uikit.app.main.live.LiveActivity
import com.tencent.uikit.app.main.popular.PopularRoomsActivity
import com.trtc.uikit.roomkit.RoomHomeActivity

class TRTCMainData {
    val itemDataList = ArrayList<MainItemData?>()

    init {
        itemDataList.add(
            MainItemData(
                MainTypeEnum.TYPE_ITEM_CALL, R.drawable.app_ic_main_call, R.string.app_main_item_call,
                R.string.app_main_item_call_sub, GroupCallActivity::class.java, MainItemData.Category.KIT
            )
        )
        itemDataList.add(
            MainItemData(
                MainTypeEnum.TYPE_ITEM_LIVE, R.drawable.app_ic_main_live, R.string.app_main_item_live,
                R.string.app_main_item_live_sub, LiveActivity::class.java, MainItemData.Category.KIT
            )
        )
        itemDataList.add(
            MainItemData(
                MainTypeEnum.TYPE_ITEM_MEETING, R.drawable.app_ic_main_meeting, R.string.app_main_item_meeting,
                R.string.app_main_item_meeting_sub, RoomHomeActivity::class.java, MainItemData.Category.KIT
            )
        )
        itemDataList.add(
            MainItemData(
                MainTypeEnum.TYPE_ITEM_POPULAR_ROOMS, R.drawable.app_ic_main_popular,
                R.string.app_main_item_popular_rooms, R.string.app_main_item_popular_rooms_sub,
                PopularRoomsActivity::class.java, MainItemData.Category.HOT
            )
        )
    }
}