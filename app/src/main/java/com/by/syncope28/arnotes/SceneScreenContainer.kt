package com.by.syncope28.arnotes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SceneScreenContainer(
    viewModel: SceneViewModel,
    onShare: () -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    SceneScreen (
        state = state,
        onDraftChange = viewModel::changeDraft,
        onAdd = viewModel::addNote,
        onEdit = viewModel::editNote,
        onSave = viewModel::saveNote,
        onDelete = viewModel::deleteNote,
        onMessageShown = viewModel::messageShown,
        onShare = onShare,
        onBack = onBack
    )
}
