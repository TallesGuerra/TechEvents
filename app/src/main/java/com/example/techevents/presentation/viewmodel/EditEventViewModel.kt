package com.example.techevents.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.techevents.domain.model.Event
import com.example.techevents.domain.usecase.DeleteEventUseCase
import com.example.techevents.domain.usecase.GetEventDetailUseCase
import com.example.techevents.domain.usecase.UpdateEventUseCase
import com.example.techevents.presentation.state.UiState
import kotlinx.coroutines.launch

class EditEventViewModel(
    private val getEventDetailUseCase: GetEventDetailUseCase,
    private val updateEventUseCase: UpdateEventUseCase,
    private val deleteEventUseCase: DeleteEventUseCase
) : ViewModel() {

    private val _event = MutableLiveData<UiState<Event>>()
    val event: LiveData<UiState<Event>> = _event

    private val _updateState = MutableLiveData<UiState<Event>>()
    val updateState: LiveData<UiState<Event>> = _updateState

    private val _deleteState = MutableLiveData<UiState<Unit>>()
    val deleteState: LiveData<UiState<Unit>> = _deleteState

    fun loadEvent(id: String) {
        _event.value = UiState.Loading
        viewModelScope.launch {
            getEventDetailUseCase(id)
                .onSuccess { event -> _event.value = UiState.Success(event) }
                .onFailure { error -> _event.value = UiState.Error(error.message ?: "Erro ao carregar evento") }
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
            updateEventUseCase(id, title, description, date, time, location, category, isOnline, capacity, link)
                .onSuccess { event -> _updateState.value = UiState.Success(event) }
                .onFailure { error -> _updateState.value = UiState.Error(error.message ?: "Erro ao atualizar evento") }
        }
    }

    fun deleteEvent(id: String) {
        _deleteState.value = UiState.Loading
        viewModelScope.launch {
            deleteEventUseCase(id)
                .onSuccess { _deleteState.value = UiState.Success(Unit) }
                .onFailure { error -> _deleteState.value = UiState.Error(error.message ?: "Erro ao deletar evento") }
        }
    }

    class Factory(
        private val getEventDetailUseCase: GetEventDetailUseCase,
        private val updateEventUseCase: UpdateEventUseCase,
        private val deleteEventUseCase: DeleteEventUseCase
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(EditEventViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return EditEventViewModel(getEventDetailUseCase, updateEventUseCase, deleteEventUseCase) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
