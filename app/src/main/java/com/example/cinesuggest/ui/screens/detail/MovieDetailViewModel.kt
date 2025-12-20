package com.example.cinesuggest.ui.screens.detail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cinesuggest.data.mapper.toDomain
import com.example.cinesuggest.data.mapper.toUiState
import com.example.cinesuggest.domain.repository.MovieRepository
import com.example.cinesuggest.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val repository: MovieRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val userId = 1
    private val movieId: Int = checkNotNull(savedStateHandle["movieId"]).toString().toInt()

    private val _uiState = MutableStateFlow<UiState<MovieDetailUiState>>(UiState.Loading)
    val uiState: StateFlow<UiState<MovieDetailUiState>> = _uiState.asStateFlow()


    init {
        loadMovieDetail()
    }

    fun onEvent(event : MovieDetailUiEvent){
        when(event){
            is MovieDetailUiEvent.OnFavoriteClick -> onFavoriteClicked(event.isFavorite)
            is MovieDetailUiEvent.OnRatingChange -> onRatingChanged(event.rating)
        }

    }


    private fun loadMovieDetail() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.getMovieDetail(
                movieId,
                userId = userId
            )
                .onSuccess { movieDetail ->
                    val isFavorite : Boolean = repository.isFavoriteCheck(favoriteMovieId = movieDetail.id)

                    _uiState.value = UiState.Success(movieDetail.toUiState().copy(isFavorite = isFavorite))
                }
                .onFailure {
                    _uiState.value = UiState.Error(it.message ?: "Unknown error")
                }
        }
    }

    fun onFavoriteClicked(isFavorite: Boolean) {
        val currentState = _uiState.value
        if (currentState !is UiState.Success) return

        val movie = currentState.data

        viewModelScope.launch {
            // 1. Make the API call
            repository.toggleFavorite(userId, movie.id)
                .onSuccess {
                    // 2. On success, update the local cache
                    if (isFavorite) {
                        repository.removeFavorite(movie.id)
                    } else {
                        repository.addFavorite(movie.toDomain())
                    }
                    _uiState.update {
                            (it as UiState.Success).copy(
                                data = movie.copy(
                                    isFavorite = !isFavorite
                                )
                            )

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