package com.example.cinesuggest.ui.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cinesuggest.data.model.MovieDetail
import com.example.cinesuggest.data.repository.MovieRepository
import com.example.cinesuggest.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class MovieDetailUiDataHolder(
    val id: Int ,
    val title: String,
    val posterUrl: String,
    val releaseDate: String,
    val runtime: String,
    val genres: String,
    val revenue : Int,
    val originalLanguage: String,
    val overview: String,
    val userRating: Int?  = 1,
    val isFavorite: Boolean = false
)

sealed class MovieDetailUiEventHolder {
    data class OnFavouriteClick(val isFavorite: Boolean) : MovieDetailUiEventHolder()
    data class OnRatingChange(val rating : Int) : MovieDetailUiEventHolder()
}

fun MovieDetail.toMovieDetailUiDataHolder() : MovieDetailUiDataHolder {
    return MovieDetailUiDataHolder(
        id = this.id,
        title = this.title,
        posterUrl = this.posterUrl,
        releaseDate = this.releaseDate,
        runtime = this.runtime,
        genres = this.genres,
        revenue =  this.revenue,
        originalLanguage = this.originalLanguage,
        overview = this.overview,
    )
}

fun MovieDetailUiDataHolder.testFavourite() : MovieDetail {
    return MovieDetail(
        id = this.id,
        title = this.title,
        posterUrl = this.posterUrl,
        releaseDate = this.releaseDate,
        runtime = this.runtime,
        genres = this.genres,
        revenue =  this.revenue,
        originalLanguage = this.originalLanguage,
        overview = this.overview,
    )
}

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val repository: MovieRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val userId = 1
    private val movieId: Int = checkNotNull(savedStateHandle["movieId"]).toString().toInt()

    private val _uiState = MutableStateFlow<UiState<MovieDetailUiDataHolder>>(UiState.Loading)
    val uiState: StateFlow<UiState<MovieDetailUiDataHolder>> = _uiState.asStateFlow()

    init {
        loadMovieDetail()
    }

    fun onEvent(event : MovieDetailUiEventHolder){
        when(event){
            is MovieDetailUiEventHolder.OnFavouriteClick -> onFavoriteClicked(event.isFavorite)
            is MovieDetailUiEventHolder.OnRatingChange -> TODO()
        }

    }





    private fun loadMovieDetail() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.getMovieDetail(movieId)
                .onSuccess { movieDetail ->
                    val isFavourite : Boolean = repository.getFavouriteId(favoriteId = movieDetail.id)

                    _uiState.value = UiState.Success(movieDetail.toMovieDetailUiDataHolder().copy(isFavorite = isFavourite))
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
                        repository.removeFavoriteFromCache(movie.id)
                    } else {
                        repository.addFavoriteToCache(movie.testFavourite())
                    }
                    // 3. Update the UI state
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