package com.tencent.uikit.app.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.tencent.imsdk.v2.V2TIMUserFullInfo
import com.tencent.imsdk.v2.V2TIMValueCallback
import com.tencent.qcloud.tuicore.TUILogin
import com.tencent.uikit.app.login.LoginActivity
import com.tencent.uikit.app.main.live.LiveActivity
import com.tencent.uikit.app.mine.UserManager


class MainActivity : BaseActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        openLiveActivity()
    }

    private fun openLiveActivity() {
        if (!TUILogin.isUserLogined()) {
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }
        getUserInfo()

        val intent = Intent(this@MainActivity, LiveActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun getUserInfo() {
        if (!TUILogin.isUserLogined()) {
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }
        UserManager.getInstance().getSelfUserInfo(object : V2TIMValueCallback<V2TIMUserFullInfo> {
            override fun onError(errorCode: Int, errorMsg: String?) {
                Log.e(TAG, "getUserInfo failed, code:$errorCode msg: $errorMsg")
            }

            override fun onSuccess(timUserFullInfo: V2TIMUserFullInfo?) {
                if (timUserFullInfo == null) {
                    Log.e(TAG, "getUserInfo result is empty")
                    return
                }
            }
        })
    }
}