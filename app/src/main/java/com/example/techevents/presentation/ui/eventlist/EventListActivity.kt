package com.example.techevents.presentation.ui.eventlist

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.techevents.R
import com.example.techevents.data.api.RetrofitClient
import com.example.techevents.data.repository.EventRepositoryImpl
import com.example.techevents.domain.usecase.GetEventsUseCase
import com.example.techevents.presentation.state.UiState
import com.example.techevents.presentation.ui.eventdetail.EventDetailActivity
import com.example.techevents.presentation.viewmodel.EventListViewModel

class EventListActivity : AppCompatActivity() {

    private lateinit var viewModel: EventListViewModel
    private lateinit var adapter: EventAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvError: TextView
    private lateinit var tvEmpty: TextView
    private lateinit var searchView: SearchView
    private lateinit var btnFilterOnline: Button
    private lateinit var btnFilterPresential: Button
    private lateinit var btnFilterAll: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_list)

        setupViews()
        setupViewModel()
        setupRecyclerView()
        setupSearchView()
        setupFilters()
        observeEvents()
    }

    private fun setupViews() {
        recyclerView = findViewById(R.id.recyclerView)
        progressBar = findViewById(R.id.progressBar)
        tvError = findViewById(R.id.tvError)
        tvEmpty = findViewById(R.id.tvEmpty)
        searchView = findViewById(R.id.searchView)
        btnFilterOnline = findViewById(R.id.btnFilterOnline)
        btnFilterPresential = findViewById(R.id.btnFilterPresential)
        btnFilterAll = findViewById(R.id.btnFilterAll)
    }

    private fun setupViewModel() {
        val repository = EventRepositoryImpl(RetrofitClient.api)
        val factory = EventListViewModel.Factory(GetEventsUseCase(repository))
        viewModel = ViewModelProvider(this, factory)[EventListViewModel::class.java]
    }

    private fun setupRecyclerView() {
        adapter = EventAdapter { event ->
            val intent = Intent(this, EventDetailActivity::class.java)
            intent.putExtra(EventDetailActivity.EXTRA_EVENT_ID, event.id)
            startActivity(intent)
        }
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val lastVisible = layoutManager.findLastVisibleItemPosition()
                val total = layoutManager.itemCount
                if (dy > 0 && lastVisible >= total - 3) viewModel.loadNextPage()
            }
        })
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.search(query ?: "")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrBlank()) viewModel.search("")
                return true
            }
        })
    }

    private fun setupFilters() {
        btnFilterAll.setOnClickListener { viewModel.filterByOnline(null) }
        btnFilterOnline.setOnClickListener { viewModel.filterByOnline(true) }
        btnFilterPresential.setOnClickListener { viewModel.filterByOnline(false) }
    }

    private fun observeEvents() {
        viewModel.events.observe(this) { state ->
            progressBar.visibility = View.GONE
            tvError.visibility = View.GONE
            tvEmpty.visibility = View.GONE
            recyclerView.visibility = View.GONE

            when (state) {
                is UiState.Loading -> {
                    if (adapter.currentList.isEmpty()) progressBar.visibility = View.VISIBLE
                }
                is UiState.Success -> {
                    recyclerView.visibility = View.VISIBLE
                    adapter.submitList(state.data)
                }
                is UiState.Error -> {
                    tvError.visibility = View.VISIBLE
                    tvError.text = state.message
                }
                is UiState.Empty -> tvEmpty.visibility = View.VISIBLE
            }
        }
    }
}
