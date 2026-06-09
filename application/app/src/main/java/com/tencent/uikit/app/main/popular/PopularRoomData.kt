package com.tencent.uikit.app.main.popular

data class PopularRoomData(
    val initial: String,
    val title: String,
    val country: String,
    val vipLabel: String,
    val awards: String,
    val subtitle: String,
    val score: String,
    val supportLabel: String,
    val isHot: Boolean,
    val avatarColors: IntArray
)
