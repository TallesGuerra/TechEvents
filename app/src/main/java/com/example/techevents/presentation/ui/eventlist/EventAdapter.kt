package com.example.techevents.presentation.ui.eventlist

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
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
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        private val tvLocation: TextView = itemView.findViewById(R.id.tvLocation)
        private val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        private val tvEnrolled: TextView = itemView.findViewById(R.id.tvEnrolled)
        private val tvFormat: TextView = itemView.findViewById(R.id.tvFormat)

        fun bind(event: Event) {
            tvTitle.text = event.title
            tvDate.text = "${event.date}  ${event.time}"
            tvLocation.text = event.location
            tvEnrolled.text = "${event.enrolled}/${event.capacity} inscritos"

            if (event.category.isNotBlank()) {
                tvCategory.visibility = View.VISIBLE
                tvCategory.text = event.category
                tvCategory.backgroundTintList = ColorStateList.valueOf(categoryColor(event.category))
                tvCategory.setTextColor(android.graphics.Color.WHITE)
            } else {
                tvCategory.visibility = View.GONE
            }

            tvFormat.text = if (event.isOnline) "Online" else "Presencial"
            tvFormat.backgroundTintList = ColorStateList.valueOf(
                if (event.isOnline) 0xFF0078D4.toInt() else 0xFF2E7D32.toInt()
            )

            itemView.setOnClickListener { onClick(event) }
        }

        private fun categoryColor(category: String) = when (category.trim().lowercase()) {
            "android"     -> 0xFF34A853.toInt()
            "kotlin"      -> 0xFF7F52FF.toInt()
            "backend"     -> 0xFF0078D4.toInt()
            "web"         -> 0xFFF97316.toInt()
            "ia", "ai"    -> 0xFF06B6D4.toInt()
            "ios"         -> 0xFF1C1C1E.toInt()
            "devops"      -> 0xFFEF4444.toInt()
            "flutter"     -> 0xFF54C5F8.toInt()
            else          -> 0xFF6366F1.toInt()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Event>() {
        override fun areItemsTheSame(oldItem: Event, newItem: Event) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Event, newItem: Event) = oldItem == newItem
    }
}