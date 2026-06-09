package com.example.atomicxcore

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.atomicxcore.databinding.ItemVoiceRoomBinding
import java.text.NumberFormat
import java.util.Locale

class RoomAdapter : ListAdapter<VoiceRoom, RoomAdapter.RoomViewHolder>(RoomDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val binding = ItemVoiceRoomBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RoomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class RoomViewHolder(
        private val binding: ItemVoiceRoomBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(room: VoiceRoom) = with(binding) {
            tvAvatar.text = room.avatarInitials
            tvAvatar.background = GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                intArrayOf(Color.parseColor(room.avatarStartColor), Color.parseColor(room.avatarEndColor))
            ).apply {
                shape = GradientDrawable.OVAL
            }

            tvHotBadge.text = room.label
            tvHotBadge.visibility = if (room.label.isBlank()) View.GONE else View.VISIBLE
            tvRoomTitle.text = room.title
            ivCountryFlag.setImageResource(flagFor(room.countryCode))
            tvVipBadge.text = room.vipBadge
            tvRankLevel.text = "R${room.rankIcons}"
            tvWelcome.text = room.welcomeText
            tvPopularity.text = NumberFormat.getIntegerInstance(Locale.US).format(room.popularity)
            tvPkBadge.visibility = if (room.pkEnabled) View.VISIBLE else View.INVISIBLE

            ivCrownOne.alpha = if (room.rankIcons >= 1) 1f else DISABLED_ICON_ALPHA
            ivCrownTwo.alpha = if (room.rankIcons >= 2) 1f else DISABLED_ICON_ALPHA
            ivCrownThree.alpha = if (room.rankIcons >= 3) 1f else DISABLED_ICON_ALPHA
        }

        private fun flagFor(countryCode: String): Int {
            return when (countryCode.uppercase(Locale.US)) {
                "BD" -> R.drawable.ic_flag_bangladesh
                else -> R.drawable.ic_flag_bangladesh
            }
        }

        private companion object {
            const val DISABLED_ICON_ALPHA = 0.25f
        }
    }

    private object RoomDiffCallback : DiffUtil.ItemCallback<VoiceRoom>() {
        override fun areItemsTheSame(oldItem: VoiceRoom, newItem: VoiceRoom): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: VoiceRoom, newItem: VoiceRoom): Boolean {
            return oldItem == newItem
        }
    }
}
