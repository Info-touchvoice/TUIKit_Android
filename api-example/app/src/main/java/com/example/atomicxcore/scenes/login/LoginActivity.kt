package com.example.atomicxcore.scenes.login

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.atomicxcore.R
import com.example.atomicxcore.components.LocalizedManager
import com.example.atomicxcore.databinding.ActivityLoginBinding
import com.example.atomicxcore.debug.GenerateTestUserSig
import com.example.atomicxcore.debug.SDKAPPID
import com.example.atomicxcore.scenes.featurelist.FeatureListActivity
import com.example.atomicxcore.utils.completionHandler
import com.example.atomicxcore.utils.PermissionHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.trtc.tuikit.atomicxcore.api.login.LoginListener
import io.trtc.tuikit.atomicxcore.api.login.LoginStatus
import io.trtc.tuikit.atomicxcore.api.login.LoginStore
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * Business scenario: User login page
 *
 * Related APIs:
 * - LoginStore.shared.login(context, sdkAppID, userID, userSig, completion) - SDK login
 * - LoginStore.shared.loginState.loginStatus - Login status observation (StateFlow)
 * - LoginStore.shared.addLoginListener(LoginListener) - Login event listener
 *
 * UserSig is generated locally (for debugging only)
 * Corresponds to LoginViewController on iOS
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    /** Key for locally cached user ID */
    private val cachedUserIDKey = "CachedLoginUserID"
    private var isLoading = false

    /** Login event listener */
    private val loginListener = object : LoginListener() {
        override fun onKickedOffline() {
            runOnUiThread {
                showAlert(
                    getString(R.string.common_warning),
                    getString(R.string.login_error_kickedOffline)
                )
            }
        }

        override fun onLoginExpired() {
            runOnUiThread {
                showAlert(
                    getString(R.string.common_warning),
                    getString(R.string.login_error_loginExpired)
                )
            }
        }
    }

    // MARK: - Lifecycle

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocalizedManager.applyLanguage(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureSystemBars()
        applyWindowInsets()
        setupActions()
        setupBindings()
    }

    override fun onDestroy() {
        super.onDestroy()
        LoginStore.shared.removeLoginListener(loginListener)
    }

    // MARK: - Setup

    private fun setupActions() {
        binding.btnAppleSignIn.setOnClickListener {
            onLoginTapped(LoginMethod.APPLE)
        }

        binding.btnFacebookConnect.setOnClickListener {
            onLoginTapped(LoginMethod.FACEBOOK)
        }

        binding.btnGoogleLogin.setOnClickListener {
            onLoginTapped(LoginMethod.GOOGLE)
        }

        binding.btnPhoneLogin.setOnClickListener {
            onLoginTapped(LoginMethod.PHONE)
        }

        binding.btnLanguage.setOnClickListener {
            LocalizedManager.showLanguageSwitchDialog(this)
        }
    }

    private fun setupBindings() {
        // Register login event listener
        LoginStore.shared.addLoginListener(loginListener)

        // Observe login status changes (StateFlow)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                LoginStore.shared.loginState.loginStatus.collect { status ->
                    updateLoginStatus(status)
                }
            }
        }
    }

    // MARK: - Actions

    private fun onLoginTapped(method: LoginMethod) {
        if (isLoading) {
            return
        }

        if (!PermissionHelper.isNetworkAvailable(this)) {
            showAlert(getString(R.string.common_warning), getString(R.string.permission_network_unavailable))
            return
        }

        performLogin(resolveCachedUserID(), method)
    }

    // MARK: - Login Logic

    private fun performLogin(userID: String, method: LoginMethod) {
        setLoading(true)

        // Auto-generate UserSig (debug mode only)
        val userSig = GenerateTestUserSig.genTestUserSig(userID)

        LoginStore.shared.login(
            this,
            SDKAPPID.toInt(),
            userID,
            userSig,
            completionHandler { code, message ->
                runOnUiThread {
                    if (code == 0) {
                        // Login succeeded; cache user ID for auto-fill on next cold start
                        getSharedPreferences("login_prefs", MODE_PRIVATE)
                            .edit()
                            .putString(cachedUserIDKey, userID)
                            .putString("CachedLoginMethod", method.name)
                            .apply()

                        // Delay 2 seconds then check profile and navigate (aligned with iOS behavior)
                        binding.root.postDelayed({
                            setLoading(false)
                            checkProfileAndNavigate()
                        }, 2000)
                    } else {
                        setLoading(false)
                        Toast.makeText(
                            this,
                            getString(R.string.login_error_loginFailed, message),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        )
    }

    // MARK: - Status Handling

    private fun updateLoginStatus(status: LoginStatus) {
        when (status) {
            LoginStatus.UNLOGIN -> {
                Toast.makeText(this, getString(R.string.login_status_notLoggedIn), Toast.LENGTH_SHORT).show()
            }
            LoginStatus.LOGINED -> {
                Toast.makeText(this, getString(R.string.login_status_loggedIn), Toast.LENGTH_SHORT).show()
            }
        }
    }

    // MARK: - Navigation

    /**
     * After login succeeds, check whether nickname is empty to decide
     * whether to navigate to profile setup or feature list.
     * Corresponds to checkProfileAndNavigate on iOS
     */
    private fun checkProfileAndNavigate() {
        val userInfo = LoginStore.shared.loginState.loginUserInfo.value
        val nickname = userInfo?.nickname ?: ""

        if (nickname.isEmpty()) {
            // Nickname is empty → navigate to profile setup page
            startActivity(Intent(this, ProfileSetupActivity::class.java))
        } else {
            // Nickname is set → go directly to feature list
            startActivity(Intent(this, FeatureListActivity::class.java))
        }
        finish()
    }

    // MARK: - UI Helpers

    private fun setLoading(loading: Boolean) {
        isLoading = loading
        binding.btnAppleSignIn.isEnabled = !loading
        binding.btnFacebookConnect.isEnabled = !loading
        binding.btnGoogleLogin.isEnabled = !loading
        binding.btnPhoneLogin.isEnabled = !loading
        binding.loginScroll.alpha = if (loading) 0.72f else 1f
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
    }

    private fun showAlert(title: String, message: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(getString(R.string.common_confirm), null)
            .show()
    }

    private fun configureSystemBars() {
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }
    }

    /**
     * Restore last logged-in user ID from local cache.
     * If no cache exists, generate a random User ID and cache it to avoid
     * multiple devices using the same ID.
     * Corresponds to restoreCachedUserID on iOS
     */
    private fun resolveCachedUserID(): String {
        val prefs = getSharedPreferences("login_prefs", MODE_PRIVATE)
        val cachedUserID = prefs.getString(cachedUserIDKey, null)
        return if (!cachedUserID.isNullOrEmpty()) {
            cachedUserID
        } else {
            val randomUserID = generateRandomUserID()
            prefs.edit().putString(cachedUserIDKey, randomUserID).apply()
            randomUserID
        }
    }

    /**
     * Generate a random numeric User ID (9-digit random number).
     * This ID also serves as the anchor's room ID.
     */
    private fun generateRandomUserID(): String {
        return (100_000_000..999_999_999).random().toString()
    }

    private fun applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.loginRoot) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.btnLanguage.updateLayoutParams<ConstraintLayout.LayoutParams> {
                topMargin = 16.dp() + systemBars.top
            }
            binding.loginContent.updatePadding(
                top = 88.dp() + systemBars.top,
                bottom = 32.dp() + systemBars.bottom
            )
            insets
        }
        ViewCompat.requestApplyInsets(binding.loginRoot)
    }

    private fun Int.dp(): Int {
        return (this * resources.displayMetrics.density).roundToInt()
    }

    private enum class LoginMethod {
        APPLE,
        FACEBOOK,
        GOOGLE,
        PHONE
    }
}
