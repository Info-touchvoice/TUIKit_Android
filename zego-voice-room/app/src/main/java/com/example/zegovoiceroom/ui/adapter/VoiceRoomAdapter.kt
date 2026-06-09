package com.example.zegovoiceroom.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.zegovoiceroom.data.model.VoiceRoom
import com.example.zegovoiceroom.databinding.ItemVoiceRoomBinding

class VoiceRoomAdapter(
    private val onHostRoom: (VoiceRoom) -> Unit,
    private val onJoinAudience: (VoiceRoom) -> Unit
) : ListAdapter<VoiceRoom, VoiceRoomAdapter.VoiceRoomViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VoiceRoomViewHolder {
        val binding = ItemVoiceRoomBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VoiceRoomViewHolder(binding, onHostRoom, onJoinAudience)
    }

    override fun onBindViewHolder(holder: VoiceRoomViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class VoiceRoomViewHolder(
        private val binding: ItemVoiceRoomBinding,
        private val onHostRoom: (VoiceRoom) -> Unit,
        private val onJoinAudience: (VoiceRoom) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(room: VoiceRoom) {
            binding.titleText.text = room.title
            binding.topicText.text = room.topic
            binding.metaText.text = "Room ${room.roomId} - Host ${room.hostName} - ${room.audienceCount} listening"
            binding.hostButton.setOnClickListener { onHostRoom(room) }
            binding.audienceButton.setOnClickListener { onJoinAudience(room) }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<VoiceRoom>() {
        override fun areItemsTheSame(oldItem: VoiceRoom, newItem: VoiceRoom): Boolean {
            return oldItem.roomId == newItem.roomId
        }

        override fun areContentsTheSame(oldItem: VoiceRoom, newItem: VoiceRoom): Boolean {
            return oldItem == newItem
        }
    }
}
