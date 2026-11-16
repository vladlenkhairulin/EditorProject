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
import androidx.core.content.edit
import java.util.UUID

class MainActivity : ComponentActivity() {
    private var sceneName by mutableStateOf("")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val appearance = getSharedPreferences(SceneKeys.PREF_APPEARANCE, MODE_PRIVATE)
        setContent {
            var selectedDarkTheme by rememberSaveable {
                mutableStateOf<Boolean?>(null)
            }
            var selectedDynamicColor by rememberSaveable {
                mutableStateOf<Boolean?>(null)
            }
            val systemDarkTheme = isSystemInDarkTheme()
            val darkTheme = selectedDarkTheme ?: systemDarkTheme
            val dynamicColor = selectedDynamicColor ?: false
            ARNotesTheme(
                darkTheme = darkTheme,
                dynamicColor = dynamicColor
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
                                appearance.edit {
                                    putBoolean(SceneKeys.PREF_DARK_THEME, darkTheme)
                                    putBoolean(SceneKeys.PREF_DYNAMIC_COLOR, it)
                                }
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