package com.example.techevents.presentation.ui.eventlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.techevents.R
import com.example.techevents.domain.model.Event

class EventAdapter(
    private val onClick: (Event) -> Unit
) : ListAdapter<Event, EventAdapter.EventViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivCover: ImageView = itemView.findViewById(R.id.ivCover)
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        private val tvLocation: TextView = itemView.findViewById(R.id.tvLocation)
        private val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        private val tvEnrolled: TextView = itemView.findViewById(R.id.tvEnrolled)

        fun bind(event: Event) {
            tvTitle.text = event.title
            tvDate.text = "${event.date}  ${event.time}"
            tvLocation.text = event.location
            tvCategory.text = event.category.ifBlank { null }
            tvCategory.visibility = if (event.category.isBlank()) View.GONE else View.VISIBLE
            tvEnrolled.text = "${event.enrolled}/${event.capacity} inscritos"

            if (!event.imageUrl.isNullOrBlank()) {
                ivCover.visibility = View.VISIBLE
                ivCover.load(event.imageUrl) {
                    crossfade(true)
                    error(android.R.drawable.ic_menu_gallery)
                }
            } else {
                ivCover.visibility = View.GONE
            }

            itemView.setOnClickListener { onClick(event) }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Event>() {
        override fun areItemsTheSame(oldItem: Event, newItem: Event) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Event, newItem: Event) = oldItem == newItem
    }
}
