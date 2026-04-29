package com.example.techevents.presentation.ui.createevent

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import com.google.android.material.materialswitch.MaterialSwitch
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.techevents.R
import com.example.techevents.data.api.RetrofitClient
import com.example.techevents.data.local.AppDatabase
import com.example.techevents.data.repository.EventRepositoryImpl
import com.example.techevents.domain.usecase.CreateEventUseCase
import com.example.techevents.presentation.state.UiState
import com.example.techevents.presentation.viewmodel.CreateEventViewModel
import com.example.techevents.utils.showToast

class CreateEventActivity : AppCompatActivity() {

    private lateinit var viewModel: CreateEventViewModel
    private lateinit var etTitle: EditText
    private lateinit var etDescription: EditText
    private lateinit var etDate: EditText
    private lateinit var etTime: EditText
    private lateinit var etLocation: EditText
    private lateinit var etCategory: EditText
    private lateinit var etCapacity: EditText
    private lateinit var etLink: EditText
    private lateinit var cbIsOnline: MaterialSwitch
    private lateinit var btnSave: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)

        setupViews()
        setupViewModel()
        observeState()
    }

    private fun setupViews() {
        etTitle = findViewById(R.id.etTitle)
        etDescription = findViewById(R.id.etDescription)
        etDate = findViewById(R.id.etDate)
        etTime = findViewById(R.id.etTime)
        etLocation = findViewById(R.id.etLocation)
        etCategory = findViewById(R.id.etCategory)
        etCapacity = findViewById(R.id.etCapacity)
        etLink = findViewById(R.id.etLink)
        cbIsOnline = findViewById(R.id.cbIsOnline)
        btnSave = findViewById(R.id.btnSave)
        progressBar = findViewById(R.id.progressBar)

        btnSave.setOnClickListener { onSaveClicked() }
    }

    private fun setupViewModel() {
        val dao = AppDatabase.getInstance(this).eventDao()
        val repository = EventRepositoryImpl(RetrofitClient.api, dao)
        val factory = CreateEventViewModel.Factory(CreateEventUseCase(repository))
        viewModel = ViewModelProvider(this, factory)[CreateEventViewModel::class.java]
    }

    private fun onSaveClicked() {
        val title = etTitle.text.toString().trim()
        val description = etDescription.text.toString().trim()
        val date = etDate.text.toString().trim()
        val time = etTime.text.toString().trim()
        val location = etLocation.text.toString().trim()
        val category = etCategory.text.toString().trim()
        val capacityText = etCapacity.text.toString().trim()
        val link = etLink.text.toString().trim().ifBlank { null }
        val isOnline = cbIsOnline.isChecked

        if (title.isBlank() || date.isBlank() || location.isBlank()) {
            showToast("Preencha título, data e local")
            return
        }

        val capacity = capacityText.toIntOrNull() ?: 0

        viewModel.createEvent(title, description, date, time, location, category, isOnline, capacity, link)
    }

    private fun observeState() {
        viewModel.createState.observe(this) { state ->
            when (state) {
                is UiState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    btnSave.isEnabled = false
                }
                is UiState.Success -> {
                    progressBar.visibility = View.GONE
                    showToast("Evento criado com sucesso!")
                    setResult(RESULT_OK)
                    finish()
                }
                is UiState.Error -> {
                    progressBar.visibility = View.GONE
                    btnSave.isEnabled = true
                    showToast(state.message)
                }
                is UiState.Empty -> Unit
            }
        }
    }
}
