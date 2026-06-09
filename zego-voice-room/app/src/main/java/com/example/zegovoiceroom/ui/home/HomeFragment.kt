package com.example.zegovoiceroom.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zegovoiceroom.databinding.FragmentHomeBinding
import com.example.zegovoiceroom.ui.main.MainViewModel
import com.example.zegovoiceroom.ui.main.RoomNavigator
import com.example.zegovoiceroom.ui.rooms.RoomAdapter

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()
    private val bannerAdapter = BannerAdapter()
    private val roomAdapter = RoomAdapter { room -> (activity as? RoomNavigator)?.openRoom(room) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        bannerSlider.adapter = bannerAdapter
        popularRooms.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        popularRooms.adapter = roomAdapter

        searchInput.doAfterTextChanged { viewModel.search(it?.toString().orEmpty()) }
        startRoomButton.setOnClickListener {
            val room = viewModel.rooms.value?.firstOrNull() ?: return@setOnClickListener
            (activity as? RoomNavigator)?.openRoom(room, asHost = true)
        }

        viewModel.banners.observe(viewLifecycleOwner, bannerAdapter::submitList)
        viewModel.rooms.observe(viewLifecycleOwner, roomAdapter::submitList)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
