package com.by.syncope28.arnotes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.by.syncope28.arnotes.ui.theme.AppSpacing

@Composable
fun NoteEditScreen(
    note: Note?,
    result: SceneResult,
    isNewNote: Boolean,
    draft: String,
    onDraftChange: (String) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit
) {
    val title = if (isNewNote) R.string.add_note else R.string.edit_note
    val canEdit = (result is SceneResult.Content || result == SceneResult.Empty) &&
        (isNewNote || note != null)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(AppSpacing.Large)
    ) {
        Text(
            text = stringResource(title),
            style = MaterialTheme.typography.headlineSmall
        )
        Row {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back)
                )
            }
            if (canEdit) {
                IconButton(onClick = onSave, enabled = draft.isNotBlank()) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = stringResource(R.string.save_note)
                    )
                }
            }
        }

        if (canEdit) {
            OutlinedTextField(
                value = draft,
                onValueChange = onDraftChange,
                label = { Text(stringResource(R.string.note_text)) },
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            NoteStatus(result)
        }
    }
}
