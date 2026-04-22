package com.example.techevents.presentation.ui.eventdetail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.techevents.R
import com.example.techevents.data.api.RetrofitClient
import com.example.techevents.data.repository.EventRepositoryImpl
import com.example.techevents.domain.usecase.GetEventDetailUseCase
import com.example.techevents.presentation.state.UiState
import com.example.techevents.presentation.viewmodel.EventDetailViewModel

class EventDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_EVENT_ID = "extra_event_id"
    }

    private lateinit var viewModel: EventDetailViewModel
    private lateinit var progressBar: ProgressBar
    private lateinit var tvError: TextView
    private lateinit var tvTitle: TextView
    private lateinit var tvDate: TextView
    private lateinit var tvTime: TextView
    private lateinit var tvLocation: TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvEnrolled: TextView
    private lateinit var tvLink: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_detail)

        setupViews()
        setupViewModel()
        observeEvent()

        val eventId = intent.getStringExtra(EXTRA_EVENT_ID) ?: return
        viewModel.loadEvent(eventId)
    }

    private fun setupViews() {
        progressBar = findViewById(R.id.progressBar)
        tvError = findViewById(R.id.tvError)
        tvTitle = findViewById(R.id.tvTitle)
        tvDate = findViewById(R.id.tvDate)
        tvTime = findViewById(R.id.tvTime)
        tvLocation = findViewById(R.id.tvLocation)
        tvDescription = findViewById(R.id.tvDescription)
        tvEnrolled = findViewById(R.id.tvEnrolled)
        tvLink = findViewById(R.id.tvLink)
    }

    private fun setupViewModel() {
        val repository = EventRepositoryImpl(RetrofitClient.api)
        val factory = EventDetailViewModel.Factory(GetEventDetailUseCase(repository))
        viewModel = ViewModelProvider(this, factory)[EventDetailViewModel::class.java]
    }

    private fun observeEvent() {
        viewModel.event.observe(this) { state ->
            progressBar.visibility = View.GONE
            tvError.visibility = View.GONE

            when (state) {
                is UiState.Loading -> progressBar.visibility = View.VISIBLE
                is UiState.Success -> {
                    val event = state.data
                    tvTitle.text = event.title
                    tvDate.text = event.date
                    tvTime.text = event.time
                    tvLocation.text = event.location
                    tvDescription.text = event.description
                    tvEnrolled.text = "${event.enrolled}/${event.capacity} inscritos"

                    if (event.link.isNullOrBlank()) {
                        tvLink.visibility = View.GONE
                    } else {
                        tvLink.visibility = View.VISIBLE
                        tvLink.text = event.link
                        tvLink.setOnClickListener {
                            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(event.link)))
                        }
                    }
                }
                is UiState.Error -> {
                    tvError.visibility = View.VISIBLE
                    tvError.text = state.message
                }
                is UiState.Empty -> {
                    tvError.visibility = View.VISIBLE
                    tvError.text = "Evento não encontrado"
                }
            }
        }
    }
}
