package com.example.zegovoiceroom.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zegovoiceroom.data.model.VoiceRoom
import com.example.zegovoiceroom.databinding.FragmentVoiceRoomListBinding
import com.example.zegovoiceroom.ui.adapter.VoiceRoomAdapter
import com.example.zegovoiceroom.ui.room.VoiceRoomActivity

class VoiceRoomListFragment : Fragment() {
    private var _binding: FragmentVoiceRoomListBinding? = null
    private val binding get() = requireNotNull(_binding)
    private val viewModel: VoiceRoomListViewModel by viewModels()
    private val roomAdapter = VoiceRoomAdapter(
        onHostRoom = { openVoiceRoom(it, isHost = true) },
        onJoinAudience = { openVoiceRoom(it, isHost = false) }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVoiceRoomListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.roomRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.roomRecyclerView.adapter = roomAdapter

        viewModel.rooms.observe(viewLifecycleOwner) { rooms ->
            roomAdapter.submitList(rooms)
            binding.emptyView.isVisible = rooms.isEmpty()
            binding.roomRecyclerView.isVisible = rooms.isNotEmpty()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshRooms()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun openVoiceRoom(room: VoiceRoom, isHost: Boolean) {
        startActivity(VoiceRoomActivity.createIntent(requireContext(), room, isHost))
    }
}
