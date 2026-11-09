package com.by.syncope28.arnotes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SceneUiState(
    val sceneName: String = "",
    val draft: String = "",
    val editingIndex: Int? = null,
    val result: SceneResult = SceneResult.Loading,
    val userMessage: Int? = null
)

sealed interface SceneResult {
    object Loading : SceneResult
    data class Content(val notes: List<String>) : SceneResult
    object Empty : SceneResult
    data class Error(val message: String) : SceneResult
}

class SceneViewModel(
    private val savedStateHandle : SavedStateHandle
) : ViewModel() {
    companion object {
        const val DRAFT_KEY = "draft"
    }
    private val _uiState = MutableStateFlow(
        SceneUiState(
            draft  = savedStateHandle[DRAFT_KEY] ?: ""
        )
    )
    val uiState : StateFlow<SceneUiState> = _uiState.asStateFlow()
    fun loadScene(sceneName : String,
        notes : List<String>,
        errorMessage : String? = null
    ){
        if (_uiState.value.result != SceneResult.Loading) {
            return
        }
        viewModelScope.launch {
            delay(700)
            _uiState.update {
                it.copy(
                    sceneName = sceneName,
                    result = when {
                        errorMessage != null ->
                            SceneResult.Error(errorMessage)
                        notes.isEmpty() -> SceneResult.Empty
                        else ->
                            SceneResult.Content(notes)
                    }
                )
            }
        }
    }

    fun changeDraft(value : String) {
        savedStateHandle[DRAFT_KEY] = value
        _uiState.update{
            it.copy(draft = value)
        }
    }

    fun currentNotes() : List<String> {
        val result = _uiState.value.result
        return when (result) {
            is SceneResult.Content -> result.notes
            else -> emptyList()
        }
    }

    fun addNote() {
        val state = _uiState.value
        if (state.draft.isBlank()) {
            return
        }
        val notes = currentNotes() + state.draft
        savedStateHandle[DRAFT_KEY] = ""
        _uiState.update {
            it.copy (
                draft = "",
                result = SceneResult.Content(notes),
                userMessage = R.string.note_added
            )
        }
    }
    fun editNote(index : Int) {
        val notes = currentNotes()
        if (index !in notes.indices) {
            return
        }
        val text = notes[index]
        savedStateHandle[DRAFT_KEY] = text
        _uiState.update {
            it.copy (
                draft = text,
                editingIndex = index
            )
        }
    }
    fun saveNote() {
        val state = _uiState.value
        val index = state.editingIndex
        if (index == null || state.draft.isBlank()) {
            return
        }
        val notes = currentNotes().toMutableList()
        notes[index] = state.draft
        savedStateHandle[DRAFT_KEY] = ""
        _uiState.update {
            it.copy (
                draft = "",
                editingIndex = null,
                result = SceneResult.Content(notes),
                userMessage = R.string.note_changed
            )
        }
    }
    fun deleteNote(index: Int) {
        val notes = currentNotes().toMutableList()
        notes.removeAt(index)
        _uiState.update {
            it.copy (
                result = if (notes.isEmpty()) {
                    SceneResult.Empty
                } else {
                    SceneResult.Content(notes)
                },
                userMessage = R.string.note_deleted
            )
        }
    }
    fun messageShown() {
        _uiState.update {
            it.copy (
                userMessage = null
            )
        }
    }

}

