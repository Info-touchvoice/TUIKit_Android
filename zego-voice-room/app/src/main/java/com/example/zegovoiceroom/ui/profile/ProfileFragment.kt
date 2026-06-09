package com.example.zegovoiceroom.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.zegovoiceroom.R
import com.example.zegovoiceroom.data.UserSession
import com.example.zegovoiceroom.databinding.FragmentProfileBinding
import com.example.zegovoiceroom.ui.main.MainViewModel

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        val userId = UserSession(requireContext()).userId
        val profile = viewModel.profile(userId)
        avatar.setImageResource(profile.avatarRes)
        displayName.text = profile.displayName
        profileUserId.text = getString(R.string.profile_user_id, profile.userId)
        userLevel.text = getString(R.string.profile_level, profile.level)
        coins.text = getString(R.string.profile_coins, profile.coins)
        vipBadge.visibility = if (profile.isVip) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
