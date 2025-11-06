package com.trtc.uikit.gamekit;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.unity3d.player.IUnityPlayerLifecycleEvents;
import com.unity3d.player.UnityPlayer;

public class LiteGameUnityPlayer extends UnityPlayer {
    private static LiteGameUnityPlayer sUnityPlayer;
    private boolean isFirstFrameRendered = false;
    private OnRenderFirstFrameListener onRenderFirstFrameListener;

    public interface OnRenderFirstFrameListener {
        void onFirstFrameRendered();
    }
    public LiteGameUnityPlayer(Context context) {
        super(context);
    }

    public LiteGameUnityPlayer(Context context, IUnityPlayerLifecycleEvents iUnityPlayerLifecycleEvents) {
        super(context, iUnityPlayerLifecycleEvents);
    }

    @Override
    protected void kill() {
    }

    private void setFirstFrameRendered() {
        isFirstFrameRendered = true;
        if (onRenderFirstFrameListener != null) {
            onRenderFirstFrameListener.onFirstFrameRendered();
        }
    }

    public static synchronized LiteGameUnityPlayer getUnityPlayer(Context ctx) {
        if (sUnityPlayer == null) {
            sUnityPlayer = new LiteGameUnityPlayer(ctx.getApplicationContext());
        }
        return sUnityPlayer;
    }

    public static void detachFromParent() {
        if (sUnityPlayer != null) {
            View v = sUnityPlayer.getView();
            if (v != null && v.getParent() != null) {
                ((ViewGroup) v.getParent()).removeView(v);
            }
        }
    }

    public static void onFirstFrameRendered() {
        if (sUnityPlayer != null) {
            sUnityPlayer.setFirstFrameRendered();
        }
    }
    public static void UnitySendMessage(String gameOjbName, String methodName, String data) {
        UnityPlayer.UnitySendMessage(gameOjbName, methodName, data);
    }

    public void setOnRenderFirstFrameListener(LiteGameUnityPlayer.OnRenderFirstFrameListener listener) {
        onRenderFirstFrameListener = listener;
        if (isFirstFrameRendered && onRenderFirstFrameListener != null) {
            onRenderFirstFrameListener.onFirstFrameRendered();
        }
    }
}
