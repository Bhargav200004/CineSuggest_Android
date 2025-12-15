package com.example.cinesuggest.utils


sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data class Success<out T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

// generic handler

fun <T> handleUiState(
    state: UiState<T>,
    onLoading: () -> Unit = { println("Loading...") }, // Optional default
    onError: (String) -> Unit = { msg -> println("Error: $msg") }, // Optional default
    onSuccess: (T) -> Unit
) {
    when (state) {
        is UiState.Loading -> onLoading()
        is UiState.Error -> onError(state.message)
        is UiState.Success -> onSuccess(state.data)
    }
}