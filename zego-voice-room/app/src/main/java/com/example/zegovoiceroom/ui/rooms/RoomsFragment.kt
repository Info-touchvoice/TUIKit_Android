package com.example.zegovoiceroom.ui.rooms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zegovoiceroom.databinding.FragmentRoomsBinding
import com.example.zegovoiceroom.ui.main.MainViewModel
import com.example.zegovoiceroom.ui.main.RoomNavigator

class RoomsFragment : Fragment() {
    private var _binding: FragmentRoomsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()
    private val roomAdapter = RoomAdapter { room -> (activity as? RoomNavigator)?.openRoom(room) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRoomsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        roomList.layoutManager = LinearLayoutManager(requireContext())
        roomList.adapter = roomAdapter
        roomSearchInput.doAfterTextChanged { viewModel.search(it?.toString().orEmpty()) }
        viewModel.rooms.observe(viewLifecycleOwner, roomAdapter::submitList)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
