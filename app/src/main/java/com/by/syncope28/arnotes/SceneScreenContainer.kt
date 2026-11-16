package com.by.syncope28.arnotes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun SceneScreenContainer(
    viewModel: SceneViewModel,
    initialBackStack: List<NavKey>,
    onShare: () -> Unit,
    onShareNote: (String) -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val backStack = rememberNavBackStack(*initialBackStack.toTypedArray())
    val adaptiveInfo = currentWindowAdaptiveInfoV2()
    val strategy = rememberListDetailSceneStrategy<NavKey>(
        directive = calculatePaneScaffoldDirective(adaptiveInfo)
    )

    val popBackStack: () -> Unit = {
        if (backStack.size > 1) {
            if (backStack.removeLastOrNull() is NoteEdit) {
                viewModel.changeDraft("")
            }
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val messageId = state.userMessage
    val message = if (messageId != null) stringResource(messageId) else null
    LaunchedEffect(messageId) {
        if (message != null) {
            snackbarHostState.showSnackbar(message)
            viewModel.messageShown()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        NavDisplay(
            backStack = backStack,
            onBack = popBackStack,
            sceneStrategies = listOf(strategy),
            entryProvider = entryProvider {
                entry<NoteList>(
                    metadata = ListDetailSceneStrategy.listPane()
                ) {
                    SceneScreen(
                        state = state,
                        onAdd = {
                            if (backStack.lastOrNull() !is NoteEdit) {
                                viewModel.changeDraft("")
                                backStack.add(NoteEdit())
                            }
                        },
                        onOpenNote = { noteId ->
                            val detail = NoteDetail(noteId)
                            if (backStack.lastOrNull() != detail &&
                                backStack.lastOrNull() !is NoteEdit
                            ) {
                                if (backStack.lastOrNull() is NoteDetail) {
                                    backStack.removeLastOrNull()
                                }
                                backStack.add(detail)
                            }
                        },
                        onDelete = { noteId ->
                            viewModel.deleteNote(noteId)
                            if (backStack.lastOrNull() == NoteDetail(noteId)) {
                                backStack.removeLastOrNull()
                            }
                        },
                        onShare = onShare,
                        onBack = onBack
                    )
                }
                entry<NoteDetail>(metadata = ListDetailSceneStrategy.detailPane()) { route ->
                    val result = state.result
                    val note = (result as? SceneResult.Content)?.notes
                        ?.firstOrNull { it.id == route.noteId }
                    NoteDetailScreen(
                        note = note,
                        result = result,
                        onShare = {
                            onShareNote(route.noteId)
                        },
                        onEdit = {
                            if (note != null && backStack.lastOrNull() == route) {
                                viewModel.changeDraft(note.text)
                                backStack.add(NoteEdit(route.noteId))
                            }
                        },
                        onBack = {
                            if (backStack.lastOrNull() == route) {
                                popBackStack()
                            }
                        }
                    )
                }
                entry<NoteEdit> { route ->
                    val result = state.result
                    val note = (result as? SceneResult.Content)?.notes
                        ?.firstOrNull { it.id == route.noteId }
                    NoteEditScreen(
                        note = note,
                        result = result,
                        isNewNote = route.noteId == null,
                        draft = state.draft,
                        onDraftChange = viewModel::changeDraft,
                        onSave = {
                            if (backStack.lastOrNull() == route) {
                                val noteId = route.noteId
                                if (noteId == null) {
                                    viewModel.addNote()
                                } else {
                                    viewModel.saveNote(noteId)
                                }
                                popBackStack()
                            }
                        },
                        onBack = {
                            if (backStack.lastOrNull() == route) {
                                popBackStack()
                            }
                        }
                    )
                }
            }
        )
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
        )
    }
}
