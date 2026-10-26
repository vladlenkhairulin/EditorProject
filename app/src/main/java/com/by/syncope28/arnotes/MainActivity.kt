package com.by.syncope28.arnotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.by.syncope28.arnotes.ui.theme.ARNotesTheme
import androidx.compose.ui.Alignment
import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import java.util.UUID

class MainActivity : ComponentActivity() {
    private var sceneName by mutableStateOf("")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ARNotesTheme {
                MainScreen(sceneName = sceneName, onSceneNameChange = {
                    sceneName = it
                }, onCreateScene = {
                    if (sceneName != "") {
                        val sceneId = UUID.randomUUID().toString()
                        val intent = Intent(
                            this, SceneActivity::class.java
                        )
                        intent.putExtra(
                            "SCENE_NAME", sceneName
                        )
                        intent.putExtra(
                            "SCENE_ID", sceneId
                        )
                        startActivity(intent)
                    }
                })
            }
        }
    }
}

@Composable
fun MainScreen(sceneName : String, onSceneNameChange: (String) -> Unit, onCreateScene: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally) {
            OutlinedTextField(
                value = sceneName,
                onValueChange = {
                    onSceneNameChange(it)
                }, label = {
                    Text(stringResource(R.string.scene_name))
                }
            )
            Spacer(
                modifier = Modifier.height(8.dp)
            )
            Button(
                onClick = onCreateScene
            ) {
                Text(stringResource(R.string.create_scene))
            }
        }
    }
}
