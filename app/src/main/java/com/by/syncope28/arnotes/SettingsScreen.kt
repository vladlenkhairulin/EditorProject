package com.by.syncope28.arnotes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.by.syncope28.arnotes.ui.theme.AppSpacing

@Composable
fun SettingsScreen(
    darkTheme: Boolean,
    dynamicColor: Boolean,
    onThemeSelected: (Boolean) -> Unit,
    onColorSelected: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState())
            .padding(AppSpacing.Large)
    ) {
        Text(stringResource(R.string.settings), style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(AppSpacing.Medium))
        SettingsSwitch(
            checked = darkTheme,
            offLabel = R.string.light_theme,
            onLabel = R.string.dark_theme,
            onCheckedChange = onThemeSelected
        )
        SettingsSwitch(
            checked = dynamicColor,
            offLabel = R.string.standard_colors,
            onLabel = R.string.dynamic_colors,
            onCheckedChange = onColorSelected,
        )
    }
}

@Composable
private fun SettingsSwitch(
    checked: Boolean,
    offLabel: Int,
    onLabel: Int,
    onCheckedChange: (Boolean) -> Unit
) {
    val label = if (checked) onLabel else offLabel
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(stringResource(label), modifier = Modifier.weight(1f))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
