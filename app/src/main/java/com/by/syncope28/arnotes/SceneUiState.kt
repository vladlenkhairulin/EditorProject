package com.by.syncope28.arnotes

data class SceneUiState(
    val sceneName: String = "",
    val draft: String = "",
    val editingIndex: Int? = null,
    val result: SceneResult = SceneResult.Loading,
    val userMessage: Int? = null
)
