package com.by.syncope28.arnotes

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import com.by.syncope28.arnotes.ui.theme.ARNotesTheme

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

    private val notes = mutableListOf<String>()
    private var curSceneId = ""
    private var sceneName by mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, LOG_ON_CREATE)
        val requestedSceneState = openRequestedScene(intent)
        if (requestedSceneState == SceneState.Success) {
            val savedNotes = savedInstanceState?.getStringArrayList(SceneKeys.PREF_NOTES)
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

        val darkTheme = intent.getBooleanExtra(SceneKeys.EXTRA_DARK_THEME, false)
        val dynamicColor = intent.getBooleanExtra(SceneKeys.EXTRA_DYNAMIC_COLOR, false)
        setContent {
            ARNotesTheme (
                darkTheme = darkTheme,
                dynamicColor = dynamicColor
            ){
                SceneScreenContainer(
                    viewModel = sceneViewModel,
                    onShare = {
                        shareScene()
                    },
                    onBack = {
                        finish()
                    }
                )
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
        getSharedPreferences("${SceneKeys.PREF_SCENE_PREFIX}$curSceneId", MODE_PRIVATE)
            .edit {
                putString(
                    SceneKeys.PREF_SCENE_NAME, state.sceneName
                )
                putStringSet(
                    SceneKeys.PREF_NOTES, sceneViewModel.currentNotes().toSet()
                )
            }
        Log.d(TAG, getString(R.string.scene_saved))
    }

    private fun loadScene() {
        val storage = getSharedPreferences("${SceneKeys.PREF_SCENE_PREFIX}$curSceneId", MODE_PRIVATE)
        sceneName = storage.getString(
            SceneKeys.PREF_SCENE_NAME, ""
        ) ?: ""
        val savedNotes = storage.getStringSet(
            SceneKeys.PREF_NOTES, emptySet()
        )
        if (savedNotes != null) {
            notes.addAll(savedNotes)
        }
    }

    private fun shareScene() {
        val link = "${SceneKeys.SCENE_LINK_PREFIX}$curSceneId"
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = SceneKeys.SHARE_MIME_TYPE
            putExtra(
                Intent.EXTRA_TEXT,
                link
            )
        }
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_scene)))
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
