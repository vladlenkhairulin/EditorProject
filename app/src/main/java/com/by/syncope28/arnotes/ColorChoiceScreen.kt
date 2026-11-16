package com.by.syncope28.arnotes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.by.syncope28.arnotes.ui.theme.AppSpacing

@Composable
fun ColorChoiceScreen(onColorSelected: (Boolean) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.choose_colors),
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(AppSpacing.Medium))
            Button(onClick = {
                onColorSelected(false)
            }) {
                Text(stringResource(R.string.standard_colors))
            }
            Spacer(modifier = Modifier.height(AppSpacing.Small))
            Button(onClick = {
                onColorSelected(true)
            }) {
                Text(stringResource(R.string.dynamic_colors))
            }
        }
    }
}
