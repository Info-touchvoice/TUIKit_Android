package com.example.atomicxcore

import org.json.JSONArray

class FakeRoomRepository {

    fun loadRooms(): List<VoiceRoom> {
        val rooms = JSONArray(FAKE_ROOM_JSON)
        return buildList {
            for (index in 0 until rooms.length()) {
                val item = rooms.getJSONObject(index)
                add(
                    VoiceRoom(
                        id = item.getInt("id"),
                        title = item.getString("title"),
                        countryCode = item.getString("countryCode"),
                        vipBadge = item.getString("vipBadge"),
                        welcomeText = item.optString("welcomeText", "Welcome to Hilo!"),
                        popularity = item.getInt("popularity"),
                        pkEnabled = item.getBoolean("pkEnabled"),
                        rankIcons = item.getInt("rankIcons"),
                        avatarInitials = item.getString("avatarInitials"),
                        avatarStartColor = item.getString("avatarStartColor"),
                        avatarEndColor = item.getString("avatarEndColor"),
                        label = item.optString("label")
                    )
                )
            }
        }
    }

    private companion object {
        private const val FAKE_ROOM_JSON = """
            [
              {
                "id": 1,
                "title": "Vibe friends music adda ghar",
                "countryCode": "BD",
                "vipBadge": "SK-CLUB",
                "welcomeText": "Welcome to Hilo!",
                "popularity": 4281,
                "pkEnabled": true,
                "rankIcons": 3,
                "avatarInitials": "VF",
                "avatarStartColor": "#FF18102F",
                "avatarEndColor": "#FFFF7A2F",
                "label": "HOT"
              },
              {
                "id": 2,
                "title": "One life many stories",
                "countryCode": "BD",
                "vipBadge": "SK BONDHU",
                "welcomeText": "Welcome to Hilo!",
                "popularity": 2706,
                "pkEnabled": false,
                "rankIcons": 3,
                "avatarInitials": "OL",
                "avatarStartColor": "#FF324052",
                "avatarEndColor": "#FF9BB4FF",
                "label": "NEW"
              },
              {
                "id": 3,
                "title": "Royal friends lounge",
                "countryCode": "BD",
                "vipBadge": "A P",
                "welcomeText": "Welcome to Hilo!",
                "popularity": 2198,
                "pkEnabled": false,
                "rankIcons": 3,
                "avatarInitials": "RF",
                "avatarStartColor": "#FF5B0B1D",
                "avatarEndColor": "#FFFF365E",
                "label": ""
              },
              {
                "id": 4,
                "title": "Long distance melody",
                "countryCode": "BD",
                "vipBadge": "Orpa",
                "welcomeText": "Good vibes, songs, and friendly chat",
                "popularity": 1355,
                "pkEnabled": true,
                "rankIcons": 2,
                "avatarInitials": "LD",
                "avatarStartColor": "#FFFFB6D5",
                "avatarEndColor": "#FF7B2CFF",
                "label": ""
              },
              {
                "id": 5,
                "title": "My star room",
                "countryCode": "BD",
                "vipBadge": "Mr Shohel",
                "welcomeText": "Welcome to Hilo!",
                "popularity": 840,
                "pkEnabled": true,
                "rankIcons": 3,
                "avatarInitials": "MS",
                "avatarStartColor": "#FFFFE1EC",
                "avatarEndColor": "#FFFF4FA3",
                "label": "HOT"
              },
              {
                "id": 6,
                "title": "12 plus not allow",
                "countryCode": "BD",
                "vipBadge": "3R",
                "welcomeText": "Sign in and meet the room family",
                "popularity": 530,
                "pkEnabled": false,
                "rankIcons": 1,
                "avatarInitials": "12",
                "avatarStartColor": "#FF111827",
                "avatarEndColor": "#FF9CA3AF",
                "label": ""
              }
            ]
        """
    }
}
