package com.by.syncope28.arnotes

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.by.syncope28.arnotes.ui.theme.AppSpacing

@Composable
fun ProfileScreen(
    state: AppUiState,
    onNicknameChange: (String) -> Unit,
    onSave: () -> Unit,
    onShare: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState())
            .padding(AppSpacing.Large)
    ) {
        Text(stringResource(R.string.user_profile), style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(AppSpacing.Medium))
        Box(
            modifier = Modifier
                .size(96.dp)
                .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
        )
        Spacer(modifier = Modifier.height(AppSpacing.Medium))

        if (state.profileFromLink) {
            Text(state.nickname)
        } else {
            OutlinedTextField(
                value = state.nickname,
                onValueChange = onNicknameChange,
                label = { Text(stringResource(R.string.nickname)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Row {
                Button(
                    onClick = onSave,
                    enabled = state.nickname.isNotBlank()
                ) {
                    Text(stringResource(R.string.save_profile))
                }
                TextButton(
                    onClick = onShare,
                    enabled = state.nickname.isNotBlank()
                ) {
                    Text(stringResource(R.string.share_profile))
                }
            }
        }
        state.profileMessage?.let { Text(stringResource(it)) }
    }
}
