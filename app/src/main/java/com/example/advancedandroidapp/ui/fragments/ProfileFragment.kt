package com.example.advancedandroidapp.ui.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.advancedandroidapp.R
import com.example.advancedandroidapp.databinding.FragmentProfileBinding
import com.example.advancedandroidapp.ui.viewmodels.ProfileViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                viewModel.updateProfileImage(uri)
            }
        }
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
        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        binding.apply {
            // Profile image click handler
            imageProfile.setOnClickListener {
                launchImagePicker()
            }

            // Edit profile button
            buttonEditProfile.setOnClickListener {
                enableEditMode(true)
            }

            // Save profile button
            buttonSave.setOnClickListener {
                saveProfile()
            }

            // Settings button
            buttonSettings.setOnClickListener {
                findNavController().navigate(R.id.action_profile_to_settings)
            }

            // My locations button
            buttonMyLocations.setOnClickListener {
                findNavController().navigate(R.id.action_profile_to_userLocations)
            }

            // Sign out button
            buttonSignOut.setOnClickListener {
                viewModel.signOut()
            }
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Observe user profile
                launch {
                    viewModel.userProfile.collect { profile ->
                        profile?.let {
                            binding.apply {
                                textUsername.text = it.username
                                textFullName.text = it.fullName
                                textBio.text = it.bio ?: getString(R.string.no_bio)
                                textEmail.text = it.email
                                textPhone.text = it.phoneNumber ?: getString(R.string.no_phone)

                                // Load profile image
                                Glide.with(requireContext())
                                    .load(it.avatarUrl)
                                    .placeholder(R.drawable.placeholder_profile)
                                    .error(R.drawable.error_profile)
                                    .circleCrop()
                                    .into(imageProfile)
                            }
                        }
                    }
                }

                // Observe loading state
                launch {
                    viewModel.isLoading.collect { isLoading ->
                        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                        binding.buttonSave.isEnabled = !isLoading
                    }
                }

                // Observe errors
                launch {
                    viewModel.error.collect { error ->
                        error?.let {
                            Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
                        }
                    }
                }

                // Observe edit mode
                launch {
                    viewModel.isEditMode.collect { isEditMode ->
                        enableEditMode(isEditMode)
                    }
                }
            }
        }
    }

    private fun launchImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePickerLauncher.launch(intent)
    }

    private fun enableEditMode(enabled: Boolean) {
        binding.apply {
            textFullName.isEnabled = enabled
            textBio.isEnabled = enabled
            textPhone.isEnabled = enabled
            buttonSave.visibility = if (enabled) View.VISIBLE else View.GONE
            buttonEditProfile.visibility = if (enabled) View.GONE else View.VISIBLE
        }
    }

    private fun saveProfile() {
        binding.apply {
            val updatedProfile = viewModel.userProfile.value?.copy(
                fullName = textFullName.text.toString(),
                bio = textBio.text.toString(),
                phoneNumber = textPhone.text.toString()
            )
            updatedProfile?.let {
                viewModel.updateProfile(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
