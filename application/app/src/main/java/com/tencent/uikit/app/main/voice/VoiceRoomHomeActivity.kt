package com.tencent.uikit.app.main.voice

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tencent.uikit.app.R
import com.tencent.uikit.app.main.BaseActivity
import com.trtc.uikit.livekit.voiceroom.VoiceRoomListActivity

class VoiceRoomHomeActivity : BaseActivity() {
    private lateinit var viewModel: VoiceRoomHomeViewModel
    private lateinit var bannerAdapter: VoiceRoomBannerAdapter
    private lateinit var popularRoomAdapter: VoiceRoomCardAdapter
    private lateinit var newRoomAdapter: VoiceRoomCardAdapter
    private lateinit var guildAdapter: VoiceRoomGuildAdapter
    private lateinit var bannerRecyclerView: RecyclerView
    private lateinit var bannerIndicatorLayout: LinearLayout
    private lateinit var bottomNavigationView: BottomNavigationView

    private val bannerHandler = Handler(Looper.getMainLooper())
    private var bannerAutoRunnable: Runnable? = null
    private var selectedBannerIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_activity_voice_room_home)

        viewModel = ViewModelProvider(this).get(VoiceRoomHomeViewModel::class.java)
        initViews()
        initRecyclerViews()
        initBottomNavigation()
        render(viewModel.getUiState())
    }

    override fun onResume() {
        super.onResume()
        startBannerAutoSlide()
    }

    override fun onPause() {
        stopBannerAutoSlide()
        super.onPause()
    }

    override fun onDestroy() {
        stopBannerAutoSlide()
        super.onDestroy()
    }

    private fun initViews() {
        findViewById<View>(R.id.button_voice_home_back).setOnClickListener { finish() }
        bannerRecyclerView = findViewById(R.id.recycler_voice_home_banners)
        bannerIndicatorLayout = findViewById(R.id.layout_voice_home_banner_indicator)
        bottomNavigationView = findViewById(R.id.bottom_navigation_voice_home)

        setupSectionHeader(
            root = findViewById(R.id.section_popular_rooms),
            title = getString(R.string.app_voice_home_popular_rooms),
            showAction = true
        )
        setupSectionHeader(
            root = findViewById(R.id.section_new_rooms),
            title = getString(R.string.app_voice_home_new_rooms),
            showAction = true
        )
        setupSectionHeader(
            root = findViewById(R.id.section_voice_guilds),
            title = getString(R.string.app_voice_home_guilds),
            showAction = false
        )
    }

    private fun setupSectionHeader(root: View, title: String, showAction: Boolean) {
        root.findViewById<TextView>(R.id.text_voice_section_title).text = title
        root.findViewById<TextView>(R.id.text_voice_section_action).apply {
            visibility = if (showAction) View.VISIBLE else View.GONE
            setOnClickListener { openVoiceRoomList() }
        }
    }

    private fun initRecyclerViews() {
        bannerAdapter = VoiceRoomBannerAdapter { openVoiceRoomList() }
        popularRoomAdapter = VoiceRoomCardAdapter(R.layout.app_item_voice_room_card) { openVoiceRoomList() }
        newRoomAdapter = VoiceRoomCardAdapter(R.layout.app_item_voice_room_card_compact) { openVoiceRoomList() }
        guildAdapter = VoiceRoomGuildAdapter { showComingSoon() }

        bannerRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@VoiceRoomHomeActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = bannerAdapter
            PagerSnapHelper().attachToRecyclerView(this)
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        val layoutManager = recyclerView.layoutManager as? LinearLayoutManager ?: return
                        val position = layoutManager.findFirstCompletelyVisibleItemPosition()
                            .takeIf { it != RecyclerView.NO_POSITION }
                            ?: layoutManager.findFirstVisibleItemPosition()
                        if (position != RecyclerView.NO_POSITION) {
                            selectedBannerIndex = position
                            updateBannerIndicators(bannerAdapter.itemCount)
                        }
                    }
                }
            })
        }

        findViewById<RecyclerView>(R.id.recycler_voice_home_popular).apply {
            layoutManager = GridLayoutManager(this@VoiceRoomHomeActivity, 2)
            adapter = popularRoomAdapter
            isNestedScrollingEnabled = false
        }

        findViewById<RecyclerView>(R.id.recycler_voice_home_new).apply {
            layoutManager = LinearLayoutManager(this@VoiceRoomHomeActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = newRoomAdapter
        }

        findViewById<RecyclerView>(R.id.recycler_voice_home_guilds).apply {
            layoutManager = LinearLayoutManager(this@VoiceRoomHomeActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = guildAdapter
        }
    }

    private fun initBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener { item ->
            val tab = when (item.itemId) {
                R.id.navigation_voice_home -> VoiceRoomHomeTab.HOME
                R.id.navigation_voice_rooms -> VoiceRoomHomeTab.ROOMS
                R.id.navigation_voice_create -> VoiceRoomHomeTab.CREATE
                R.id.navigation_voice_family -> VoiceRoomHomeTab.GUILDS
                R.id.navigation_voice_profile -> VoiceRoomHomeTab.PROFILE
                else -> VoiceRoomHomeTab.HOME
            }
            render(viewModel.selectTab(tab))
            when (tab) {
                VoiceRoomHomeTab.HOME -> true
                VoiceRoomHomeTab.ROOMS,
                VoiceRoomHomeTab.CREATE -> {
                    openVoiceRoomList()
                    true
                }
                VoiceRoomHomeTab.GUILDS,
                VoiceRoomHomeTab.PROFILE -> {
                    showComingSoon()
                    true
                }
            }
        }
    }

    private fun render(uiState: VoiceRoomHomeUiState) {
        bannerAdapter.submitList(uiState.banners)
        popularRoomAdapter.submitList(uiState.popularRooms)
        newRoomAdapter.submitList(uiState.newRooms)
        guildAdapter.submitList(uiState.guilds)
        selectedBannerIndex = selectedBannerIndex.coerceIn(0, (uiState.banners.size - 1).coerceAtLeast(0))
        updateBannerIndicators(uiState.banners.size)
        bottomNavigationView.selectedItemId = when (uiState.selectedTab) {
            VoiceRoomHomeTab.HOME -> R.id.navigation_voice_home
            VoiceRoomHomeTab.ROOMS -> R.id.navigation_voice_rooms
            VoiceRoomHomeTab.CREATE -> R.id.navigation_voice_create
            VoiceRoomHomeTab.GUILDS -> R.id.navigation_voice_family
            VoiceRoomHomeTab.PROFILE -> R.id.navigation_voice_profile
        }
    }

    private fun startBannerAutoSlide() {
        stopBannerAutoSlide()
        if (bannerAdapter.itemCount <= 1) return
        bannerAutoRunnable = object : Runnable {
            override fun run() {
                val itemCount = bannerAdapter.itemCount
                if (itemCount <= 1) return

                selectedBannerIndex = (selectedBannerIndex + 1) % itemCount
                if (selectedBannerIndex == 0) {
                    bannerRecyclerView.scrollToPosition(selectedBannerIndex)
                } else {
                    bannerRecyclerView.smoothScrollToPosition(selectedBannerIndex)
                }
                updateBannerIndicators(itemCount)
                bannerHandler.postDelayed(this, BANNER_AUTO_SCROLL_DELAY_MS)
            }
        }.also { bannerHandler.postDelayed(it, BANNER_AUTO_SCROLL_DELAY_MS) }
    }

    private fun stopBannerAutoSlide() {
        bannerAutoRunnable?.let { bannerHandler.removeCallbacks(it) }
        bannerAutoRunnable = null
    }

    private fun updateBannerIndicators(itemCount: Int) {
        bannerIndicatorLayout.removeAllViews()
        repeat(itemCount) { index ->
            val isSelected = index == selectedBannerIndex
            val indicator = View(this).apply {
                setBackgroundResource(
                    if (isSelected) {
                        R.drawable.app_voice_home_indicator_active
                    } else {
                        R.drawable.app_voice_home_indicator_inactive
                    }
                )
                layoutParams = LinearLayout.LayoutParams(
                    if (isSelected) dp(18) else dp(6),
                    dp(6)
                ).apply {
                    marginStart = 4
                    marginEnd = 4
                }
            }
            bannerIndicatorLayout.addView(indicator)
        }
    }

    private fun openVoiceRoomList() {
        startActivity(Intent(this, VoiceRoomListActivity::class.java))
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }

    private fun showComingSoon() {
        Toast.makeText(this, R.string.app_voice_home_coming_soon, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val BANNER_AUTO_SCROLL_DELAY_MS = 4000L
    }
}
