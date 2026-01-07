package com.example.cinesuggest.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cinesuggest.data.local.UserPreferences
import com.example.cinesuggest.domain.repository.MovieRepository
import com.example.cinesuggest.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: MovieRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {


    private val _uiState = MutableStateFlow<UiState<HomeUiState>>(UiState.Loading)
    val uiState: StateFlow<UiState<HomeUiState>> = _uiState.asStateFlow()

    init {
        loadRecommendation()
    }

    private fun loadRecommendation() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            userPreferences.userId.collect { storeId ->
                val idToUse = storeId ?: 1

                try {
                    coroutineScope {
                        val recommendationsDeferred =
                            async { repository.getRecommendation(userId = idToUse) }
                        val allMoviesDeferred = async { repository.getAllMovies() }

                        val recommendationResult = recommendationsDeferred.await()
                        val allMoviesResult = allMoviesDeferred.await()

                        if (recommendationResult.isSuccess && allMoviesResult.isSuccess) {
                            _uiState.value = UiState.Success(
                                data = HomeUiState(
                                    recommendedMovies = recommendationResult.getOrThrow(),
                                    movies = allMoviesResult.getOrThrow()
                                )
                            )
                        }else{
                            _uiState.value = UiState.Error("Failed to fetch some data")
                        }
                    }
                } catch (e: Exception) {
                    _uiState.value = UiState.Error(e.message ?: "Unknown Error")
                }
            }
        }
    }
}
