package com.by.syncope28.arnotes

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import androidx.navigation3.runtime.deeplink.DeepLinkRequest
import androidx.navigation3.runtime.deeplink.DeepLinkUri
import androidx.navigation3.runtime.deeplink.UriDeepLinkMatcher
import androidx.navigation3.runtime.deeplink.withBackStack
import com.by.syncope28.arnotes.ui.theme.ARNotesTheme
import java.util.UUID
import kotlinx.serialization.serializer

class SceneActivity : ComponentActivity() {
    private val sceneViewModel: SceneViewModel by viewModels()
    companion object {
        const val TAG = "ARNotes"
        private const val LOG_ON_CREATE = "onCreate"
        private const val LOG_ON_START = "onStart"
        private const val LOG_ON_RESUME = "onResume"
        private const val LOG_ON_PAUSE = "onPause"
        private const val LOG_ON_STOP = "onStop"
        private const val LOG_ON_DESTROY = "onDestroy"
    }

    private val noteLinkMatcher = UriDeepLinkMatcher(
        DeepLinkUri(SceneKeys.NOTE_LINK_PATTERN),
        serializer<NoteLink>()
    ).withBackStack { match ->
        listOf(NoteList, NoteDetail(match.key.noteId))
    }

    private val notes = mutableListOf<Note>()
    private var curSceneId = ""
    private var sceneName by mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, LOG_ON_CREATE)
        val noteLink = intent.data?.takeIf { intent.action == Intent.ACTION_VIEW }?.let { link ->
            noteLinkMatcher.match(DeepLinkRequest(uri = DeepLinkUri(link.toString())))
        }
        val requestedSceneState = try {
            openRequestedScene(intent, noteLink?.key?.sceneId)
        } catch (error: Exception) {
            Log.e(TAG, "openRequestedScene", error)
            SceneState.Error(getString(R.string.cannot_load_scene))
        }
        val errorMessage = when (requestedSceneState) {
            SceneState.Success -> {
                try {
                    loadScene()
                    null
                } catch (error: Exception) {
                    Log.e(TAG, "loadScene", error)
                    getString(R.string.cannot_load_scene)
                }
            }
            is SceneState.Error -> requestedSceneState.message
            SceneState.Loading -> null
        }
        val initialBackStack = if (errorMessage == null && noteLink != null) {
            noteLink.backStack
        } else {
            listOf(NoteList)
        }
        sceneViewModel.loadScene (
            sceneName = sceneName,
            notes = notes.toList(),
            errorMessage = errorMessage
        )

        val appearance = getSharedPreferences(SceneKeys.PREF_APPEARANCE, MODE_PRIVATE)
        setContent {
            val darkTheme = intent.getBooleanExtra(
                SceneKeys.EXTRA_DARK_THEME,
                appearance.getBoolean(SceneKeys.PREF_DARK_THEME, isSystemInDarkTheme())
            )
            val dynamicColor = intent.getBooleanExtra(
                SceneKeys.EXTRA_DYNAMIC_COLOR,
                appearance.getBoolean(SceneKeys.PREF_DYNAMIC_COLOR, false)
            )
            ARNotesTheme (
                darkTheme = darkTheme,
                dynamicColor = dynamicColor
            ){
                SceneScreenContainer(
                    viewModel = sceneViewModel,
                    initialBackStack = initialBackStack,
                    onShare = {
                        shareScene()
                    },
                    onShareNote = { noteId ->
                        shareNote(noteId)
                    },
                    onBack = {
                        finish()
                    }
                )
            }
        }
    }

    private fun openRequestedScene(intent: Intent, linkedSceneId: String?): SceneState {
        val link = intent.data
        if (link != null) {
            val sceneId = linkedSceneId ?: link.pathSegments.takeIf {
                link.scheme == "https" && link.host == "arnotes.com" &&
                    it.size == 2 && it[0] == "scene"
            }?.get(1)
            if (sceneId != null && checkScene(sceneId)) {
                curSceneId = sceneId
                return SceneState.Success
            } else {
                return SceneState.Error(
                    getString(R.string.link_invalid)
                )
            }
        } else {
            val sceneId = intent.getStringExtra(SceneKeys.EXTRA_SCENE_ID)
            val name = intent.getStringExtra(SceneKeys.EXTRA_SCENE_NAME)
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
            "${SceneKeys.PREF_SCENE_PREFIX}$sceneId", MODE_PRIVATE
        )
        return storage.contains(SceneKeys.PREF_SCENE_NAME)
    }

    private fun saveSceneName() {
        getSharedPreferences(
            "${SceneKeys.PREF_SCENE_PREFIX}$curSceneId", MODE_PRIVATE
        )
            .edit {
                putString(
                    SceneKeys.PREF_SCENE_NAME, sceneName
                )
            }
    }


    private fun saveScene() {
        val state = sceneViewModel.uiState.value
        if (state.result !is SceneResult.Content && state.result != SceneResult.Empty) {
            return
        }
        saveNotes(sceneViewModel.currentNotes())
        Log.d(TAG, getString(R.string.scene_saved))
    }

    private fun saveNotes(notes: List<Note>) {
        val storage = getSharedPreferences("${SceneKeys.PREF_SCENE_PREFIX}$curSceneId", MODE_PRIVATE)
        val oldIds = storage.getString(SceneKeys.PREF_NOTE_IDS, "").orEmpty()
            .split(",")
            .filter { it.isNotEmpty() }
        val newIds = notes.map { it.id }

        storage.edit {
            putString(SceneKeys.PREF_NOTE_IDS, newIds.joinToString(","))
            for (note in notes) {
                putString("${SceneKeys.PREF_NOTE_TEXT_PREFIX}${note.id}", note.text)
            }
            for (id in oldIds) {
                if (id !in newIds) {
                    remove("${SceneKeys.PREF_NOTE_TEXT_PREFIX}$id")
                }
            }
        }
    }

    private fun loadScene() {
        val storage = getSharedPreferences("${SceneKeys.PREF_SCENE_PREFIX}$curSceneId", MODE_PRIVATE)
        sceneName = storage.getString(
            SceneKeys.PREF_SCENE_NAME, ""
        ) ?: ""
        val savedIds = storage.getString(SceneKeys.PREF_NOTE_IDS, null)
        if (savedIds == null) {
            val legacyNotes = storage.getStringSet(SceneKeys.PREF_NOTES, emptySet()).orEmpty()
            notes.addAll(legacyNotes.map { Note(UUID.randomUUID().toString(), it) })
            if (notes.isNotEmpty()) {
                saveNotes(notes)
            }
            return
        }

        for (id in savedIds.split(",")) {
            if (id.isEmpty()) continue
            val text = checkNotNull(
                storage.getString("${SceneKeys.PREF_NOTE_TEXT_PREFIX}$id", null)
            )
            notes.add(Note(id, text))
        }
    }

    private fun shareScene() {
        val link = "${SceneKeys.SCENE_LINK_PREFIX}$curSceneId"
        shareLink(link, getString(R.string.share_scene))
    }

    private fun shareNote(noteId: String) {
        saveScene()
        val sceneId = Uri.encode(curSceneId)
        val link = "${SceneKeys.SCENE_LINK_PREFIX}$sceneId/note/${Uri.encode(noteId)}"
        shareLink(link)
    }

    private fun shareLink(link: String, title: String? = null) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = SceneKeys.SHARE_MIME_TYPE
            putExtra(
                Intent.EXTRA_TEXT,
                link
            )
        }
        startActivity(Intent.createChooser(shareIntent, title))
    }


    override fun onStart() {
        super.onStart()
        Log.d(TAG, LOG_ON_START)
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, LOG_ON_RESUME)
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, LOG_ON_PAUSE)
    }

    override fun onStop() {
        super.onStop()
        if (curSceneId.isNotEmpty()) saveScene()
        Log.d(TAG, LOG_ON_STOP)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, LOG_ON_DESTROY)
    }
}
