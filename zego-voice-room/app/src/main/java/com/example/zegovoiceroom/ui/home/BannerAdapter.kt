package com.example.zegovoiceroom.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.zegovoiceroom.data.Banner
import com.example.zegovoiceroom.databinding.ItemBannerBinding

class BannerAdapter : RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {
    private val items = mutableListOf<Banner>()

    fun submitList(banners: List<Banner>) {
        items.clear()
        items.addAll(banners)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val binding = ItemBannerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BannerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class BannerViewHolder(private val binding: ItemBannerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Banner) = with(binding) {
            bannerImage.setImageResource(item.imageRes)
            bannerTitle.text = item.title
            bannerSubtitle.text = item.subtitle
        }
    }
}
