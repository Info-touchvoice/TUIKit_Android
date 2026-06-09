package com.example.zegovoiceroom.ui.voice

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.zegovoiceroom.R
import com.example.zegovoiceroom.data.Seat
import com.example.zegovoiceroom.databinding.ItemSeatBinding

class SeatAdapter : RecyclerView.Adapter<SeatAdapter.SeatViewHolder>() {
    private val items = mutableListOf<Seat>()

    fun submitList(seats: List<Seat>) {
        items.clear()
        items.addAll(seats)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeatViewHolder {
        val binding = ItemSeatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SeatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SeatViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class SeatViewHolder(private val binding: ItemSeatBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(seat: Seat) = with(binding) {
            seatLabel.text = seat.label
            seatUser.text = seat.userName ?: root.context.getString(R.string.empty_seat)
            seatStatus.setImageResource(
                when {
                    seat.userName == null -> R.drawable.ic_add
                    seat.isMuted -> R.drawable.ic_mic_off
                    else -> R.drawable.ic_mic
                }
            )
            seatAvatar.setImageResource(if (seat.isHostSeat) R.drawable.ic_crown else R.drawable.ic_person)
            root.isSelected = seat.isHostSeat
        }
    }
}
