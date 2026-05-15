package com.example.techevents.presentation.ui.eventdetail

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.techevents.R
import com.example.techevents.data.api.RetrofitClient
import com.example.techevents.data.datasource.LocalDataSourceImpl
import com.example.techevents.data.datasource.RemoteDataSourceImpl
import com.example.techevents.data.local.AppDatabase
import com.example.techevents.data.repository.EventRepositoryImpl
import com.example.techevents.presentation.state.UiState
import com.example.techevents.presentation.ui.editevent.EditEventActivity
import com.example.techevents.presentation.viewmodel.EventDetailViewModel
import com.example.techevents.utils.showToast
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.Locale

class EventDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_EVENT_ID = "extra_event_id"
    }

    private lateinit var viewModel: EventDetailViewModel
    private lateinit var progressBar: ProgressBar
    private lateinit var tvTitle: TextView
    private lateinit var tvDay: TextView
    private lateinit var tvDateLabel: TextView
    private lateinit var tvTime: TextView
    private lateinit var tvLocation: TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvCategory: TextView
    private lateinit var tvFormat: TextView
    private lateinit var tvCapacity: TextView
    private lateinit var tvCapacityPct: TextView
    private lateinit var progressCapacity: ProgressBar
    private lateinit var btnEdit: MaterialButton

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
        tvTitle = findViewById(R.id.tvTitle)
        tvDay = findViewById(R.id.tvDay)
        tvDateLabel = findViewById(R.id.tvDateLabel)
        tvTime = findViewById(R.id.tvTime)
        tvLocation = findViewById(R.id.tvLocation)
        tvDescription = findViewById(R.id.tvDescription)
        tvCategory = findViewById(R.id.tvCategory)
        tvFormat = findViewById(R.id.tvFormat)
        tvCapacity = findViewById(R.id.tvCapacity)
        tvCapacityPct = findViewById(R.id.tvCapacityPct)
        progressCapacity = findViewById(R.id.progressCapacity)
        btnEdit = findViewById(R.id.btnEnroll)

        btnEdit.text = "Editar evento"
        btnEdit.setOnClickListener {
            val intent = Intent(this, EditEventActivity::class.java)
            intent.putExtra(EditEventActivity.EXTRA_EVENT_ID, eventId)
            editLauncher.launch(intent)
        }

        findViewById<MaterialButton>(R.id.btnBack).setOnClickListener { finish() }
    }

    private fun setupViewModel() {
        val dao = AppDatabase.getInstance(this).eventDao()
        val remote = RemoteDataSourceImpl(RetrofitClient.api)
        val local = LocalDataSourceImpl(dao)
        val repository = EventRepositoryImpl(remote, local)
        val factory = EventDetailViewModel.Factory(repository)
        viewModel = ViewModelProvider(this, factory)[EventDetailViewModel::class.java]
    }

    private fun observeEvent() {
        viewModel.event.observe(this) { state ->
            when (state) {
                is UiState.Loading -> progressBar.visibility = View.VISIBLE
                is UiState.Success -> {
                    progressBar.visibility = View.GONE
                    val event = state.data

                    tvTitle.text = event.title
                    tvTime.text = event.time
                    tvLocation.text = event.location
                    tvDescription.text = event.description
                    tvCategory.text = event.category
                    tvFormat.text = if (event.isOnline) "ONLINE" else "PRESENCIAL"

                    parseDate(event.date)

                    val pct = if (event.capacity > 0) event.enrolled * 100 / event.capacity else 0
                    tvCapacity.text = "${event.enrolled} de ${event.capacity} vagas"
                    tvCapacityPct.text = "$pct%"
                    progressCapacity.progress = pct
                }
                is UiState.Error -> {
                    progressBar.visibility = View.GONE
                    showToast(state.message)
                    finish()
                }
                is UiState.Empty -> finish()
            }
        }
    }

    private fun parseDate(apiDate: String) {
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = sdf.parse(apiDate) ?: return
            tvDay.text = SimpleDateFormat("dd", Locale.getDefault()).format(date)
            val month = SimpleDateFormat("MMM", Locale("pt", "BR")).format(date)
                .uppercase().replace(".", "")
            val weekDay = SimpleDateFormat("EEE", Locale("pt", "BR")).format(date)
                .uppercase().replace(".", "")
            tvDateLabel.text = "$month · $weekDay"
        } catch (e: Exception) {
            tvDay.text = ""
            tvDateLabel.text = apiDate
        }
    }
}
