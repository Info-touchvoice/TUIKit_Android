package com.example.zegovoiceroom.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.zegovoiceroom.data.model.Seat
import com.example.zegovoiceroom.data.model.UserRole
import com.example.zegovoiceroom.databinding.ItemSeatBinding

class SeatAdapter : ListAdapter<Seat, SeatAdapter.SeatViewHolder>(DiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeatViewHolder {
        val binding = ItemSeatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SeatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SeatViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class SeatViewHolder(private val binding: ItemSeatBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(seat: Seat) {
            val user = seat.occupiedBy
            binding.avatarText.text = user?.initials ?: (seat.index + 1).toString()
            binding.nameText.text = user?.displayName ?: "Open seat"
            binding.roleText.text = if (seat.role == UserRole.HOST) "Host seat" else "Audience seat"
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<Seat>() {
        override fun areItemsTheSame(oldItem: Seat, newItem: Seat): Boolean {
            return oldItem.index == newItem.index
        }

        override fun areContentsTheSame(oldItem: Seat, newItem: Seat): Boolean {
            return oldItem == newItem
        }
    }
}
