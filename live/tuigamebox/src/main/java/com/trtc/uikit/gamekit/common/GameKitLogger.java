package com.trtc.uikit.gamekit.common;

import android.util.Log;
import com.tencent.trtc.TRTCCloud;
import com.trtc.tuikit.common.system.ContextProvider;
import org.json.JSONException;
import org.json.JSONObject;

public final class GameKitLogger {

    public static final String MODULE_GAME_KIT = "LiteGameKit";

    private static final String API = "TuikitLog";
    private static final String LOG_KEY_API = "api";
    private static final String LOG_KEY_PARAMS = "params";
    private static final String LOG_KEY_PARAMS_LEVEL = "level";
    private static final String LOG_KEY_PARAMS_MESSAGE = "message";
    private static final String LOG_KEY_PARAMS_FILE = "file";
    private static final String LOG_KEY_PARAMS_MODULE = "module";
    private static final String LOG_KEY_PARAMS_LINE = "line";

    private static final int LOG_LEVEL_INFO = 0;
    private static final int LOG_LEVEL_WARNING = 1;
    private static final int LOG_LEVEL_ERROR = 2;

    private final String moduleName;
    private final String fileName;

    private GameKitLogger(String moduleName, String fileName) {
        this.moduleName = moduleName;
        this.fileName = fileName;
    }

    public static GameKitLogger getGameKitLogger(String file) {
        return new GameKitLogger(MODULE_GAME_KIT, file);
    }

    private static void log(String module, String file, int level, String message) {
        try {
            JSONObject json = new JSONObject();
            json.put(LOG_KEY_API, API);

            JSONObject params = new JSONObject();
            params.put(LOG_KEY_PARAMS_LEVEL, level);
            params.put(LOG_KEY_PARAMS_MESSAGE, message);
            params.put(LOG_KEY_PARAMS_MODULE, module);
            params.put(LOG_KEY_PARAMS_FILE, file);
            params.put(LOG_KEY_PARAMS_LINE, 0);

            json.put(LOG_KEY_PARAMS, params);

            String jsonStr = json.toString();
            TRTCCloud.sharedInstance(ContextProvider.getApplicationContext()).callExperimentalAPI(jsonStr);
        } catch (JSONException e) {
            Log.e("Logger", e.toString());
        } catch (Exception e) {
            // 防止 TRTCCloud 或 ContextProvider 的调用抛出未捕获异常
            Log.e("Logger", e.toString());
        }
    }

    public void info(String message) {
        log(LOG_LEVEL_INFO, message);
    }

    public void warn(String message) {
        log(LOG_LEVEL_WARNING, message);
    }

    public void error(String message) {
        log(LOG_LEVEL_ERROR, message);
    }

    private void log(int level, String message) {
        log(this.moduleName, this.fileName, level, message);
    }
}