package com.example.zegovoiceroom.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zegovoiceroom.R
import com.example.zegovoiceroom.data.model.VoiceRoom
import com.example.zegovoiceroom.databinding.DialogCreateRoomBinding
import com.example.zegovoiceroom.databinding.FragmentHomeBinding
import com.example.zegovoiceroom.ui.adapter.ChatMessageAdapter
import com.example.zegovoiceroom.ui.adapter.SeatAdapter
import com.example.zegovoiceroom.ui.room.VoiceRoomActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = requireNotNull(_binding)
    private val viewModel: HomeViewModel by viewModels()
    private val seatAdapter = SeatAdapter()
    private val chatAdapter = ChatMessageAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.seatRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.seatRecyclerView.adapter = seatAdapter
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.chatRecyclerView.adapter = chatAdapter
        binding.createRoomButton.setOnClickListener { showCreateRoomDialog() }

        viewModel.profile.observe(viewLifecycleOwner) { profile ->
            binding.welcomeText.text = getString(R.string.home) + ", ${profile?.displayName.orEmpty()}"
        }
        viewModel.seats.observe(viewLifecycleOwner, seatAdapter::submitList)
        viewModel.messages.observe(viewLifecycleOwner, chatAdapter::submitList)
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun showCreateRoomDialog() {
        val dialogBinding = DialogCreateRoomBinding.inflate(layoutInflater)
        dialogBinding.roomIdInput.setText("room_${System.currentTimeMillis().toString().takeLast(6)}")
        dialogBinding.titleInput.setText("Voice Room")
        dialogBinding.topicInput.setText("Live audio conversation")

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.create_room)
            .setView(dialogBinding.root)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.host_room, null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val (room, error) = viewModel.createRoom(
                    roomId = dialogBinding.roomIdInput.text?.toString().orEmpty(),
                    title = dialogBinding.titleInput.text?.toString().orEmpty(),
                    topic = dialogBinding.topicInput.text?.toString().orEmpty()
                )
                if (error != null) {
                    Snackbar.make(binding.root, error, Snackbar.LENGTH_LONG).show()
                } else if (room != null) {
                    dialog.dismiss()
                    openVoiceRoom(room, isHost = true)
                }
            }
        }
        dialog.show()
    }

    private fun openVoiceRoom(room: VoiceRoom, isHost: Boolean) {
        startActivity(VoiceRoomActivity.createIntent(requireContext(), room, isHost))
    }
}
