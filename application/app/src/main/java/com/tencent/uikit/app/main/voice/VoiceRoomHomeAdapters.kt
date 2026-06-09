package com.tencent.uikit.app.main.voice

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.tencent.uikit.app.R

class VoiceRoomBannerAdapter(
    private val onBannerClick: () -> Unit
) : RecyclerView.Adapter<VoiceRoomBannerAdapter.BannerViewHolder>() {
    private val items = mutableListOf<VoiceRoomBanner>()

    fun submitList(newItems: List<VoiceRoomBanner>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.app_item_voice_banner, parent, false)
        return BannerViewHolder(view, onBannerClick)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class BannerViewHolder(
        itemView: View,
        private val onBannerClick: () -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val background: ConstraintLayout = itemView.findViewById(R.id.layout_voice_banner_background)
        private val title: TextView = itemView.findViewById(R.id.text_voice_banner_title)
        private val subtitle: TextView = itemView.findViewById(R.id.text_voice_banner_subtitle)
        private val action: TextView = itemView.findViewById(R.id.text_voice_banner_action)

        fun bind(item: VoiceRoomBanner) {
            background.setBackgroundResource(item.backgroundRes)
            title.text = item.title
            subtitle.text = item.subtitle
            action.text = item.action
            itemView.setOnClickListener { onBannerClick() }
        }
    }
}

class VoiceRoomCardAdapter(
    private val layoutRes: Int,
    private val onRoomClick: (VoiceRoomCard) -> Unit
) : RecyclerView.Adapter<VoiceRoomCardAdapter.RoomViewHolder>() {
    private val items = mutableListOf<VoiceRoomCard>()

    fun submitList(newItems: List<VoiceRoomCard>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
        return RoomViewHolder(view, onRoomClick)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class RoomViewHolder(
        itemView: View,
        private val onRoomClick: (VoiceRoomCard) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val avatar: TextView = itemView.findViewById(R.id.text_room_avatar)
        private val tag: TextView = itemView.findViewById(R.id.text_room_tag)
        private val title: TextView = itemView.findViewById(R.id.text_room_title)
        private val host: TextView = itemView.findViewById(R.id.text_room_host)
        private val listenerCount: TextView = itemView.findViewById(R.id.text_room_listener_count)
        private val seats: TextView = itemView.findViewById(R.id.text_room_seats)

        fun bind(item: VoiceRoomCard) {
            avatar.setBackgroundResource(item.avatarBackgroundRes)
            avatar.text = item.title.initials()
            tag.text = item.tag
            title.text = item.title
            host.text = "Host ${item.host}"
            listenerCount.text = "${item.listenerCount} ${itemView.context.getString(R.string.app_voice_home_online)}"
            seats.text = item.seatSummary
            itemView.setOnClickListener { onRoomClick(item) }
        }

        private fun String.initials(): String {
            return split(" ")
                .filter { it.isNotBlank() }
                .take(2)
                .joinToString(separator = "") { word -> word.first().uppercaseChar().toString() }
                .ifEmpty { "VR" }
        }
    }
}

class VoiceRoomGuildAdapter(
    private val onGuildClick: (VoiceRoomGuild) -> Unit
) : RecyclerView.Adapter<VoiceRoomGuildAdapter.GuildViewHolder>() {
    private val items = mutableListOf<VoiceRoomGuild>()

    fun submitList(newItems: List<VoiceRoomGuild>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuildViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.app_item_voice_guild, parent, false)
        return GuildViewHolder(view, onGuildClick)
    }

    override fun onBindViewHolder(holder: GuildViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class GuildViewHolder(
        itemView: View,
        private val onGuildClick: (VoiceRoomGuild) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val badge: TextView = itemView.findViewById(R.id.text_guild_badge)
        private val name: TextView = itemView.findViewById(R.id.text_guild_name)
        private val members: TextView = itemView.findViewById(R.id.text_guild_members)
        private val description: TextView = itemView.findViewById(R.id.text_guild_description)

        fun bind(item: VoiceRoomGuild) {
            badge.text = item.name.firstOrNull()?.uppercaseChar()?.toString() ?: "F"
            badge.setTextColor(ContextCompat.getColor(itemView.context, item.accentColorRes))
            name.text = item.name
            members.text = item.memberCount
            description.text = item.description
            itemView.setOnClickListener { onGuildClick(item) }
        }
    }
}
