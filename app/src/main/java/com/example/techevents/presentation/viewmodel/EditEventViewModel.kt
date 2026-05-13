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

class EditEventViewModel(private val repository: EventRepository) : ViewModel() {

    private val _event = MutableLiveData<UiState<Event>>()
    val event: LiveData<UiState<Event>> = _event

    private val _updateState = MutableLiveData<UiState<Event>>()
    val updateState: LiveData<UiState<Event>> = _updateState

    private val _deleteState = MutableLiveData<UiState<Unit>>()
    val deleteState: LiveData<UiState<Unit>> = _deleteState

    fun loadEvent(id: String) {
        _event.value = UiState.Loading
        viewModelScope.launch {
            repository.getEventById(id)
                .onSuccess { _event.value = UiState.Success(it) }
                .onFailure { _event.value = UiState.Error(it.message ?: "Erro ao carregar evento") }
        }
    }

    fun updateEvent(
        id: String,
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
        _updateState.value = UiState.Loading
        viewModelScope.launch {
            repository.updateEvent(id, title, description, date, time, location, category, isOnline, capacity, link)
                .onSuccess { _updateState.value = UiState.Success(it) }
                .onFailure { _updateState.value = UiState.Error(it.message ?: "Erro ao atualizar evento") }
        }
    }

    fun deleteEvent(id: String) {
        _deleteState.value = UiState.Loading
        viewModelScope.launch {
            repository.deleteEvent(id)
                .onSuccess { _deleteState.value = UiState.Success(Unit) }
                .onFailure { _deleteState.value = UiState.Error(it.message ?: "Erro ao deletar evento") }
        }
    }

    class Factory(private val repository: EventRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(EditEventViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return EditEventViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
