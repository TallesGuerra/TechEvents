package com.example.techevents.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.techevents.data.repository.Result
import com.example.techevents.domain.model.Event
import com.example.techevents.domain.usecase.GetEventsUseCase
import com.example.techevents.presentation.state.UiState
import kotlinx.coroutines.launch

class EventListViewModel(private val getEventsUseCase: GetEventsUseCase) : ViewModel() {

    private val _events = MutableLiveData<UiState<List<Event>>>()
    val events: LiveData<UiState<List<Event>>> = _events

    private var currentQuery = ""
    private var currentCategory = ""
    private var currentIsOnline: Boolean? = null

    init {
        loadEvents()
    }

    fun loadEvents(page: Int = 1) {
        _events.value = UiState.Loading
        viewModelScope.launch {
            when (val result = getEventsUseCase(page, 20, currentQuery, currentCategory, currentIsOnline)) {
                is Result.Success -> {
                    if (result.data.isEmpty()) {
                        _events.value = UiState.Empty
                    } else {
                        _events.value = UiState.Success(result.data)
                    }
                }
                is Result.Error -> {
                    _events.value = UiState.Error(result.exception.message ?: "Erro desconhecido")
                }
            }
        }
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
