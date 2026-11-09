package com.by.syncope28.arnotes

sealed interface SceneResult {
    object Loading : SceneResult
    data class Content(val notes: List<String>) : SceneResult
    object Empty : SceneResult
    data class Error(val message: String) : SceneResult
}
