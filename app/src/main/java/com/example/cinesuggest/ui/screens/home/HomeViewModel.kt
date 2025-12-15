package com.example.cinesuggest.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cinesuggest.data.remote.dto.MovieDto
import com.example.cinesuggest.domain.model.Movie
import com.example.cinesuggest.domain.repository.MovieRepository
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

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState : StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadRecommendation()
    }

    private fun loadRecommendation() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            repository.getRecommendation(userId = userId)
                .onSuccess {response ->
                    _uiState.value = HomeUiState.Success(response)
                }
                .onFailure {
                    _uiState.value = HomeUiState.Error(it.message ?: "Unknown Error")
                }

        }
    }
}

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(val movies : List<Movie>) : HomeUiState
    data class Error(val message : String) : HomeUiState
}