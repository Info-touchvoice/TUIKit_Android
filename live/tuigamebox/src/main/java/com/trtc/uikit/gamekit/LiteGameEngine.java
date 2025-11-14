package com.trtc.uikit.gamekit;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tencent.cloud.tuikit.engine.common.TUICommonDefine;
import com.tencent.cloud.tuikit.engine.room.TUIRoomDefine;
import com.tencent.cloud.tuikit.engine.room.TUIRoomEngine;
import com.trtc.uikit.gamekit.common.GameKitLogger;
import com.trtc.uikit.gamekit.gameview.GameView;

import org.json.JSONObject;

public final class LiteGameEngine {
    private static final GameKitLogger LOGGER = GameKitLogger.getGameKitLogger(LiteGameEngine.class.getName());
    public static class GameID {
        public static String NONE = "";
        public static String GOBANG = "Gobang";
        public static String FLYINGCHESS = "FlyingChess";
    }

    public static class ActionType {
        public static String ACTION_GAME_START = "ACTION_GAME_START";
        public static String ACTION_GAME_STOP = "ACTION_GAME_STOP";
        public static String ACTION_USER_ENTER = "ACTION_USER_ENTER";
        public static String ACTION_USER_EXIT = "ACTION_USER_EXIT";
    }

    public static final String customMessageBusinessId = "LiteavGameKit";

    private String runningGameId = "";
    private static volatile LiteGameEngine sInstance;
    private LiteGameEngine() {
    }

    public static LiteGameEngine getInstance() {
        if (sInstance == null) {
            synchronized (LiteGameEngine.class) {
                if (sInstance == null) {
                    sInstance = new LiteGameEngine();
                }
            }
        }
        return sInstance;
    }

    public static void destroyInstance() {
        synchronized (LiteGameEngine.class) {
            if (sInstance != null) {
                sInstance = null;
            }
        }
    }
    public String getRunningGameId() {
        return runningGameId;
    }
    public void startGame(String gameId, boolean isHost, String userId, String nickName, String avatarUrl) {
        if (runningGameId.equals(gameId))
        {
            return;
        }
        runningGameId = gameId;
        try {

            JSONObject actionDataObj = new JSONObject();
            actionDataObj.put("IsHost", isHost);
            actionDataObj.put("PlayerId", userId);
            actionDataObj.put("Nickname", nickName);
            actionDataObj.put("AvatarUrl", avatarUrl);

            JSONObject messageObj = new JSONObject();
            messageObj.put("gameId", gameId);
            messageObj.put("actionType", ActionType.ACTION_GAME_START);
            messageObj.put("actionData", actionDataObj.toString());
            if (gameId.equals(LiteGameEngine.GameID.GOBANG)) {
                LiteGameUnityPlayer.UnitySendMessage("GameManager", "StartGobangGame", messageObj.toString());
            } else if (gameId.equals(LiteGameEngine.GameID.FLYINGCHESS)) {
                LiteGameUnityPlayer.UnitySendMessage("GameManager", "StartFlyingChessGame", messageObj.toString());
            }

        } catch (Exception e) {
            LOGGER.warn( "StartGameInternal fail");
        }
    }

    public void startLauncher() {
        runningGameId = "";
        LiteGameUnityPlayer.UnitySendMessage("GameManager", "StartLauncher", "");
    }

    public void sendMessageToUnity(String message) {
        LiteGameUnityPlayer.UnitySendMessage("GameManager", "OnReceivedMessage", message);
    }

    public void broadcastGameQuitMessage()
    {
        try {
            JSONObject message = new JSONObject();
            message.put("gameId", runningGameId);
            message.put("actionType", ActionType.ACTION_GAME_STOP);
            message.put("actionData", "{}");
            broadcastMessage(message.toString());
        } catch (Exception e) {
            LOGGER.warn( "Send game stop message fail");
        }
    }

    public void broadcastMessage(String message) {
        TUIRoomDefine.RoomCustomMessage customMessage = new TUIRoomDefine.RoomCustomMessage();
        customMessage.businessId = LiteGameEngine.customMessageBusinessId;
        customMessage.data = message;
        TUIRoomEngine.sharedInstance().sendCustomMessage(customMessage, new TUIRoomDefine.SendCustomMessageCallback() {
            @Override
            public void onSuccess(TUIRoomDefine.RoomCustomMessage message) {
                //Log.i(TAG, "SendMessage to room success");
            }

            @Override
            public void onError(TUICommonDefine.Error error, String message) {
                LOGGER.warn( "SendMessage to room error:" + error + " message:" + message);
            }
        });
    }

    public void onRemoteUserEnterRoom(String userId, String userName, String avatarUrl) {
        try {
            JSONObject actionDataObj = new JSONObject();
            actionDataObj.put("PlayerId", userId);
            actionDataObj.put("Nickname", userName);
            actionDataObj.put("AvatarUrl", avatarUrl);

            JSONObject outer = new JSONObject();
            outer.put("gameId", runningGameId);
            outer.put("actionType", ActionType.ACTION_USER_ENTER);
            outer.put("actionData", actionDataObj.toString());
            sendMessageToUnity(outer.toString());
        } catch (Exception e) {
            LOGGER.warn( "onRemoteUserEnterRoom message send fail");
        }
    }

    public void onRemoteUserLeaveRoom(String userId, String userName, String avatarUrl) {
        try {
            JSONObject actionDataObj = new JSONObject();
            actionDataObj.put("PlayerId", userId);
            actionDataObj.put("Nickname", userName);
            actionDataObj.put("AvatarUrl", avatarUrl);

            // 外层对象，并把内层对象作为字符串放入 actionData
            JSONObject outer = new JSONObject();
            outer.put("gameId", runningGameId);
            outer.put("actionType", ActionType.ACTION_USER_EXIT);
            outer.put("actionData", actionDataObj.toString());
            sendMessageToUnity(outer.toString());
        } catch (Exception e) {
            LOGGER.warn("onRemoteUserLeaveRoom message send fail");
        }
    }
}

