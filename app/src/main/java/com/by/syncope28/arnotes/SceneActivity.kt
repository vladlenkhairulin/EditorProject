package com.by.syncope28.arnotes

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.by.syncope28.arnotes.ui.theme.ARNotesTheme
import androidx.core.content.edit

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
    @Composable
    private fun SceneScreen() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Text(
                text = if (sceneExists) sceneName else stringResource(R.string.error_cannot_open),
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(msg)
            if (!sceneExists) {
                Spacer(modifier = Modifier.height(20.dp))
                Text(stringResource(R.string.cannot_load_scene))
                return
            }
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedTextField(
                value = draft,
                onValueChange = { draft = it },
                label = { Text(stringResource(R.string.note_text)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                if (draft.isNotBlank()) {
                    notes.add(draft)
                    draft = ""
                }
            }
            ) {
                Text(stringResource(R.string.add_note))
            }
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(modifier = Modifier.weight(1f)) {
                itemsIndexed(notes) { index, note ->
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = note, modifier = Modifier.weight(1f))
                        TextButton(onClick = {
                            notes.removeAt(index)
                        })
                        {
                            Text(stringResource(R.string.delete))
                        }
                    }
                }
            }
            Button(onClick = {
                saveScene()
                msg = getString(R.string.scene_saved)
            }, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.save_scene))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { shareScene() }, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.share_scene))
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