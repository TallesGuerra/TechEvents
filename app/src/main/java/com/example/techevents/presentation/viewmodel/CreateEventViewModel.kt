package com.example.techevents.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.techevents.data.repository.Result
import com.example.techevents.domain.model.Event
import com.example.techevents.domain.usecase.CreateEventUseCase
import com.example.techevents.presentation.state.UiState
import kotlinx.coroutines.launch

class CreateEventViewModel(private val createEventUseCase: CreateEventUseCase) : ViewModel() {

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
            when (val result = createEventUseCase(
                title, description, date, time, location, category, isOnline, capacity, link
            )) {
                is Result.Success -> _createState.value = UiState.Success(result.data)
                is Result.Error -> _createState.value = UiState.Error(result.exception.message ?: "Erro ao criar evento")
            }
        }
    }

    class Factory(private val createEventUseCase: CreateEventUseCase) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CreateEventViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return CreateEventViewModel(createEventUseCase) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
