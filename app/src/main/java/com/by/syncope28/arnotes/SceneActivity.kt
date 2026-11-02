package com.by.syncope28.arnotes

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.by.syncope28.arnotes.ui.theme.ARNotesTheme
import androidx.core.content.edit
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.by.syncope28.arnotes.ui.theme.AppSpacing
import kotlinx.coroutines.delay
import androidx.compose.ui.tooling.preview.Preview


sealed interface SceneState {
    object Loading : SceneState
    object Success : SceneState
    data class Error(
        val message: String
    ) : SceneState
}

class SceneActivity : ComponentActivity() {
    private val sceneViewModel: SceneViewModel by viewModels()
    companion object {
        const val TAG = "ARNotes"
        const val NOTES = "notes"
        const val DRAFT = "draft"
    }

    private val notes = mutableListOf<String>()
    private var curSceneId = ""
    private var sceneName by mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        val requestedSceneState = openRequestedScene(intent)
        if (requestedSceneState == SceneState.Success) {
            val savedNotes = savedInstanceState?.getStringArrayList(NOTES)
            if (savedNotes != null) {
                notes.addAll(savedNotes)
            } else {
                loadScene()
            }
        }

        val errorMessage = if (requestedSceneState is SceneState.Error) {
            requestedSceneState.message
        } else {
            null
        }
        sceneViewModel.loadScene (
            sceneName = sceneName,
            notes = notes.toList(),
            errorMessage = errorMessage
        )

        val darkTheme = intent.getBooleanExtra("DARK_THEME", false)
        val dynamicColor = intent.getBooleanExtra("DYNAMIC_COLOR", false)
        setContent {
            ARNotesTheme (
                darkTheme = darkTheme,
                dynamicColor = dynamicColor
            ){
                SceneScreenContainer(viewModel = sceneViewModel)
            }
        }
    }

    private fun openRequestedScene(intent: Intent): SceneState {
        val link = intent.data
        if (link != null) {
            val sceneId = link.lastPathSegment
            if (sceneId != null && checkScene(sceneId)) {
                curSceneId = sceneId
                return SceneState.Success
            } else {
                return SceneState.Error(
                    getString(R.string.link_invalid)
                )
            }
        } else {
            val sceneId = intent.getStringExtra("SCENE_ID")
            val name = intent.getStringExtra("SCENE_NAME")
            if (sceneId != null && name != null) {
                curSceneId = sceneId
                sceneName = name
                saveSceneName()
                return SceneState.Success
            } else {
                return SceneState.Error(
                    getString(R.string.scene_not_found)
                )
            }
        }
    }

    private fun checkScene(sceneId : String) : Boolean {
        val storage = getSharedPreferences(
            "scene_$sceneId", MODE_PRIVATE
        )
        return storage.contains("SCENE_NAME")
    }

    private fun saveSceneName() {
        getSharedPreferences(
            "scene_$curSceneId", MODE_PRIVATE
        )
            .edit {
                putString(
                    "SCENE_NAME", sceneName
                )
            }
    }


    @Composable
    private fun SceneScreenContainer(
        viewModel : SceneViewModel
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
            onShare = {
                shareScene()
            },
            onBack = {
                finish()
            }
        )
    }

    private fun saveScene() {
        val state = sceneViewModel.uiState.value
        getSharedPreferences("scene_$curSceneId", MODE_PRIVATE)
            .edit {
                putString(
                    "SCENE_NAME", state.sceneName
                )
                putStringSet(
                    NOTES, sceneViewModel.currentNotes().toSet()
                )
            }
        Log.d(TAG, getString(R.string.scene_saved))
    }

    private fun loadScene() {
        val storage = getSharedPreferences("scene_$curSceneId", MODE_PRIVATE)
        sceneName = storage.getString(
            "SCENE_NAME", ""
        ) ?: ""
        val savedNotes = storage.getStringSet(
            NOTES, emptySet()
        )
        if (savedNotes != null) {
            notes.addAll(savedNotes)
        }
    }

    private fun shareScene() {
        val link = "https://arnotes.com/scene/$curSceneId"
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(
                Intent.EXTRA_TEXT,
                link
            )
        }
        startActivity(Intent.createChooser(shareIntent, "Поделиться сценой"))
    }


    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
    }

    override fun onStop() {
        super.onStop()
        if (curSceneId.isNotEmpty()) saveScene()
        Log.d(TAG, "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SceneScreen(
    state : SceneUiState,
    onDraftChange : (String) -> Unit,
    onAdd : () -> Unit,
    onEdit : (Int) -> Unit,
    onSave : () -> Unit,
    onDelete : (Int) -> Unit,
    onMessageShown : () -> Unit,
    onShare : () -> Unit,
    onBack : () -> Unit
) {
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val messageId = state.userMessage

    val message = if (messageId != null) {
        stringResource(messageId)
    } else {
        null
    }
    LaunchedEffect(state.userMessage) {
        if (message != null) {
            snackbarHostState.showSnackbar(message)
            onMessageShown()
        }
    }
    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
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
                        if (state.editingIndex != null) {
                            IconButton(
                                onClick = {
                                    onSave()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Save,
                                    contentDescription = stringResource(R.string.save_note),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
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
                    OutlinedTextField(
                        value = state.draft,
                        onValueChange = {
                            onDraftChange(it)
                        },
                        label = {
                            Text(stringResource(R.string.note_text))
                        },
                        shape = MaterialTheme.shapes.small,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(
                        modifier = Modifier.height(AppSpacing.Small)
                    )
                    if (state.editingIndex == null) {
                        Button(
                            onClick = {
                                onAdd()
                            },
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .fillMaxWidth(0.7f),
                            shape = MaterialTheme.shapes.large
                        ) {
                            Text(
                                text = stringResource(R.string.add_note),
                                style = MaterialTheme.typography.headlineMedium
                            )
                        }
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
                            modifier = Modifier.weight(1f)
                        ) {
                            itemsIndexed(notes) { index, note ->
                                Surface(
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
                                            text = note,
                                            modifier = Modifier.weight(1f),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        IconButton(
                                            onClick = {
                                                onEdit(index)
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = stringResource(R.string.edit),
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        IconButton(
                                            onClick = {
                                                onDelete(index)
                                            },
                                            enabled = state.editingIndex == null
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