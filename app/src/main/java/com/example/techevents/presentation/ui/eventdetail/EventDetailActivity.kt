package com.example.techevents.presentation.ui.eventdetail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import coil.load
import com.example.techevents.R
import com.example.techevents.data.api.RetrofitClient
import com.example.techevents.data.local.AppDatabase
import com.example.techevents.data.repository.EventRepositoryImpl
import com.example.techevents.domain.usecase.GetEventDetailUseCase
import com.example.techevents.presentation.state.UiState
import com.example.techevents.presentation.ui.editevent.EditEventActivity
import com.example.techevents.presentation.viewmodel.EventDetailViewModel
import com.example.techevents.utils.toDisplayDate

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
    private lateinit var btnEdit: Button
    private lateinit var ivCover: ImageView

    private lateinit var eventId: String

    private val editLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            setResult(RESULT_OK)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_detail)

        eventId = intent.getStringExtra(EXTRA_EVENT_ID) ?: run { finish(); return }

        setupViews()
        setupViewModel()
        observeEvent()
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
        btnEdit = findViewById(R.id.btnEdit)
        ivCover = findViewById(R.id.ivCover)

        btnEdit.setOnClickListener {
            val intent = Intent(this, EditEventActivity::class.java)
            intent.putExtra(EditEventActivity.EXTRA_EVENT_ID, eventId)
            editLauncher.launch(intent)
        }

        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupViewModel() {
        val dao = AppDatabase.getInstance(this).eventDao()
        val repository = EventRepositoryImpl(RetrofitClient.api, dao)
        val factory = EventDetailViewModel.Factory(GetEventDetailUseCase(repository))
        viewModel = ViewModelProvider(this, factory)[EventDetailViewModel::class.java]
    }

    private fun formatDateFull(apiDate: String): String {
        return try {
            val input = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            val output = java.text.SimpleDateFormat("dd 'de' MMMM, yyyy", java.util.Locale("pt", "BR"))
            output.format(input.parse(apiDate)!!)
        } catch (e: Exception) {
            apiDate
        }
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
                    tvDate.text = formatDateFull(event.date)
                    tvTime.text = event.time
                    tvLocation.text = event.location
                    tvDescription.text = event.description
                    tvEnrolled.text = "${event.enrolled}/${event.capacity} inscritos"

                    if (!event.imageUrl.isNullOrBlank()) {
                        ivCover.visibility = View.VISIBLE
                        ivCover.load(event.imageUrl) { crossfade(true) }
                    } else {
                        ivCover.visibility = View.GONE
                    }

                    if (event.link.isNullOrBlank()) {
                        tvLink.visibility = View.GONE
                    } else {
                        tvLink.visibility = View.VISIBLE
                        tvLink.text = "🔗 ${event.link}"
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
