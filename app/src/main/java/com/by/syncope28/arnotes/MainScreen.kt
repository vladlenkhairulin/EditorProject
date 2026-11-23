package com.by.syncope28.arnotes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.by.syncope28.arnotes.ui.theme.AppSpacing

@Composable
fun MainScreen(
    sceneName: String,
    onSceneNameChange: (String) -> Unit,
    onCreateScene: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = sceneName,
                onValueChange = {
                    onSceneNameChange(it)
                }, label = {
                    Text(stringResource(R.string.scene_name))
                }
            )
            Spacer(
                modifier = Modifier.height(AppSpacing.Small)
            )
            Button(
                onClick = onCreateScene
            ) {
                Text(stringResource(R.string.create_scene))
            }
        }
    }
}
