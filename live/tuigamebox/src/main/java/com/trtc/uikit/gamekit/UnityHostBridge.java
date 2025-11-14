package com.trtc.uikit.gamekit;

import android.app.Activity;
import android.util.Log;

import com.tencent.cloud.tuikit.engine.common.TUICommonDefine;
import com.tencent.cloud.tuikit.engine.room.TUIRoomDefine;
import com.tencent.cloud.tuikit.engine.room.TUIRoomEngine;

public class UnityHostBridge {
    private static final String TAG = UnityHostBridge.class.getName();
    private static Activity sHost;

    public static void setHostActivity(Activity a) {
        sHost = a;
    }
    public static void clearHostActivity() {
        sHost = null;
    }

    public static void finishHostActivity() {
        if (sHost == null) {
            Log.w(TAG, "finishHost: host is null");
            return;
        }
        final Activity a = sHost;
        a.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    a.finish();
                } catch (Throwable t) {
                    Log.e(TAG, "finishHost failed", t);
                }
            }
        });
    }

    public static void onUnityPlayerRednerFirstFrame() {
        //
        Log.w(TAG, "onUnityPlayerRednerFirstFrame");
        LiteGameUnityPlayer.onFirstFrameRendered();
    }

    public static void SendMessage(String message) {
        LiteGameEngine.getInstance().broadcastMessage(message);
    }
}
