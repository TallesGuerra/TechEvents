package com.example.techevents.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.techevents.data.repository.Result
import com.example.techevents.domain.model.Event
import com.example.techevents.domain.repository.EventRepository
import com.example.techevents.presentation.state.UiState
import kotlinx.coroutines.launch

class EventDetailViewModel(private val repository: EventRepository) : ViewModel() {

    private val _event = MutableLiveData<UiState<Event>>()
    val event: LiveData<UiState<Event>> = _event

    fun loadEvent(id: String) {
        _event.value = UiState.Loading
        viewModelScope.launch {
            when (val result = repository.getEventById(id)) {
                is Result.Success -> _event.value = UiState.Success(result.data)
                is Result.Error -> _event.value = UiState.Error(result.exception.message ?: "Erro desconhecido")
            }
        }
    }

    class Factory(private val repository: EventRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(EventDetailViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return EventDetailViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
