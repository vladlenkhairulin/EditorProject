package com.by.syncope28.arnotes

sealed interface SceneState {
    object Loading : SceneState
    object Success : SceneState
    data class Error(
        val message: String
    ) : SceneState
}
