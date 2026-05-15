package com.example.techevents.presentation.ui.editevent

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.techevents.R
import com.example.techevents.data.api.RetrofitClient
import com.example.techevents.data.datasource.LocalDataSourceImpl
import com.example.techevents.data.datasource.RemoteDataSourceImpl
import com.example.techevents.data.local.AppDatabase
import com.example.techevents.data.repository.EventRepositoryImpl
import com.example.techevents.presentation.state.UiState
import com.example.techevents.presentation.viewmodel.EditEventViewModel
import com.example.techevents.utils.DateMaskWatcher
import com.example.techevents.utils.TimeMaskWatcher
import com.example.techevents.utils.showToast
import com.example.techevents.utils.toApiDate
import com.example.techevents.utils.toDisplayDate
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class EditEventActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_EVENT_ID = "extra_event_id"
    }

    private lateinit var viewModel: EditEventViewModel
    private lateinit var etTitle: EditText
    private lateinit var etDescription: EditText
    private lateinit var etDate: EditText
    private lateinit var etTime: EditText
    private lateinit var etLocation: EditText
    private lateinit var chipGroupCategory: ChipGroup
    private lateinit var etCapacity: EditText
    private lateinit var etLink: EditText
    private lateinit var toggleFormat: MaterialButtonToggleGroup
    private lateinit var btnSave: MaterialButton
    private lateinit var btnDelete: MaterialButton
    private lateinit var progressBar: ProgressBar

    private lateinit var eventId: String

    private val categories = listOf(
        "Android", "iOS", "Kotlin", "Flutter", "Web", "Backend",
        "DevOps", "IA / Machine Learning", "Segurança", "Cloud", "UX/UI", "Outro"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_event)

        eventId = intent.getStringExtra(EXTRA_EVENT_ID) ?: run { finish(); return }

        setupViews()
        setupViewModel()
        observeStates()
        viewModel.loadEvent(eventId)
    }

    private fun setupViews() {
        etTitle = findViewById(R.id.etTitle)
        etDescription = findViewById(R.id.etDescription)
        etDate = findViewById(R.id.etDate)
        etTime = findViewById(R.id.etTime)
        etLocation = findViewById(R.id.etLocation)
        chipGroupCategory = findViewById(R.id.chipGroupCategory)
        etCapacity = findViewById(R.id.etCapacity)
        etLink = findViewById(R.id.etLink)
        toggleFormat = findViewById(R.id.toggleFormat)
        btnSave = findViewById(R.id.btnSave)
        btnDelete = findViewById(R.id.btnDelete)
        progressBar = findViewById(R.id.progressBar)

        categories.forEach { cat ->
            val chip = Chip(this)
            chip.text = cat
            chip.isCheckable = true
            chipGroupCategory.addView(chip)
        }

        btnSave.setOnClickListener { onSaveClicked() }
        btnDelete.setOnClickListener { confirmDelete() }
        findViewById<MaterialButton>(R.id.btnBack).setOnClickListener { finish() }
        findViewById<MaterialButton>(R.id.btnCancel).setOnClickListener { finish() }

        etDate.addTextChangedListener(DateMaskWatcher())
        etTime.addTextChangedListener(TimeMaskWatcher())
    }

    private fun setupViewModel() {
        val dao = AppDatabase.getInstance(this).eventDao()
        val remote = RemoteDataSourceImpl(RetrofitClient.api)
        val local = LocalDataSourceImpl(dao)
        val repository = EventRepositoryImpl(remote, local)
        val factory = EditEventViewModel.Factory(repository)
        viewModel = ViewModelProvider(this, factory)[EditEventViewModel::class.java]
    }

    private fun observeStates() {
        viewModel.event.observe(this) { state ->
            when (state) {
                is UiState.Loading -> progressBar.visibility = View.VISIBLE
                is UiState.Success -> {
                    progressBar.visibility = View.GONE
                    val event = state.data
                    etTitle.setText(event.title)
                    etDescription.setText(event.description)
                    etDate.setText(event.date.toDisplayDate())
                    etTime.setText(event.time)
                    etLocation.setText(event.location)
                    etCapacity.setText(event.capacity.toString())
                    etLink.setText(event.link ?: "")

                    for (i in 0 until chipGroupCategory.childCount) {
                        val chip = chipGroupCategory.getChildAt(i) as? Chip
                        if (chip?.text == event.category) {
                            chip.isChecked = true
                            break
                        }
                    }

                    toggleFormat.check(if (event.isOnline) R.id.btnOnline else R.id.btnPresencial)
                }
                is UiState.Error -> {
                    progressBar.visibility = View.GONE
                    showToast(state.message)
                    finish()
                }
                is UiState.Empty -> finish()
            }
        }

        viewModel.updateState.observe(this) { state ->
            when (state) {
                is UiState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    btnSave.isEnabled = false
                }
                is UiState.Success -> {
                    progressBar.visibility = View.GONE
                    showToast("Evento atualizado!")
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

        viewModel.deleteState.observe(this) { state ->
            when (state) {
                is UiState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    btnDelete.isEnabled = false
                }
                is UiState.Success -> {
                    progressBar.visibility = View.GONE
                    showToast("Evento deletado!")
                    setResult(RESULT_OK)
                    finish()
                }
                is UiState.Error -> {
                    progressBar.visibility = View.GONE
                    btnDelete.isEnabled = true
                    showToast(state.message)
                }
                is UiState.Empty -> Unit
            }
        }
    }

    private fun onSaveClicked() {
        val title = etTitle.text.toString().trim()
        val description = etDescription.text.toString().trim()
        val date = etDate.text.toString().trim().toApiDate()
        val time = etTime.text.toString().trim()
        val location = etLocation.text.toString().trim()
        val checkedChipId = chipGroupCategory.checkedChipId
        val category = if (checkedChipId != View.NO_ID)
            chipGroupCategory.findViewById<Chip>(checkedChipId).text.toString()
        else ""
        val capacityText = etCapacity.text.toString().trim()
        val link = etLink.text.toString().trim().ifBlank { null }
        val isOnline = toggleFormat.checkedButtonId == R.id.btnOnline

        if (title.isBlank() || date.isBlank() || location.isBlank()) {
            showToast("Preencha título, data e local")
            return
        }

        val capacity = capacityText.toIntOrNull() ?: 0
        viewModel.updateEvent(eventId, title, description, date, time, location, category, isOnline, capacity, link)
    }

    private fun confirmDelete() {
        AlertDialog.Builder(this)
            .setTitle("Excluir evento")
            .setMessage("Deseja excluir este evento?")
            .setPositiveButton("Excluir") { _, _ -> viewModel.deleteEvent(eventId) }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
