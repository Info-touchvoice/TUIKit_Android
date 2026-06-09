package com.example.zegovoiceroom.ui.rooms

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.zegovoiceroom.data.VoiceRoom
import com.example.zegovoiceroom.databinding.ItemRoomBinding

class RoomAdapter(
    private val onRoomClick: (VoiceRoom) -> Unit
) : RecyclerView.Adapter<RoomAdapter.RoomViewHolder>() {
    private val items = mutableListOf<VoiceRoom>()

    fun submitList(rooms: List<VoiceRoom>) {
        items.clear()
        items.addAll(rooms)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val binding = ItemRoomBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RoomViewHolder(binding, onRoomClick)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class RoomViewHolder(
        private val binding: ItemRoomBinding,
        private val onRoomClick: (VoiceRoom) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(room: VoiceRoom) = with(binding) {
            roomCover.setImageResource(room.coverRes)
            hostAvatar.setImageResource(room.hostAvatarRes)
            roomName.text = room.name
            hostName.text = room.hostName
            onlineUsers.text = root.resources.getQuantityString(
                com.example.zegovoiceroom.R.plurals.online_users,
                room.onlineUsers,
                room.onlineUsers
            )
            pkBadge.text = if (room.isPkEnabled) "PK" else "Chat"
            root.setOnClickListener { onRoomClick(room) }
        }
    }
}
