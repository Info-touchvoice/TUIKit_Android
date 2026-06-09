package com.tencent.uikit.app.login

import android.content.Intent
import android.content.DialogInterface
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.text.HtmlCompat
import androidx.lifecycle.lifecycleScope
import com.tencent.qcloud.tuicore.TUILogin
import com.tencent.qcloud.tuicore.util.SPUtils
import com.tencent.qcloud.tuikit.debug.GenerateTestUserSig
import com.tencent.qcloud.tuikit.tuicallkit.TUICallKit.Companion.createInstance
import com.tencent.uikit.app.R
import com.tencent.uikit.app.common.utils.DEMO_LOGIN_SUCCESS
import com.tencent.uikit.app.common.utils.KeyMetrics
import com.tencent.uikit.app.main.BaseActivity
import com.tencent.uikit.app.main.MainActivity
import io.trtc.tuikit.atomicx.widget.basicwidget.toast.AtomicToast
import io.trtc.tuikit.atomicxcore.api.CompletionHandler
import io.trtc.tuikit.atomicxcore.api.login.LoginStore
import kotlinx.coroutines.launch
import java.util.UUID

class LoginActivity : BaseActivity() {
    companion object {
        private const val TAG = "LoginActivity"
        private const val SP_NAME = "app_uikit"
        private const val SP_KEY_USER_ID = "userId"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!isTaskRoot
            && intent.hasCategory(Intent.CATEGORY_LAUNCHER)
            && intent.action != null
            && intent.action.equals(Intent.ACTION_MAIN)
        ) {
            finish()
            return
        }
        setContentView(R.layout.app_activity_login)
        applyLoginSystemBars()
        initView()
    }

    private fun initView() {
        findViewById<View>(R.id.btn_login).setOnClickListener {
            loginWithDemoUser("apple")
        }
        findViewById<View>(R.id.btn_facebook_login).setOnClickListener {
            loginWithDemoUser("facebook")
        }
        findViewById<View>(R.id.btn_google_login).setOnClickListener {
            loginWithDemoUser("google")
        }
        findViewById<View>(R.id.btn_phone_login).setOnClickListener {
            showManualUserIdDialog()
        }
        findViewById<TextView>(R.id.tv_login_terms).text = HtmlCompat.fromHtml(
            getString(R.string.app_login_terms_html),
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
    }

    private fun applyLoginSystemBars() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
            window.navigationBarColor = Color.TRANSPARENT
        }
    }

    private fun loginWithDemoUser(provider: String) {
        val userId = getOrCreateUserId(provider)
        login(userId)
    }

    private fun getOrCreateUserId(provider: String): String {
        val preferences = SPUtils.getInstance(SP_NAME)
        val savedUserId = preferences.getString(SP_KEY_USER_ID)?.trim().orEmpty()
        if (savedUserId.isNotEmpty()) {
            return savedUserId
        }

        val suffix = UUID.randomUUID().toString().replace("-", "").take(8)
        val userId = "demo_${provider}_$suffix"
        preferences.put(SP_KEY_USER_ID, userId)
        return userId
    }

    private fun showManualUserIdDialog() {
        val input = EditText(this).apply {
            setSingleLine()
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            hint = getString(R.string.app_hint_user_id)
            setText(SPUtils.getInstance(SP_NAME).getString(SP_KEY_USER_ID))
            setSelectAllOnFocus(true)
        }
        val dialog = AlertDialog.Builder(this)
            .setTitle(R.string.app_user_id)
            .setView(input)
            .setNegativeButton(R.string.app_btn_cancel, null)
            .setPositiveButton(R.string.app_btn_login, null)
            .show()

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            val userId = input.text.toString().trim()
            if (userId.isEmpty()) {
                AtomicToast.show(
                    this,
                    getString(R.string.app_user_id_is_empty),
                    AtomicToast.Style.ERROR
                )
                return@setOnClickListener
            }

            SPUtils.getInstance(SP_NAME).put(SP_KEY_USER_ID, userId)
            dialog.dismiss()
            login(userId)
        }
    }

    private fun login(userId: String) {
        if (userId.isEmpty()) {
            AtomicToast.show(
                this,
                getString(R.string.app_user_id_is_empty),
                AtomicToast.Style.ERROR
            )
            return
        }
        val userSig = GenerateTestUserSig.genTestUserSig(userId)
        LoginStore.shared.login(this, GenerateTestUserSig.SDKAPPID, userId, userSig, object : CompletionHandler {
            override fun onSuccess() {
                Log.i(TAG, "login onSuccess")
                val instance = createInstance(application)
                instance.enableFloatWindow(true)
                instance.enableVirtualBackground(true)
                instance.enableIncomingBanner(true)
                instance.enableAITranscriber(true)
                getUserInfo()

                KeyMetrics.reportAtomicMetrics(DEMO_LOGIN_SUCCESS)
            }

            override fun onFailure(code: Int, desc: String) {
                AtomicToast.show(
                    this@LoginActivity,
                    getString(R.string.app_toast_login_fail, code, desc),
                    AtomicToast.Style.ERROR
                )
                Log.e(TAG, "login fail errorCode: $code errorMessage:$desc")
            }
        })
        TUILogin.login(this, GenerateTestUserSig.SDKAPPID, userId, userSig, null)
    }

    private fun getUserInfo() {
        lifecycleScope.launch {
            LoginStore.shared.loginState.loginUserInfo.collect { loginUserInfo ->
                loginUserInfo?.let {
                    if (it.userID.isEmpty()) {
                        return@collect
                    }
                    if (it.nickname.isNullOrEmpty() || it.avatarURL.isNullOrEmpty()) {
                        val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
    }
}