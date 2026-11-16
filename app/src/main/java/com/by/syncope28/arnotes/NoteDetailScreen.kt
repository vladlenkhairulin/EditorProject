package com.by.syncope28.arnotes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.by.syncope28.arnotes.ui.theme.AppSpacing

@Composable
fun NoteDetailScreen(
    note: Note?,
    result: SceneResult,
    onEdit: () -> Unit,
    onShare: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(AppSpacing.Large)
    ) {
        Text(
            text = stringResource(R.string.note_detail),
            style = MaterialTheme.typography.headlineSmall
        )
        Row {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back)
                )
            }
            if (note != null) {
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(R.string.edit)
                    )
                }
                IconButton(onClick = onShare) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = stringResource(R.string.share_note)
                    )
                }
            }
        }

        if (note != null) {
            Text(
                text = note.text,
                modifier = Modifier.verticalScroll(rememberScrollState()),
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            NoteStatus(result)
        }
    }
}

@Composable
internal fun NoteStatus(result: SceneResult) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (result) {
            SceneResult.Loading -> CircularProgressIndicator()
            is SceneResult.Error -> Text(result.message, color = MaterialTheme.colorScheme.error)
            else -> Text(stringResource(R.string.note_not_found))
        }
    }
}
