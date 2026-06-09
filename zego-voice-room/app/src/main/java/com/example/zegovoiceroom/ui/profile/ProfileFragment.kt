package com.example.zegovoiceroom.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.R.drawable.abc_ic_ab_back_material
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.zegovoiceroom.data.model.UserProfile
import com.example.zegovoiceroom.databinding.FragmentProfileBinding
import com.example.zegovoiceroom.ui.login.LoginActivity
import com.google.android.material.snackbar.Snackbar

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = requireNotNull(_binding)
    private val viewModel: ProfileViewModel by viewModels()
    private val showBackButton: Boolean by lazy {
        arguments?.getBoolean(ARG_SHOW_BACK_BUTTON) ?: false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (showBackButton) {
            binding.toolbar.setNavigationIcon(abc_ic_ab_back_material)
            binding.toolbar.setNavigationOnClickListener { requireActivity().finish() }
        }

        binding.saveButton.setOnClickListener {
            val error = viewModel.updateProfile(
                displayName = binding.nameInput.text?.toString().orEmpty(),
                appSign = binding.appSignInput.text?.toString().orEmpty()
            )
            if (error != null) {
                Snackbar.make(binding.root, error, Snackbar.LENGTH_LONG).show()
            } else {
                Snackbar.make(binding.root, "Profile saved", Snackbar.LENGTH_SHORT).show()
            }
        }
        binding.logoutButton.setOnClickListener {
            viewModel.logout()
            openLogin()
        }

        viewModel.profile.observe(viewLifecycleOwner) { profile ->
            if (profile == null) {
                openLogin()
            } else {
                bindProfile(profile)
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun bindProfile(profile: UserProfile) {
        binding.avatarText.text = profile.initials
        binding.nameText.text = profile.displayName
        binding.idText.text = profile.userId
        if (binding.nameInput.text?.toString() != profile.displayName) {
            binding.nameInput.setText(profile.displayName)
        }
        if (binding.appSignInput.text?.toString() != profile.appSign) {
            binding.appSignInput.setText(profile.appSign)
        }
    }

    private fun openLogin() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    companion object {
        private const val ARG_SHOW_BACK_BUTTON = "show_back_button"

        fun newInstance(showBackButton: Boolean): ProfileFragment {
            return ProfileFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_SHOW_BACK_BUTTON, showBackButton)
                }
            }
        }
    }
}
