package com.by.syncope28.arnotes

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.by.syncope28.arnotes.ui.theme.ARNotesTheme
import androidx.core.content.edit
import com.by.syncope28.arnotes.ui.theme.AppSpacing
import kotlinx.coroutines.delay

class SceneActivity : ComponentActivity() {
    companion object {
        const val TAG = "ARNotes"
        const val NOTES = "notes"
        const val DRAFT = "draft"
    }

    private val notes = mutableStateListOf<String>()
    private var curSceneId = ""
    private var sceneName by mutableStateOf("")
    private var draft by mutableStateOf("")
    private var msg by mutableStateOf("")
    private var sceneExists by mutableStateOf(false)
    private var isLoading by mutableStateOf(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        openRequestedScene(intent)
        if (sceneExists) {
            val savedNotes = savedInstanceState?.getStringArrayList(NOTES)
            if (savedNotes != null) notes.addAll(savedNotes) else loadScene()
            draft = savedInstanceState?.getString(DRAFT) ?: ""
        }
        setContent {
            ARNotesTheme {
                SceneScreen()
            }
        }
    }

    private fun openRequestedScene(intent: Intent) {
        val link = intent.data
        if (link != null) {
            val sceneId = link.lastPathSegment
            if (sceneId != null && checkScene(sceneId)) {
                curSceneId = sceneId
                sceneExists = true
                msg = getString(R.string.scene_opened_via_link)
            } else {
                sceneExists = false
                msg = getString(R.string.link_invalid)
            }
        } else {
            val sceneId = intent.getStringExtra("SCENE_ID")
            val name = intent.getStringExtra("SCENE_NAME")
            if (sceneId != null && name != null) {
                curSceneId = sceneId
                sceneName = name
                sceneExists = true
                saveSceneName()
                msg = getString(R.string.my_notes)
            } else {
                sceneExists = false
                msg = getString(R.string.scene_not_found)
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
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun SceneScreen() {
        LaunchedEffect(Unit) {
            delay(700)
            isLoading = false
        }
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = if (sceneExists) sceneName else "",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    },
                    actions = {
                        if (sceneExists && !isLoading) {
                            IconButton(
                                onClick = {
                                    saveScene()
                                    msg = getString(R.string.scene_saved)
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Save,
                                    contentDescription = stringResource(R.string.save_scene),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            IconButton(
                                onClick = {
                                    shareScene()
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
                when {
                    isLoading -> {
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
                    !sceneExists -> {
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
                                    text = stringResource(R.string.cannot_load_scene),
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(
                                    modifier = Modifier.height(AppSpacing.Medium)
                                )
                                Button(
                                    onClick = {
                                        finish()
                                    }
                                ) {
                                    Text(stringResource(R.string.back))
                                }
                            }
                        }
                    }
                    else -> {
                        OutlinedTextField(
                            value = draft,
                            onValueChange = {
                                draft = it
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
                        Button(
                            onClick = {
                                if (draft.isNotBlank()) {
                                    notes.add(draft)
                                    draft = ""
                                }
                            },
                            shape = MaterialTheme.shapes.large
                        ) {
                            Text(stringResource(R.string.add_note))
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
                                                    notes.removeAt(index)
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



    private fun saveScene() {
        getSharedPreferences("scene_$curSceneId", MODE_PRIVATE)
            .edit {
                putString(
                    "SCENE_NAME", sceneName
                )
                putStringSet(
                    NOTES, notes.toSet()
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

    override fun onSaveInstanceState(outState: Bundle) {
        if (sceneExists) {
            outState.putStringArrayList(NOTES, ArrayList(notes))
            outState.putString(DRAFT, draft)
        }
        super.onSaveInstanceState(outState)
        Log.d(TAG, "onSaveInstanceState")
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
        if (sceneExists) saveScene()
        Log.d(TAG, "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }
}