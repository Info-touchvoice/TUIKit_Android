package com.trtc.uikit.gamekit.gameview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import org.json.JSONException;
import org.json.JSONObject;
import com.tencent.cloud.tuikit.engine.room.TUIRoomDefine;
import com.tencent.cloud.tuikit.engine.room.TUIRoomEngine;
import com.tencent.cloud.tuikit.engine.room.TUIRoomObserver;
import com.trtc.uikit.gamekit.LiteGameEngine;
import com.trtc.uikit.gamekit.LiteGameUnityPlayer;
import com.trtc.uikit.gamekit.R;
import com.trtc.uikit.gamekit.common.GameKitLogger;

public class GameView extends FrameLayout {
    private static final GameKitLogger LOGGER = GameKitLogger.getGameKitLogger(GameView.class.getName());
    private final Context mContext;
    private String userId;
    private String userName;
    private String avatarUrl;
    private boolean isOwner = false;
    private LiteGameUnityPlayer mUnityPlayer;

    // 回调接口
    public interface OnHideListener {
        void onHide();
    }

    private OnHideListener mOnHideListener;

    public void setOnHideListener(OnHideListener listener) {
        LOGGER.info("setOnHideListener");
        this.mOnHideListener = listener;
    }

    private final TUIRoomObserver mRoomObserver = new TUIRoomObserver() {
        @Override
        public void onReceiveCustomMessage(TUIRoomDefine.RoomCustomMessage customMessage) {
            // LOGGER.info("onReceiveCustomMessage " + customMessage.businessId + " msg:" + customMessage.data);
            if (customMessage.businessId.equals(LiteGameEngine.customMessageBusinessId)) {
                JSONObject msgJsonObj;
                try {
                    if (!GameView.this.isOwner)
                    {
                        String runningGameId = LiteGameEngine.getInstance().getRunningGameId();
                        msgJsonObj = new JSONObject(customMessage.data);
                        String gameId = msgJsonObj.optString("gameId");
                        String actionType = msgJsonObj.optString("actionType");
                        if (runningGameId.isEmpty()) { // 当前没有游戏
                            if (!gameId.isEmpty() && !actionType.equals(LiteGameEngine.ActionType.ACTION_GAME_STOP)) {
                                startGame(gameId);
                                showView();
                            }
                        } else {
                            if (gameId.isEmpty() || actionType.equals(LiteGameEngine.ActionType.ACTION_GAME_STOP)) {
                                LiteGameEngine.getInstance().startLauncher();
                                hideView();
                            } else if (!runningGameId.equals(gameId)) {
                                LiteGameEngine.getInstance().startLauncher();
                                startGame(gameId);
                            }
                        }
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                } finally {
                    LiteGameEngine.getInstance().sendMessageToUnity(customMessage.data);
                }
            }
        }

        @Override
        public void onRemoteUserEnterRoom(String roomId, TUIRoomDefine.UserInfo userInfo) {
            LiteGameEngine.getInstance().onRemoteUserEnterRoom(userInfo.userId, userInfo.userName, userInfo.avatarUrl);
        }

        @Override
        public void onRemoteUserLeaveRoom(String roomId, TUIRoomDefine.UserInfo userInfo) {
            LiteGameEngine.getInstance().onRemoteUserLeaveRoom(userInfo.userId, userInfo.userName, userInfo.avatarUrl);
        }
    };

    public GameView(Context context) {
        this(context, null);
    }

    public GameView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        LayoutInflater.from(mContext).inflate(R.layout.gamekit_gameview_root_view, this, true);
        initUnityPlayer();
    }

    private void initUnityPlayer() {
        LOGGER.info("initUnityPlayer");
        if (mUnityPlayer != null) {
            return;
        }
        mUnityPlayer = LiteGameUnityPlayer.getUnityPlayer(mContext);
        android.view.View unityView = mUnityPlayer.getView();
        // 将 Unity 的 view 添加到这个 FrameLayout 中
        LayoutParams lp = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        FrameLayout unityPlayerContainer = findViewById(R.id.game_view_container);
        unityPlayerContainer.addView(unityView, lp);

        mUnityPlayer.setOnRenderFirstFrameListener(() -> {
            findViewById(R.id.game_view_mask).setVisibility(INVISIBLE);
            findViewById(R.id.game_view_container).setVisibility(VISIBLE);
        });

        // 让 UnityPlayer 获取焦点以便接收输入
        mUnityPlayer.requestFocus();
        TUIRoomEngine.sharedInstance().addObserver(mRoomObserver);
    }

    // -------------------- 生命周期控制方法，Activity 应当调用对应方法 --------------------
    public void onStart(){
        LOGGER.info("onStart");
        if (mUnityPlayer != null) {
            mUnityPlayer.onStart();
        }
    }

    public void onStop() {
        LOGGER.info("onStop");
        if (mUnityPlayer != null) {
            mUnityPlayer.onStop();
        }
    }

    public void onResume() {
        LOGGER.info("onResume");
        if (mUnityPlayer != null) {
            mUnityPlayer.onResume();
        }
    }

    public void onPause() {
        LOGGER.info("onPause");
        if (mUnityPlayer != null) {
            mUnityPlayer.onPause();
        }
    }

    public void onDestroy() {
        LOGGER.info("onDestroy");
        LiteGameEngine.getInstance().startLauncher();
        LiteGameUnityPlayer.detachFromParent();
        if (mUnityPlayer != null) {
            mUnityPlayer.setOnRenderFirstFrameListener(null);
        }
        TUIRoomEngine.sharedInstance().removeObserver(mRoomObserver);
    }

    public void onLowMemory() {
        LOGGER.info("onLowMemory");
        if (mUnityPlayer != null) {
            mUnityPlayer.lowMemory();
        }
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        LOGGER.info("onWindowFocusChanged");
        if (mUnityPlayer != null) {
            mUnityPlayer.windowFocusChanged(hasFocus);
        }
    }

    // 将输入事件转发给 UnityPlayer（若 Unity 处理了，则直接返回 true）
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (mUnityPlayer != null && mUnityPlayer.injectEvent(event)) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mUnityPlayer != null) {
            if (mUnityPlayer.injectEvent(event)) {
                return true;
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if (mUnityPlayer != null && mUnityPlayer.injectEvent(event)) {
            return true;
        }
        return super.onGenericMotionEvent(event);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public void init(String userId, String userName, String avatarUrl, boolean isOwner) {
        LOGGER.info("init userId:" + userId + " userName:" + userName + " avatarUrl:" + avatarUrl + " isOwner:" + isOwner);
        this.userId = userId;
        this.userName = userName;
        this.avatarUrl = avatarUrl;
        this.isOwner = isOwner;
    }

    public void showView() {
        LOGGER.info("showView");
        setVisibility(VISIBLE);
    }

    public void hideView() {
        LOGGER.info("hideView");
        setVisibility(GONE);
    }

    public void startGame(String gameID) {
        LOGGER.info("startGame gameID:" + gameID);
        LiteGameEngine.getInstance().startGame(gameID, isOwner, userId, userName, avatarUrl);
    }

    public void stopCurrentRunningGame() {
        LOGGER.info("stopCurrentRunningGame");
        LiteGameEngine.getInstance().startLauncher();
        if (isOwner) {
            LiteGameEngine.getInstance().broadcastGameQuitMessage();
        }
    }
}
