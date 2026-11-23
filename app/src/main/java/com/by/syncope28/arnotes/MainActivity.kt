package com.by.syncope28.arnotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.by.syncope28.arnotes.ui.theme.ARNotesTheme
import android.content.Intent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import java.util.UUID

class MainActivity : ComponentActivity() {
    private var sceneName by mutableStateOf("")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var selectedDarkTheme by rememberSaveable {
                mutableStateOf<Boolean?>(null)
            }
            var selectedDynamicColor by rememberSaveable {
                mutableStateOf<Boolean?>(null)
            }
            val systemDarkTheme = isSystemInDarkTheme()
            ARNotesTheme(
                darkTheme = selectedDarkTheme ?: systemDarkTheme,
                dynamicColor = selectedDynamicColor ?: false
            ) {
                when {
                    selectedDarkTheme == null -> {
                        ThemeChoiceScreen(
                            onThemeSelected = {
                                selectedDarkTheme = it
                            }
                        )
                    }

                    selectedDynamicColor == null -> {
                        ColorChoiceScreen(
                            onColorSelected = {
                                selectedDynamicColor = it
                            }
                        )
                    }

                    else -> {
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
                                    intent.putExtra(SceneKeys.EXTRA_DARK_THEME, selectedDarkTheme)
                                    intent.putExtra(SceneKeys.EXTRA_DYNAMIC_COLOR, selectedDynamicColor)
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