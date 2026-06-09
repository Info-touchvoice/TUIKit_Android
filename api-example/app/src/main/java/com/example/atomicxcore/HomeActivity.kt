package com.example.atomicxcore

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.atomicxcore.databinding.ActivityHomeBinding
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    private val roomAdapter = RoomAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupTabs()
        setupRooms()
        setupBottomNavigation()
        observeRooms()
    }

    private fun setupTabs() = with(binding.tabLayout) {
        addTab(newTab().setText(R.string.voice_tab_related))
        addTab(newTab().setText(R.string.voice_tab_popular))
        addTab(newTab().setText(R.string.voice_tab_discover))
        addTab(newTab().setText(R.string.voice_tab_new))

        addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewModel.selectTab(tab.position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) = Unit

            override fun onTabReselected(tab: TabLayout.Tab) {
                viewModel.selectTab(tab.position)
            }
        })
    }

    private fun setupRooms() {
        binding.recyclerRooms.adapter = roomAdapter
        binding.recyclerRooms.setHasFixedSize(true)
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.nav_group
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            binding.tvHomeTitle.text = item.title
            true
        }
    }

    private fun observeRooms() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.rooms.collect(roomAdapter::submitList)
            }
        }
    }
}
