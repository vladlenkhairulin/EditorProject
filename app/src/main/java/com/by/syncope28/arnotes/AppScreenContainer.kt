package com.by.syncope28.arnotes

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle

private enum class AppSection {
    Home,
    Settings,
    Profile
}

@Composable
fun AppScreenContainer(
    viewModel: AppViewModel,
    openProfile: Boolean = false,
    onShareProfile: (String) -> Unit,
    home: @Composable () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val systemDarkTheme = isSystemInDarkTheme()
    var selected by rememberSaveable {
        mutableStateOf(if (openProfile) AppSection.Profile else AppSection.Home)
    }
    val sectionState = rememberSaveableStateHolder()

    BackHandler(enabled = selected != AppSection.Home) {
        viewModel.closeProfileLink()
        selected = AppSection.Home
    }

    Scaffold(
        modifier = Modifier.imePadding(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            NavigationBar {
                val titles = listOf(R.string.my_notes, R.string.settings, R.string.user_profile)
                val icons = listOf(Icons.Default.Home, Icons.Default.Settings, Icons.Default.Person)
                AppSection.entries.forEachIndexed { index, destination ->
                    val isThisTabSelected = selected == destination
                    NavigationBarItem(
                        selected = isThisTabSelected,
                        onClick = {
                            if (selected != destination) {
                                viewModel.closeProfileLink()
                                selected = destination
                            }
                        },
                        icon = { Icon(icons[index], contentDescription = null) },
                        label = { Text(stringResource(titles[index])) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        ) {
            when (selected) {
                AppSection.Home -> sectionState.SaveableStateProvider(AppSection.Home.name) {
                    home()
                }
                AppSection.Settings -> SettingsScreen(
                    darkTheme = state.darkTheme ?: systemDarkTheme,
                    dynamicColor = state.dynamicColor,
                    onThemeSelected = { viewModel.changeDarkTheme(it, systemDarkTheme) },
                    onColorSelected = viewModel::changeDynamicColor
                )
                AppSection.Profile -> ProfileScreen(
                    state = state,
                    onNicknameChange = viewModel::changeNickname,
                    onSave = viewModel::saveProfile,
                    onShare = { onShareProfile(viewModel.profileLink()) }
                )
            }
        }
    }
}
