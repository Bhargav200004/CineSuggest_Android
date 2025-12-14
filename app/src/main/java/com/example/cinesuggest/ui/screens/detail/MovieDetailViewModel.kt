package com.example.cinesuggest.ui.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cinesuggest.data.model.MovieDetail
import com.example.cinesuggest.data.repository.MovieRepository
import com.example.cinesuggest.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import javax.inject.Inject


data class MovieDetailHolder(
    val id: Int,
    val title: String,
    val posterUrl: String,
    val releaseDate: String,
    val runtime: String,
    val genres: String, // API provides a parsed list
    val revenue : Int,
    val originalLanguage: String,
    val overview: String,
    val userRating: Int?  = 1,
    val isFavorite: Boolean? = false
)

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val repository: MovieRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val userId = 1
    private val movieId: Int = checkNotNull(savedStateHandle["movieId"]).toString().toInt()

    private val _uiState = MutableStateFlow<UiState<MovieDetailHolder>>(UiState.Loading)
    val uiState: StateFlow<UiState<MovieDetailHolder>> = _uiState.asStateFlow()

    // Creating Object Mapper

    init {
        loadMovieDetail()
    }

    private fun loadMovieDetail() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.getMovieDetail(movieId)
                .onSuccess { detail ->
                    _uiState.value = UiState.Success(detail)
                }
                .onFailure {
                    _uiState.value = UiState.Error(it.message ?: "Unknown error")
                }
        }
    }

    fun onFavoriteClicked() {
        val currentState = _uiState.value
        if (currentState !is UiState.Success) return

        val movie = currentState.data
        val isCurrentlyFavorite = false

        viewModelScope.launch {
            // 1. Make the API call
            repository.toggleFavorite(userId, movie.id)
                .onSuccess {
                    // 2. On success, update the local cache
                    if (isCurrentlyFavorite) {
                        repository.removeFavoriteFromCache(movie.id)
                    } else {
                        repository.addFavoriteToCache(movie)
                    }
                    // 3. Update the UI state
                    _uiState.update {
                        isCurrentlyFavorite?.let { it1 ->
                            (it as UiState.Success).copy(
                                data = movie.copy()
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
        if (currentState !is UiState.Success) return

        viewModelScope.launch {
            repository.rateMovie(userId, movieId, rating)
                .onSuccess {
                    _uiState.update {
                        (it as UiState.Success).copy(
                            data = currentState.data.copy(userRating = rating)
                        )
                    }
                }
                .onFailure {
                    // Handle rating error
                }
        }
    }
}