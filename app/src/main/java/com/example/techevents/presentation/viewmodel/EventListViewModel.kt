package com.example.techevents.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.techevents.domain.model.Event
import com.example.techevents.domain.usecase.GetEventsUseCase
import com.example.techevents.presentation.state.UiState
import kotlinx.coroutines.launch

class EventListViewModel(private val getEventsUseCase: GetEventsUseCase) : ViewModel() {

    private val _events = MutableLiveData<UiState<List<Event>>>()
    val events: LiveData<UiState<List<Event>>> = _events

    private val accumulatedEvents = mutableListOf<Event>()
    private var currentPage = 1
    private var isLoading = false
    var canLoadMore = true
        private set

    private var currentQuery = ""
    private var currentCategory = ""
    private var currentIsOnline: Boolean? = null

    companion object {
        const val PAGE_SIZE = 20
    }

    init {
        loadEvents()
    }

    fun loadEvents(page: Int = 1) {
        if (isLoading) return
        isLoading = true
        currentPage = page

        if (page == 1) {
            accumulatedEvents.clear()
            _events.value = UiState.Loading
        }

        viewModelScope.launch {
            val result = getEventsUseCase(page, PAGE_SIZE, currentQuery, currentCategory, currentIsOnline)
            isLoading = false
            result
                .onSuccess { data ->
                    canLoadMore = data.size == PAGE_SIZE
                    accumulatedEvents.addAll(data)
                    _events.value = if (accumulatedEvents.isEmpty()) UiState.Empty
                    else UiState.Success(accumulatedEvents.toList())
                }
                .onFailure { e ->
                    _events.value = UiState.Error(e.message ?: "Erro desconhecido")
                }
        }
    }

    fun loadNextPage() {
        if (canLoadMore && !isLoading) loadEvents(currentPage + 1)
    }

    fun search(query: String) {
        currentQuery = query
        loadEvents()
    }

    fun filterByCategory(category: String) {
        currentCategory = category
        loadEvents()
    }

    fun filterByOnline(isOnline: Boolean?) {
        currentIsOnline = isOnline
        loadEvents()
    }

    class Factory(private val getEventsUseCase: GetEventsUseCase) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(EventListViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return EventListViewModel(getEventsUseCase) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
