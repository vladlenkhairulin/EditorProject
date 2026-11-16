package com.by.syncope28.arnotes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.by.syncope28.arnotes.ui.theme.AppSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SceneScreen(
    state : SceneUiState,
    onAdd : () -> Unit,
    onOpenNote : (String) -> Unit,
    onDelete : (String) -> Unit,
    onShare : () -> Unit,
    onBack : () -> Unit
) {
    val showAddButton = state.result is SceneResult.Content || state.result == SceneResult.Empty
    Scaffold(
        floatingActionButton = {
            if (showAddButton) {
                FloatingActionButton(
                    onClick = {
                        onAdd()
                    },
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.add_note)
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = state.sceneName,
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                actions = {
                    if (state.result is SceneResult.Content || state.result == SceneResult.Empty) {
                        IconButton(
                            onClick = {
                                onShare()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = stringResource(R.string.share_scene),
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(AppSpacing.Large)
        ) {
            when (val result = state.result) {
                SceneResult.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(
                                modifier = Modifier.height(AppSpacing.Medium)
                            )
                            Text(
                                text = stringResource(R.string.loading_scene),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
                is SceneResult.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(
                                modifier = Modifier.height(AppSpacing.Medium)
                            )
                            Text(
                                text = result.message,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(
                                modifier = Modifier.height(AppSpacing.Medium)
                            )
                            Button(
                                onClick = {
                                    onBack()
                                }
                            ) {
                                Text(stringResource(R.string.back))
                            }
                        }
                    }
                }
                else -> {
                    val notes = if (result is SceneResult.Content) {
                        result.notes
                    } else {
                        emptyList()
                    }
                    if (notes.isEmpty()) {
                        Spacer(
                            modifier = Modifier.height(AppSpacing.Large)
                        )
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(R.string.no_notes),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(
                                modifier = Modifier.height(AppSpacing.Small)
                            )
                            Text(
                                text = stringResource(R.string.add_first_note),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                    else {
                        Spacer(
                            modifier = Modifier.height(AppSpacing.Medium)
                        )

                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(
                                bottom = if (showAddButton) 80.dp else 0.dp
                            )
                        ) {
                            items(notes, key = { note -> note.id }) { note ->
                                Surface(
                                    onClick = {
                                        onOpenNote(note.id)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = AppSpacing.Small),
                                    shape = MaterialTheme.shapes.medium,
                                    color = MaterialTheme.colorScheme.surfaceContainer
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(AppSpacing.Medium),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = note.text,
                                            modifier = Modifier.weight(1f),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        IconButton(
                                            onClick = {
                                                onDelete(note.id)
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = stringResource(R.string.delete),
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}