package com.tencent.uikit.app.main.popular

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tencent.uikit.app.R

class PopularRoomsAdapter(private val rooms: List<PopularRoomData>) :
    RecyclerView.Adapter<PopularRoomsAdapter.RoomViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.app_item_popular_room, parent, false)
        return RoomViewHolder(view)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        holder.bind(rooms[position])
    }

    override fun getItemCount(): Int = rooms.size

    class RoomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val avatarContainer: View = itemView.findViewById(R.id.view_popular_room_avatar_bg)
        private val avatarInitial: TextView = itemView.findViewById(R.id.tv_popular_room_avatar_initial)
        private val hotBadge: TextView = itemView.findViewById(R.id.tv_popular_room_hot)
        private val title: TextView = itemView.findViewById(R.id.tv_popular_room_title)
        private val country: TextView = itemView.findViewById(R.id.tv_popular_room_country)
        private val vipLabel: TextView = itemView.findViewById(R.id.tv_popular_room_vip)
        private val awards: TextView = itemView.findViewById(R.id.tv_popular_room_awards)
        private val subtitle: TextView = itemView.findViewById(R.id.tv_popular_room_subtitle)
        private val score: TextView = itemView.findViewById(R.id.tv_popular_room_score)
        private val supportLabel: TextView = itemView.findViewById(R.id.tv_popular_room_support)

        fun bind(room: PopularRoomData) {
            avatarContainer.background = createAvatarBackground(room.avatarColors)
            avatarInitial.text = room.initial
            title.text = room.title
            country.text = room.country
            vipLabel.text = room.vipLabel
            awards.text = room.awards
            subtitle.text = room.subtitle
            score.text = room.score
            supportLabel.text = room.supportLabel
            hotBadge.visibility = if (room.isHot) View.VISIBLE else View.GONE
        }

        private fun createAvatarBackground(colors: IntArray): GradientDrawable {
            return GradientDrawable(GradientDrawable.Orientation.TL_BR, colors).apply {
                cornerRadius = itemView.resources.displayMetrics.density * AVATAR_RADIUS_DP
            }
        }
    }

    companion object {
        private const val AVATAR_RADIUS_DP = 14f
    }
}
