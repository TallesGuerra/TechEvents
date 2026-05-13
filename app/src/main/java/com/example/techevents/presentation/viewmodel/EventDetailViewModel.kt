package com.example.techevents.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.techevents.domain.model.Event
import com.example.techevents.domain.usecase.GetEventDetailUseCase
import com.example.techevents.presentation.state.UiState
import kotlinx.coroutines.launch

class EventDetailViewModel(private val getEventDetailUseCase: GetEventDetailUseCase) : ViewModel() {

    private val _event = MutableLiveData<UiState<Event>>()
    val event: LiveData<UiState<Event>> = _event

    fun loadEvent(id: String) {
        _event.value = UiState.Loading
        viewModelScope.launch {
            getEventDetailUseCase(id)
                .onSuccess { _event.value = UiState.Success(it) }
                .onFailure { _event.value = UiState.Error(it.message ?: "Erro desconhecido") }
        }
    }

    class Factory(private val getEventDetailUseCase: GetEventDetailUseCase) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(EventDetailViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return EventDetailViewModel(getEventDetailUseCase) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
