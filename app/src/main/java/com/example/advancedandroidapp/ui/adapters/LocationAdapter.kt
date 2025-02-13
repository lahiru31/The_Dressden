package com.example.advancedandroidapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.advancedandroidapp.R
import com.example.advancedandroidapp.data.models.Location
import com.example.advancedandroidapp.databinding.ItemLocationBinding
import java.text.SimpleDateFormat
import java.util.*

class LocationAdapter(
    private val onLocationClick: (Location) -> Unit
) : ListAdapter<Location, LocationAdapter.LocationViewHolder>(LocationDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val binding = ItemLocationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LocationViewHolder(binding, onLocationClick)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class LocationViewHolder(
        private val binding: ItemLocationBinding,
        private val onLocationClick: (Location) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

        fun bind(location: Location) {
            binding.apply {
                // Set basic information
                textLocationName.text = location.name
                textLocationDescription.text = location.description ?: ""
                textLocationAddress.text = location.address ?: ""
                textLocationCategory.text = location.category
                textCreatedDate.text = dateFormat.format(location.createdAt)

                // Set rating
                ratingBar.rating = location.rating ?: 0f
                textRating.text = String.format("%.1f", location.rating ?: 0f)

                // Load first photo if available
                location.photos?.firstOrNull()?.let { photoUrl ->
                    Glide.with(root.context)
                        .load(photoUrl)
                        .placeholder(R.drawable.placeholder_location)
                        .error(R.drawable.error_location)
                        .centerCrop()
                        .into(imageLocation)
                } ?: run {
                    imageLocation.setImageResource(R.drawable.placeholder_location)
                }

                // Set distance (implement distance calculation)
                textDistance.text = calculateDistance(location)

                // Set click listener
                root.setOnClickListener { onLocationClick(location) }

                // Set share button click listener
                buttonShare.setOnClickListener {
                    // Implement share functionality
                    shareLocation(location)
                }

                // Set favorite button
                buttonFavorite.setOnClickListener {
                    // Implement favorite functionality
                    toggleFavorite(location)
                }
            }
        }

        private fun calculateDistance(location: Location): String {
            // TODO: Implement actual distance calculation using current location
            return "2.5 km"
        }

        private fun shareLocation(location: Location) {
            // TODO: Implement share functionality
        }

        private fun toggleFavorite(location: Location) {
            // TODO: Implement favorite functionality
        }
    }

    private class LocationDiffCallback : DiffUtil.ItemCallback<Location>() {
        override fun areItemsTheSame(oldItem: Location, newItem: Location): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Location, newItem: Location): Boolean {
            return oldItem == newItem
        }
    }
}
