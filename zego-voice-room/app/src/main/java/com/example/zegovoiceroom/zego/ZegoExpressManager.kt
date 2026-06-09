package com.example.zegovoiceroom.zego

import android.app.Application
import android.util.Log

class ZegoExpressManager {
    fun prepare(application: Application): Boolean {
        if (!ZegoConfig.hasCredentials) {
            Log.i(TAG, "ZEGO credentials are not configured; using local preview UI.")
            return false
        }

        return runCatching {
            Class.forName("im.zego.zegoexpress.ZegoExpressEngine")
            Log.i(TAG, "ZEGO Express SDK is available for ${application.packageName}.")
            true
        }.getOrElse { throwable ->
            Log.w(TAG, "ZEGO Express SDK was not available at runtime.", throwable)
            false
        }
    }

    private companion object {
        const val TAG = "ZegoExpressManager"
    }
}
