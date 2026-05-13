package com.example.techevents.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.techevents.domain.model.Event
import com.example.techevents.domain.repository.EventRepository
import com.example.techevents.presentation.state.UiState
import kotlinx.coroutines.launch

class CreateEventViewModel(private val repository: EventRepository) : ViewModel() {

    private val _createState = MutableLiveData<UiState<Event>>()
    val createState: LiveData<UiState<Event>> = _createState

    fun createEvent(
        title: String,
        description: String,
        date: String,
        time: String,
        location: String,
        category: String,
        isOnline: Boolean,
        capacity: Int,
        link: String?
    ) {
        _createState.value = UiState.Loading
        viewModelScope.launch {
            repository.createEvent(title, description, date, time, location, category, isOnline, capacity, link)
                .onSuccess { _createState.value = UiState.Success(it) }
                .onFailure { _createState.value = UiState.Error(it.message ?: "Erro ao criar evento") }
        }
    }

    class Factory(private val repository: EventRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CreateEventViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return CreateEventViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
