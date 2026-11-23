package com.by.syncope28.arnotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.by.syncope28.arnotes.ui.theme.ARNotesTheme
import android.content.Intent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import java.util.UUID

class MainActivity : ComponentActivity() {
    private val appViewModel: AppViewModel by viewModels()
    private var sceneName by mutableStateOf("")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appViewModel.loadSettings(this)
        enableEdgeToEdge()
        val openProfile = intent.action == Intent.ACTION_VIEW && intent.data?.path == SceneKeys.PROFILE_PATH
        val uri = intent.data
        if (openProfile && savedInstanceState == null && uri != null) {
            appViewModel.openProfileLink(uri)
        }
        setContent {
            val appState by appViewModel.uiState.collectAsStateWithLifecycle()
            var selectedDynamicColor by rememberSaveable {
                mutableStateOf<Boolean?>(null)
            }
            val systemDarkTheme = isSystemInDarkTheme()
            LaunchedEffect(systemDarkTheme) {
                appViewModel.refreshSettings(systemDarkTheme)
            }
            val darkTheme = appState.darkTheme ?: systemDarkTheme
            val dynamicColor = appState.dynamicColor
            ARNotesTheme(
                darkTheme = darkTheme,
                dynamicColor = dynamicColor
            ) {
                when {
                    !openProfile && selectedDynamicColor == null -> {
                        ColorChoiceScreen(
                            onColorSelected = {
                                selectedDynamicColor = it
                                appViewModel.changeDynamicColor(it)
                            }
                        )
                    }

                    else -> {
                        AppScreenContainer(
                            viewModel = appViewModel,
                            openProfile = openProfile,
                            onShareProfile = { link ->
                                shareProfile(link)
                            }
                        ) {
                            MainScreen(
                                sceneName = sceneName,
                                onSceneNameChange = {
                                    sceneName = it
                                },
                                onCreateScene = {
                                    if (sceneName != "") {
                                        val sceneId = UUID.randomUUID().toString()
                                        val intent = Intent(
                                            this, SceneActivity::class.java
                                        )
                                        intent.putExtra(SceneKeys.EXTRA_SCENE_NAME, sceneName)
                                        intent.putExtra(SceneKeys.EXTRA_SCENE_ID, sceneId)
                                        intent.putExtra(SceneKeys.EXTRA_DARK_THEME, darkTheme)
                                        intent.putExtra(SceneKeys.EXTRA_DYNAMIC_COLOR, dynamicColor)
                                        startActivity(intent)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        appViewModel.refreshSettings()
    }

    private fun shareProfile(link: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = SceneKeys.SHARE_MIME_TYPE
            putExtra(Intent.EXTRA_TEXT, link)
        }
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_profile)))
    }
}