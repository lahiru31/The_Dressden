package com.example.advancedandroidapp.ui.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.advancedandroidapp.R
import com.example.advancedandroidapp.databinding.FragmentMapBinding
import com.example.advancedandroidapp.ui.viewmodels.MapViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MapViewModel by viewModels()
    private var googleMap: GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMap()
        setupUI()
        observeViewModel()
    }

    private fun setupMap() {
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun setupUI() {
        binding.apply {
            // Set up search functionality
            searchBar.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let { viewModel.searchLocations(it) }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    // Optionally implement real-time search
                    return false
                }
            })

            // Set up FAB for adding new location
            fabAddLocation.setOnClickListener {
                googleMap?.cameraPosition?.target?.let { latLng ->
                    viewModel.prepareNewLocation(latLng)
                    findNavController().navigate(R.id.action_map_to_addLocation)
                }
            }

            // Set up location types filter
            chipGroupFilters.setOnCheckedStateChangeListener { group, checkedIds ->
                viewModel.updateFilters(checkedIds)
            }
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Observe locations
                launch {
                    viewModel.locations.collect { locations ->
                        googleMap?.clear()
                        locations.forEach { location ->
                            googleMap?.addMarker(
                                MarkerOptions()
                                    .position(LatLng(location.latitude, location.longitude))
                                    .title(location.name)
                                    .snippet(location.description)
                            )?.tag = location.id
                        }
                    }
                }

                // Observe current location
                launch {
                    viewModel.currentLocation.collect { location ->
                        location?.let {
                            val latLng = LatLng(it.latitude, it.longitude)
                            googleMap?.animateCamera(
                                CameraUpdateFactory.newLatLngZoom(latLng, 15f)
                            )
                        }
                    }
                }

                // Observe loading state
                launch {
                    viewModel.isLoading.collect { isLoading ->
                        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
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
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        setupMapSettings()
        checkLocationPermission()
    }

    private fun setupMapSettings() {
        googleMap?.apply {
            uiSettings.isZoomControlsEnabled = true
            uiSettings.isMyLocationButtonEnabled = true
            
            setOnMarkerClickListener { marker ->
                marker.tag?.let { locationId ->
                    findNavController().navigate(
                        MapFragmentDirections.actionMapToLocationDetail(locationId.toString())
                    )
                }
                true
            }
        }
    }

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            enableMyLocation()
        } else {
            requestLocationPermission()
        }
    }

    private fun enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap?.isMyLocationEnabled = true
            viewModel.getCurrentLocation()
        }
    }

    private fun requestLocationPermission() {
        requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation()
            } else {
                Snackbar.make(
                    binding.root,
                    R.string.location_permission_needed,
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}
