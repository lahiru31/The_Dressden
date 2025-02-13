package com.example.advancedandroidapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.advancedandroidapp.R
import com.example.advancedandroidapp.databinding.FragmentHomeBinding
import com.example.advancedandroidapp.ui.viewmodels.HomeViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        binding.apply {
            // Set up click listeners
            btnExplore.setOnClickListener {
                findNavController().navigate(R.id.action_home_to_map)
            }

            btnProfile.setOnClickListener {
                findNavController().navigate(R.id.action_home_to_profile)
            }

            // Set up refresh layout
            swipeRefreshLayout.setOnRefreshListener {
                viewModel.refreshData()
            }

            // Set up RecyclerView
            recyclerViewLocations.apply {
                adapter = viewModel.locationAdapter
                setHasFixedSize(true)
            }
        }

        // Check and request permissions if needed
        viewModel.checkAndRequestPermissions(requireActivity())
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Observe user profile
                launch {
                    viewModel.userProfile.collect { profile ->
                        profile?.let {
                            binding.textWelcome.text = getString(R.string.welcome_user, it.username)
                            binding.textLastSync.text = getString(R.string.last_sync, it.updatedAt)
                        }
                    }
                }

                // Observe nearby locations
                launch {
                    viewModel.nearbyLocations.collect { locations ->
                        viewModel.locationAdapter.submitList(locations)
                        binding.emptyView.visibility = if (locations.isEmpty()) View.VISIBLE else View.GONE
                    }
                }

                // Observe loading state
                launch {
                    viewModel.isLoading.collect { isLoading ->
                        binding.swipeRefreshLayout.isRefreshing = isLoading
                        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                    }
                }

                // Observe errors
                launch {
                    viewModel.error.collect { error ->
                        error?.let {
                            Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG)
                                .setAction("Retry") { viewModel.refreshData() }
                                .show()
                        }
                    }
                }

                // Observe network status
                launch {
                    viewModel.isOnline.collect { isOnline ->
                        binding.offlineBar.visibility = if (!isOnline) View.VISIBLE else View.GONE
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
