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
import java.util.UUID

class SceneViewModel(
    private val savedStateHandle : SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        SceneUiState(
            draft = savedStateHandle[SceneKeys.DRAFT_KEY] ?: ""
        )
    )
    val uiState : StateFlow<SceneUiState> = _uiState.asStateFlow()
    fun loadScene(sceneName : String,
        notes : List<Note>,
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

    fun changeDraft(value: String) {
        savedStateHandle[SceneKeys.DRAFT_KEY] = value
        _uiState.update {
            it.copy(draft = value)
        }
    }

    fun currentNotes() : List<Note> {
        val result = _uiState.value.result
        return when (result) {
            is SceneResult.Content -> result.notes
            else -> emptyList()
        }
    }

    fun addNote() {
        val draft = _uiState.value.draft
        if (draft.isBlank()) {
            return
        }
        val note = Note(UUID.randomUUID().toString(), draft)
        val notes = currentNotes() + note
        _uiState.update {
            it.copy (
                result = SceneResult.Content(notes),
                userMessage = R.string.note_added
            )
        }
    }
    fun saveNote(noteId: String) {
        val draft = _uiState.value.draft
        if (draft.isBlank()) {
            return
        }
        val notes = currentNotes().toMutableList()
        val index = notes.indexOfFirst { it.id == noteId }
        if (index == -1) {
            return
        }
        notes[index] = notes[index].copy(text = draft)
        _uiState.update {
            it.copy (
                result = SceneResult.Content(notes),
                userMessage = R.string.note_changed
            )
        }
    }
    fun deleteNote(noteId: String) {
        val current = currentNotes()
        val notes = current.filterNot { it.id == noteId }
        if (notes.size == current.size) {
            return
        }
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
