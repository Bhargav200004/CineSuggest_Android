package com.example.cinesuggest.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cinesuggest.data.remote.dto.MovieDto
import com.example.cinesuggest.domain.model.Movie
import com.example.cinesuggest.domain.repository.MovieRepository
import com.example.cinesuggest.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository : MovieRepository
) : ViewModel(){

    private val userId = 1

    private val _uiState = MutableStateFlow<UiState<HomeUiState>>(UiState.Loading)
    val uiState : StateFlow<UiState<HomeUiState>> = _uiState.asStateFlow()

    init {
        loadRecommendation()
    }

    private fun loadRecommendation() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.getRecommendation(userId = userId)
                .onSuccess {movies ->
                    _uiState.value = UiState.Success(data = HomeUiState(movies = movies))
                }
                .onFailure {
                    _uiState.value = UiState.Error(it.message ?: "Unknown Error")
                }
        }
    }
}