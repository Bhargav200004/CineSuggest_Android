package com.example.cinesuggest.ui.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cinesuggest.data.model.MovieDetail
import com.example.cinesuggest.data.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val repository: MovieRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val userId = 1
    private val movieId: Int = checkNotNull(savedStateHandle["movieId"]).toString().toInt()

    private val _uiState = MutableStateFlow<MovieDetailUiState>(MovieDetailUiState.Loading)
    val uiState: StateFlow<MovieDetailUiState> = _uiState.asStateFlow()

    init {
        loadMovieDetail()
    }

    private fun loadMovieDetail() {
        viewModelScope.launch {
            _uiState.value = MovieDetailUiState.Loading
            repository.getMovieDetail(movieId)
                .onSuccess { detail ->
                    _uiState.value = MovieDetailUiState.Success(detail)
                }
                .onFailure {
                    _uiState.value = MovieDetailUiState.Error(it.message ?: "Unknown error")
                }
        }
    }

    fun onFavoriteClicked() {
        val currentState = _uiState.value
        if (currentState !is MovieDetailUiState.Success) return

        val movie = currentState.movieDetail
        val isCurrentlyFavorite = movie.isFavorite

        viewModelScope.launch {
            // 1. Make the API call
            repository.toggleFavorite(userId, movie.id)
                .onSuccess {
                    // 2. On success, update the local cache
                    if (isCurrentlyFavorite == true) {
                        repository.removeFavoriteFromCache(movie.id)
                    } else {
                        repository.addFavoriteToCache(movie)
                    }
                    // 3. Update the UI state
                    _uiState.update {
                        isCurrentlyFavorite?.let { it1 ->
                            (it as MovieDetailUiState.Success).copy(
                                movieDetail = movie.copy(isFavorite = !it1)
                            )
                        }!!
                    }
                }
                .onFailure {
                    // Handle error (e.g., show a toast)
                }
        }
    }

    fun onRatingChanged(rating: Int) {
        val currentState = _uiState.value
        if (currentState !is MovieDetailUiState.Success) return

        viewModelScope.launch {
            repository.rateMovie(userId, movieId, rating)
                .onSuccess {
                    _uiState.update {
                        (it as MovieDetailUiState.Success).copy(
                            movieDetail = currentState.movieDetail.copy(userRating = rating)
                        )
                    }
                }
                .onFailure {
                    // Handle rating error
                }
        }
    }
}

sealed interface MovieDetailUiState {
    object Loading : MovieDetailUiState
    data class Success(val movieDetail: MovieDetail) : MovieDetailUiState
    data class Error(val message: String) : MovieDetailUiState
}