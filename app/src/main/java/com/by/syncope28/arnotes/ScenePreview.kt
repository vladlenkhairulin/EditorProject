package com.by.syncope28.arnotes

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.by.syncope28.arnotes.ui.theme.ARNotesTheme

@Preview(
    name = "Content"
)
@Composable
private fun SceneContentPreview() {
    ARNotesTheme {
        SceneScreen(
            state = SceneUiState(
                sceneName = "Моя сцена",
                result = SceneResult.Content(
                    listOf(
                        "Первая заметка",
                        "Вторая заметка"
                    )
                )
            ),
            onDraftChange = {},
            onAdd = {},
            onEdit = {},
            onSave = {},
            onDelete = {},
            onMessageShown = {},
            onShare = {},
            onBack = {}
        )
    }
}

@Preview(
    name = "Empty"
)
@Composable
private fun SceneEmptyPreview() {
    ARNotesTheme {
        SceneScreen(
            state = SceneUiState(
                sceneName = "Пустая сцена",
                result = SceneResult.Empty
            ),
            onDraftChange = {},
            onAdd = {},
            onEdit = {},
            onSave = {},
            onDelete = {},
            onMessageShown = {},
            onShare = {},
            onBack = {}
        )
    }
}

@Preview(
    name = "Error"
)
@Composable
private fun SceneErrorPreview() {
    ARNotesTheme {
        SceneScreen(
            state = SceneUiState(
                result = SceneResult.Error(
                    "Сцена не найдена"
                )
            ),
            onDraftChange = {},
            onAdd = {},
            onEdit = {},
            onSave = {},
            onDelete = {},
            onMessageShown = {},
            onShare = {},
            onBack = {}
        )
    }
}