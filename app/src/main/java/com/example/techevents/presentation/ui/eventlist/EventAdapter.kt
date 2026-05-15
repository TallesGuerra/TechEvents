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
        private val tvDay: TextView = itemView.findViewById(R.id.tvDay)
        private val tvMonth: TextView = itemView.findViewById(R.id.tvMonth)
        private val tvWeekday: TextView = itemView.findViewById(R.id.tvWeekday)
        private val tvFormat: TextView = itemView.findViewById(R.id.tvFormat)
        private val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvLocation: TextView = itemView.findViewById(R.id.tvLocation)
        private val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        private val tvEnrolled: TextView = itemView.findViewById(R.id.tvEnrolled)


        fun bind(event: Event) {
            // parse data formato "YYYY-MM-DD"
            val parts = event.date.split("-")
            val months = listOf("JAN","FEV","MAR","ABR","MAI","JUN","JUL","AGO","SET","OUT","NOV","DEZ")
            if (parts.size == 3) {
                tvDay.text = parts[2]
                tvMonth.text = months.getOrElse(parts[1].toIntOrNull()?.minus(1) ?: 0) { "?" }
                try {
                    val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                    val cal = java.util.Calendar.getInstance().apply { time = sdf.parse(event.date)!! }
                    val days = listOf("DOM","SEG","TER","QUA","QUI","SEX","SAB")
                    tvWeekday.text = days[cal.get(java.util.Calendar.DAY_OF_WEEK) - 1]
                } catch (e: Exception) { tvWeekday.text = "" }
            }

            tvTitle.text = event.title
            tvTime.text = event.time
            tvLocation.text = event.location
            tvEnrolled.text = "${event.enrolled}/${event.capacity}"

            if (event.category.isNotBlank()) {
                tvCategory.visibility = View.VISIBLE
                tvCategory.text = event.category
            } else {
                tvCategory.visibility = View.GONE
            }

            tvFormat.text = if (event.isOnline) "ONLINE" else "PRESENCIAL"

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